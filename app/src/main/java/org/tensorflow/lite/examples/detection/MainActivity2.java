package org.tensorflow.lite.examples.detection;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.here.sdk.core.Anchor2D;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.Location;
import com.here.sdk.mapview.MapImage;
import com.here.sdk.mapview.MapImageFactory;
import com.here.sdk.mapview.MapMarker;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {

    private Button addOnMap;
    private static final String TAG = MainActivity2.class.getSimpleName();
    private here.PermissionsRequestor permissionsRequestor;
   // private LocationManager locationManager;
    public MapView mapView ;
    //private android.location.Location nativeLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    public MainActivity2() {
    }

    public MapView getMapView(){return mapView;}
    /*public void addMapMarker() {
        MapImage mapImage = MapImageFactory.fromResource(this.getResources(),R.drawable.poi);
        Anchor2D anchor2D = new Anchor2D(0.5f,1.0f);
        MapMarker mapMarker = new MapMarker(new GeoCoordinates(40.70055,-74.0086), mapImage);
        MapView mapView = this.getMapView();
        mapView.getMapScene().addMapMarker(mapMarker);
    }*/

    private Location convertLocation(android.location.Location nativeLocation) {
        GeoCoordinates geoCoordinates = new GeoCoordinates(
                nativeLocation.getLatitude(),
                nativeLocation.getLongitude(),
                nativeLocation.getAltitude());

        Location location = new Location(geoCoordinates, new Date());

        if (nativeLocation.hasBearing()) {
            location.bearingInDegrees = (double) nativeLocation.getBearing();
        }

        if (nativeLocation.hasSpeed()) {
            location.speedInMetersPerSecond = (double) nativeLocation.getSpeed();
        }

        if (nativeLocation.hasAccuracy()) {
            location.horizontalAccuracyInMeters = (double) nativeLocation.getAccuracy();
        }

        return location;
    }

    private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<android.location.Location>() {
            @Override
            public void onComplete(@NonNull Task<android.location.Location> task) {
                android.location.Location location = task.getResult();
                if(location!=null){
                    try {
                        Geocoder geocoder = new Geocoder(MainActivity2.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(),location.getLongitude(),1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        });

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        /*********Map marker*********/
        /*PlatformPositioningProvider platformPositioningProvider = new PlatformPositioningProvider(getApplicationContext());

        platformPositioningProvider.startLocating(new PlatformPositioningProvider.PlatformLocationListener() {
            @Override
            public void onLocationUpdated(android.location.Location location) {

            }


        });*/

        addOnMap = findViewById(R.id.addOnMap);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        addOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                    fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<android.location.Location>() {
                        @Override
                        public void onComplete(@NonNull Task<android.location.Location> task) {
                            android.location.Location location = task.getResult();
                            if(location!=null){
                                try {
                                    Geocoder geocoder = new Geocoder(MainActivity2.this, Locale.getDefault());
                                    List<Address> addresses = geocoder.getFromLocation(
                                            location.getLatitude(),location.getLongitude(),1);
                                    MapImage mapImage = MapImageFactory.fromResource(getResources(),R.drawable.poi);
                                    Anchor2D anchor2D = new Anchor2D(0.5f,1.0f);
                                    MapMarker mapMarker = new MapMarker(new GeoCoordinates(addresses.get(0).getLatitude(),addresses.get(0).getLongitude()), mapImage);
                                    // MapMarker mapMarker = new MapMarker(new GeoCoordinates(40.70055,-74.0086), mapImage);
                                    MapView mapView = getMapView();
                                    mapView.getMapScene().addMapMarker(mapMarker);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                    });
                }
                else{
                    ActivityCompat.requestPermissions(MainActivity2.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
                }


            }

        });




        Bundle b = getIntent().getExtras();

       /* if (b != null) {
            currentLocation = new GeoCoordinates(b.getDouble("locationLatitude"), b.getDouble("locationLongitude"));
        }*/
        mapView.setOnReadyListener(new MapView.OnReadyListener() {
            @Override
            public void onMapViewReady() {
                // This will be called each time after this activity is resumed.
                // It will not be called before the first map scene was loaded.
                // Any code that requires map data may not work as expected beforehand.
                Log.d(TAG, "HERE Rendering Engine attached.");
            }
        });
//ask permission
        handleAndroidPermissions();
        loadMapScene();


        ImageButton reportButton = findViewById(R.id.reportButton);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity2.this,"It works",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                //can not pass GeoCoordinates so we pass the data to rebuild it after
                //   GeoCoordinates centerMap = mapView.getCamera().;
                //  double latitude = centerMap.latitude;
                // double longitude = centerMap.longitude;
                //  intent.putExtra("mapViewLatitude", latitude);
                //   intent.putExtra("mapViewLongitude", longitude);
                //want result: the destination
                startActivityForResult(intent,2);// Activity is started with requestCode 2;
            }
        });
    }



    private void handleAndroidPermissions() {
        permissionsRequestor = new here.PermissionsRequestor(this);
        permissionsRequestor.request(new here.PermissionsRequestor.ResultListener(){

            @Override
            public void permissionsGranted() {
                loadMapScene();
            }

            @Override
            public void permissionsDenied() {
                Log.e(TAG, "Permissions denied by user.");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsRequestor.onRequestPermissionsResult(requestCode, grantResults);
    }

    private void loadMapScene(){
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, mapError -> {
            if (mapError == null){
                mapView.getCamera().lookAt(new GeoCoordinates(52.5,13.3,10000));
            }else{
                Log.d(TAG, "Loading map failed: mapError: " + mapError.name());
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

}