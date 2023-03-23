package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.android.gms.common.util.MapUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;



public class ViewLocation extends AppCompatActivity{
    private TextView title;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location);
        title = (TextView) findViewById(R.id.title);
        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl("https://www.latlong.net/c/?lat=31.809257&long=34.787083");

    }

//    @Override
//    public void onMapReady(@NonNull GoogleMap googleMap) {
//        MapsInitializer.initialize(this);
//
//        final ViewOption viewOption = getViewOption();
//        MapUtils.showElements(viewOption, googleMap, this);
//        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                Bundle args = new Bundle();
//                args.putParcelable("optionView", viewOption);
//                Intent intent = new Intent(ViewLocation.this, MainActivity.class);
//                intent.putExtra("args", args);
//                startActivity(intent);
//            }
//        });
//    }
//
//    @Override
//    public void onPointerCaptureChanged(boolean hasCapture) {
//        super.onPointerCaptureChanged(hasCapture);
//    }
//
//
//    public static ViewOption getViewOption() {
//        return new ViewOptionBuilder()
//                .withStyleName(ViewOption.StyleDef.RETRO)
//                .withCenterCoordinates(new LatLng(35.6892, 51.3890))
//                .withMarkers(AppUtils.getListExtraMarker())
//                .withPolygons(
//                        AppUtils.getPolygon_1()
//                )
//                .withPolylines(
//                        AppUtils.getPolyline_2(),
//                        AppUtils.getPolyline_4()
//                )
//                .withForceCenterMap(false)
//                .build();
//
//    }
}