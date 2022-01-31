package com.egeperk.mytravelbook.view;

import static com.egeperk.mytravelbook.R.menu.location_menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.egeperk.mytravelbook.Location;
import com.egeperk.mytravelbook.R;
import com.egeperk.mytravelbook.adapter.Adapter;
import com.egeperk.mytravelbook.databinding.ActivityMainBinding;
import com.egeperk.mytravelbook.roomdb.LocationDao;
import com.egeperk.mytravelbook.roomdb.LocationDatabase;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding bindng;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    LocationDatabase db;
    LocationDao locationDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindng = ActivityMainBinding.inflate(getLayoutInflater());
        View view = bindng.getRoot();
        setContentView(view);

        LocationDatabase db = Room.databaseBuilder(getApplicationContext(),LocationDatabase.class,"Locations").build();
        locationDao = db.locationDao();

        compositeDisposable.add(locationDao.getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(MainActivity.this::handleResponse));

    }

    private void handleResponse(List<Location> locationList) {

        bindng.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Adapter locationAdapter = new Adapter(locationList);
        bindng.recyclerView.setAdapter(locationAdapter);

}


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_location) {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            intent.putExtra("info", "new");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(location_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}

