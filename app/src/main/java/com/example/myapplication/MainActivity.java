package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    //Login Activity Contains
    //notify 4 make an notification tha background music is started
    //logIn this action make a progress bar dialog and make login in firebase and save in shared preference also start background music and move you to personal page through the Splash screen activity
    //menu that not need much explanation

    public static final String Email = "emailKey";
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Pass = "passKey";
    Button btn_login;
    EditText et_mail;
    EditText et_pass;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    SharedPreferences sharedpreferences;
    TextView tv_create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        this.tv_create = (TextView) findViewById(R.id.tv_create);
        this.et_mail = (EditText) findViewById(R.id.et_mail);
        this.et_pass = (EditText) findViewById(R.id.et_pass);
        this.btn_login = (Button) findViewById(R.id.btn_login);
        this.btn_login.setOnClickListener(this::onClick);
        this.tv_create.setOnClickListener(this::onClick);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedpreferences.getString(Email, null) != null)
            et_mail.setText(sharedpreferences.getString(Email, null));
        if (sharedpreferences.getString(Pass, null) != null)
            et_pass.setText(sharedpreferences.getString(Pass, null));
        mAuth = FirebaseAuth.getInstance();
        this.progressDialog = new ProgressDialog(this);

        getIntent();
    }

    public void onClick(View v) {
        if (v == this.btn_login) {
            if (!this.et_mail.getText().toString().equals("") && !this.et_pass.getText().toString().equals("")) {
                if (et_mail.getText().toString().equals("admin") && et_pass.getText().toString().equals("admin"))
                    startActivity(new Intent(this, AdminPage.class));
                else
                    logIn(this.et_mail.getText().toString(), this.et_pass.getText().toString());
            }
        } else if (v == this.tv_create) {
            startActivity(new Intent(this, Register.class));
        }
    }


    private void logIn(String mail, String pass) {
        this.progressDialog.setMessage("Verificating...");
        this.progressDialog.show();
        this.mAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener((Activity) this, new OnCompleteListener<AuthResult>() {
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser currentUser = MainActivity.this.mAuth.getCurrentUser();
                    SharedPreferences.Editor editor = MainActivity.this.sharedpreferences.edit();
                    editor.putString(MainActivity.Pass, MainActivity.this.et_pass.getText().toString());
                    editor.putString(MainActivity.Email, MainActivity.this.et_mail.getText().toString());
                    editor.commit();
                    notify4();
                    MainActivity.this.progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Login Succesful", Toast.LENGTH_SHORT).show();
                    startService(new Intent(MainActivity.this, MyService.class));
                    MainActivity.this.intent();
                    return;
                }
                MainActivity.this.progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void intent() {
        startActivity(new Intent(this, SplashScreen.class));
    }

    public boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            this.et_mail.setError("Email field is empty.");
            if (password.isEmpty()) {
                this.et_pass.setError("Password is empty.");
            }
            return false;
        } else if (!password.isEmpty()) {
            return true;
        } else {
            this.et_pass.setError("Password is empty.");
            if (email.isEmpty()) {
                this.et_pass.setError("Email field is empty.");
            }
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void notify4() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        String message = "Music has been started";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "My Notification");
        builder.setContentTitle("Background Music");
        builder.setContentText(message);
        builder.setSmallIcon(R.drawable.icon_mail);
        builder.setAutoCancel(true);

        Intent intent = new Intent(MainActivity.this, PersonalPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("message",message);
        PendingIntent pendingIntent=PendingIntent.getActivity(MainActivity.this,0, Intent.makeMainActivity(startService(intent)),PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE );
        builder.setContentIntent(pendingIntent);


        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        managerCompat.notify(1, builder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menua, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.LoginPage:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.Register:
                startActivity(new Intent(this, Register.class));
                return true;
            case R.id.AllAD:
                if (FirebaseAuth.getInstance().getCurrentUser()!=null)
                    startActivity(new Intent(this, AllAdActivity.class));
                return true;
            case R.id.personal_page:
                if (FirebaseAuth.getInstance().getCurrentUser()!=null)
                    startActivity(new Intent(this, PersonalPage.class));
                return true;
            case R.id.View_Profile:
                if (FirebaseAuth.getInstance().getCurrentUser()!=null)
                    startActivity(new Intent(this, MainActivity2.class));
                return true;
            case R.id.SrartMusic:
                startService(new Intent(this, MyService.class));
                return true;
            case R.id.StopMusic:
                stopService(new Intent(this, MyService.class));
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void action ()
    {
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
    }



}