package com.egeperk.mytravelbook.view;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.egeperk.mytravelbook.R;
import com.egeperk.mytravelbook.roomdb.LocationDao;
import com.egeperk.mytravelbook.roomdb.LocationDatabase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.egeperk.mytravelbook.databinding.ActivityMapsBinding;
import com.google.android.material.snackbar.Snackbar;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    ActivityResultLauncher<String> permissionLauncher;

    LocationManager locationManager;

    LocationListener locationListener;

    SharedPreferences sharedPreferences;

    boolean info;

    LocationDatabase db;

    LocationDao locationDao;

    Double selectedLatitude;

    Double selectedLongitude;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    com.egeperk.mytravelbook.Location selectedLocation;

    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        registerLaunch();

        sharedPreferences = this.getSharedPreferences("com.egeperk.mytravelbook",MODE_PRIVATE);

        info = false;

        selectedLatitude = 0.0;
        selectedLongitude = 0.0;

        db = Room.databaseBuilder(getApplicationContext(),LocationDatabase.class,"Locations").build();
        locationDao = db.locationDao();

        binding.saveButton.setEnabled(false);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        Intent intent= getIntent();
        String intentInfo = intent.getStringExtra("info");

        if(intentInfo.equals("new")) {
            binding.saveButton.setVisibility(View.VISIBLE);
            binding.deleteButton.setVisibility(View.GONE);


            // casting = (......) getSystem...
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {

                    info = sharedPreferences.getBoolean("info",false);

                    if(!info) {
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
                        sharedPreferences.edit().putBoolean("info", true).apply();
                    }
                }

            };

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // request permission
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Snackbar.make(binding.getRoot(), "Permission needed for Maps", Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                        }
                    }).show();
                } else {
                    // request permission
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

                }
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                // Ekranda son lokasyon dursun yeni lokasyon yüklenene kadar
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(lastLocation != null) {

                    LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));
                }
                mMap.setMyLocationEnabled(true);


        }





    } else {
            mMap.clear();
             selectedLocation = (com.egeperk.mytravelbook.Location) intent.getSerializableExtra("location");

            LatLng latLng = new LatLng(selectedLocation.latitude, selectedLocation.longitude);

            mMap.addMarker(new MarkerOptions().position(latLng).title(selectedLocation.name));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
            binding.locationText.setText(selectedLocation.name);
            binding.saveButton.setVisibility(View.GONE);
            binding.deleteButton.setVisibility(View.VISIBLE);


        }


        // Latitude - enlem / Longitude - boylam
        //LatLng - enlemle boylamın bir arada çalışmasını sağlayan obje

        // Lokasyon belirleme
        //LatLng myLocation = new LatLng(38.3998679, 27.0889058);

        // App'te konumun başlayacağı bölge ve zoom ayarı
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));

        //Gösterdiği lokasyona marker ekleyip marker a title ekleme
       // mMap.addMarker(new MarkerOptions().position(myLocation).title("Şu an buradasın!"));
    }

    private void registerLaunch() {
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    // permission granted
                    if(ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED); {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                    }
                    }
                else {
                    // permission denied
                    Toast.makeText(MapsActivity.this, "Permission needed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));

        selectedLatitude = latLng.latitude;
        selectedLongitude = latLng.longitude;


        binding.saveButton.setEnabled(true);

    }
    public void save(View view) {

        com.egeperk.mytravelbook.Location location = new com.egeperk.mytravelbook.Location(binding.locationText.getText().toString(),selectedLatitude,selectedLongitude);


        // threading - Main (UI) kullanıcı arayüzü burayı çok kullanma, Default( CPU Intensive) İşlemcileri yorabilecek işlemler, IO Network ve veri operasyonları (İnternetten veri isteme vs)

        // disposable - RX te kullandıkların kullanılıp atılabilir.


        compositeDisposable.add(locationDao.insert(location).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(MapsActivity.this::handleResponse));
    }

    private void handleResponse() {

        Intent intent = new Intent(MapsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void delete(View view) {
        super.onDestroy();

        if (selectedLocation !=null) {
            compositeDisposable.add(locationDao.delete(selectedLocation).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(MapsActivity.this::handleResponse));
        }


    }
}