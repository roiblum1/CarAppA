package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Activity;
import android.app.Notification;
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
import android.graphics.Color;
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

public class MainActivity extends BaseActivity {

    //Login Activity Contains
    //notify 4 make an notification tha background music is started
    //logIn this action make a progress bar dialog and make login in firebase and save in shared preference also start background music and move you to personal page through the Splash screen activity
    //menu that not need much explanation

    public static final String Email = "emailKey";
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Pass = "passKey";
    public static final String CHANNEL_1_ID = "channel1";
    Button btn_login;
    EditText et_mail;
    EditText et_pass;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    SharedPreferences sharedpreferences;
    TextView tv_create;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
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
            startActivity(new Intent(this, About_us.class));
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
                    NotificationForSendOnChannel1();
                    MainActivity.this.progressDialog.dismiss();
                    showToast("Login successful");
                    startService(new Intent(MainActivity.this, MyService.class));
                    MainActivity.this.intentToSplashScreen();
                    return;
                }
                MainActivity.this.progressDialog.dismiss();
                showToast("Login Failed");
            }
        });
    }

    public void intentToSplashScreen() {
        startActivity(new Intent(this, SplashScreen.class));
    }

    public boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            et_mail.setError("Email field is empty.");
            return false;
        }

        if (password.isEmpty()) {
            et_pass.setError("Password is empty.");
            return false;
        }

        return true;
    }


    public void NotificationForSendOnChannel1() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel 1");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);

            Intent activityIntent = new Intent(this, MainActivity2.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this,
                    0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE);

            Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
            broadcastIntent.putExtra("toastMessage", "Welcome back Brother");
            PendingIntent actionIntent = PendingIntent.getBroadcast(this,
                    0, broadcastIntent,PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE );


            Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.heart)
                    .setContentTitle("Background Music")
                    .setContentText("Welcome ! Music has been started")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setColor(Color.BLUE)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .addAction(R.drawable.logo4, "Start / Stop", actionIntent)
                    .build();

            manager.notify(1, notification);
        }
    }
}