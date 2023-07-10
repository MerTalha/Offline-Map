package com.example.offlinemap;


import static android.hardware.SensorManager.getAltitude;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;


import com.example.offlinemap.databinding.ActivityMainBinding;


import java.util.Arrays;
import java.util.List;

import egolabsapps.basicodemine.offlinemap.Interfaces.MapListener;
import egolabsapps.basicodemine.offlinemap.Utils.MapUtils;
import egolabsapps.basicodemine.offlinemap.Views.OfflineMapView;

public class MainActivity extends AppCompatActivity implements MapListener, MapEventsReceiver {

    OfflineMapView offlineMapView;

    private ActivityMainBinding binding;

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        offlineMapView = findViewById(R.id.map);

        offlineMapView.init(this, this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()){

            }else{
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }



    }

    @SuppressLint("ClickableViewAccessibility")
    public void mapLoadSuccess(MapView mapView, MapUtils mapUtils) {

        offlineMapView.setInitialPositionAndZoom(new GeoPoint(39.925533, 32.866287), 30);
        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(39.925533, 32.866287));
        marker.setTitle("Hello Istanbul");
        marker.showInfoWindow();

        mapView.getOverlays().add(marker);
        mapView.invalidate();

        mapView.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(MotionEvent e) {
                    // Uzun dokunma işlemi gerçekleştiğinde yapılacak işlemleri buraya yazın
                    // ...

                    // Dokunulan noktanın koordinatlarını al
                    GeoPoint touchedPoint = (GeoPoint) mapView.getProjection().fromPixels((int) e.getX(), (int) e.getY());

                    // Marker oluştur ve ayarla
                    Marker marker = new Marker(mapView);
                    marker.setPosition(touchedPoint);
                    marker.setTitle("Touched Location");
                    marker.showInfoWindow();

                    // Marker'ı haritaya ekle
                    List<Overlay> overlays = mapView.getOverlays();
                    overlays.clear(); // Eski markerları temizle
                    overlays.add(marker);

                    mapView.invalidate();

                    // Koordinatları kullanmak için burada yapılacak işlemleri gerçekleştirin
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    // Tek dokunma işlemi gerçekleştiğinde yapılacak işlemleri buraya yazın
                    // ...

                    // Dokunmanın tüketilmediğini ve geri dönmesi gerektiğini belirtin
                    return false;
                }
            });

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // Dokunma olayını GestureDetector'a yönlendirin
                boolean gestureResult = gestureDetector.onTouchEvent(motionEvent);

                // Dokunmanın tüketilmediğini ve geri dönmesi gerektiğini belirtin
                return gestureResult;
            }
        });








        //////////////////////////////////////////////////////////////////////////////////
        /*mapView.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(MotionEvent e) {
                    // Uzun dokunma işlemi gerçekleştiğinde yapılacak işlemleri buraya yazın

                    // Dokunulan noktanın koordinatlarını al
                    GeoPoint touchedPoint = (GeoPoint) mapView.getProjection().fromPixels((int) e.getX(), (int) e.getY());

                    // Marker oluştur ve ayarla
                    Marker marker = new Marker(mapView);
                    marker.setPosition(touchedPoint);
                    marker.setTitle("Touched Location");
                    marker.showInfoWindow();

                    // Marker'ı haritaya ekle
                    mapView.getOverlays().add(marker);
                    mapView.invalidate();


                    final StringBuilder msg = new StringBuilder();
                    final double lon = (touchedPoint.getLongitude() / 1E6) * 1000000;
                    final double lat = (touchedPoint.getLatitude() / 1E6) * 1000000;
                    final double alt = getAltitude((float) lon, (float) lat);
                    msg.append("Lon: ");
                    msg.append(lon);
                    msg.append(" Lat: ");
                    msg.append(lat);
                    msg.append(" Alt: ");
                    msg.append(alt);
                    Log.d("tag", String.valueOf(lon) + " " + String.valueOf(lat));

                    // Koordinatları kullanmak için burada yapılacak işlemleri gerçekleştirin
                }

            });
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return true;
            }


        });*/

        //on durumunda hem marker koyuyor hem de hareket ediyor. off durumund sadece hareket ediyor
        /*markerButton.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                // Toggle düğmesi açık durumdayken marker koyması gerekiyor
                mapView.setOnTouchListener((v, event) -> {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        // Dokunulan noktanın koordinatlarını al
                        GeoPoint touchedPoint = (GeoPoint) mapView.getProjection().fromPixels((int) event.getX(),
                                (int) event.getY());


                        // Marker oluştur ve ayarla
                        Marker marker1 = new Marker(mapView);
                        marker1.setPosition(touchedPoint);
                        marker1.setIcon(getResources().getDrawable(R.drawable.bonuspack_bubble));
                        marker1.setTitle("Touched Location");
                        marker1.showInfoWindow();

                        // Marker'ı haritaya ekle
                        mapView.getOverlays().add(marker1);
                        mapView.invalidate();
                        //marker1.getPosition().getAltitude();
                        final StringBuilder msg = new StringBuilder();
                        final double lon = (touchedPoint.getLongitude() / 1E6) * 1000000;
                        final double lat = (touchedPoint.getLatitude() / 1E6) * 1000000;
                        final double alt = getAltitude((float) lon, (float) lat);
                        msg.append("Lon: ");
                        msg.append(lon);
                        msg.append(" Lat: ");
                        msg.append(lat);
                        msg.append(" Alt: ");
                        msg.append(alt);
                        // Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT)
                        //.show();
                        Log.d("tag", String.valueOf(lon));
                        return true;
                    }

                    return false;
                });
            }
             else{
                    // Toggle düğmesi kapalı durumdayken haritada gezinme sağlanmalı
                    mapView.setOnTouchListener(null);
                }

        });*/



        /*markerSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mapView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (true) {
                            // Dokunulan noktanın koordinatlarını al
                            GeoPoint touchedPoint = (GeoPoint) mapView.getProjection().fromPixels((int) view.getX(),
                                    (int) view.getY());

                            // Marker oluştur ve ayarla
                            Marker marker1 = new Marker(mapView);
                            marker1.setPosition(touchedPoint);
                            marker1.setIcon(getResources().getDrawable(R.drawable.bonuspack_bubble));
                            marker1.setTitle("Touched Location");
                            marker1.showInfoWindow();

                            // Marker'ı haritaya ekle
                            mapView.getOverlays().add(marker1);
                            mapView.invalidate();
                            //marker1.getPosition().getAltitude();
                    /*final StringBuilder msg = new StringBuilder();
                    final double lon = (touchedPoint.getLongitude() / 1E6) * 1000000;
                    final double lat = (touchedPoint.getLatitude() / 1E6) * 1000000;
                    final double alt = getAltitude((float) lon, (float) lat);
                    msg.append("Lon: ");
                    msg.append(lon);
                    msg.append(" Lat: ");
                    msg.append(lat);
                    msg.append(" Alt: ");
                    msg.append(alt);
                    // Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT)
                    //.show();
                    Log.d("tag", String.valueOf(lon));
                            return true;
                        }
                        return false;
                    }
                });
            }
        });*/

    }

    public void mapLoadFailed(String ex) {
        Log.e("ex:", ex);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        permissionsToRequest.addAll(Arrays.asList(permissions).subList(0, grantResults.length));
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }

    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        return false;
    }

    public boolean longPressHelper(GeoPoint p) {
        return false;
    }
}