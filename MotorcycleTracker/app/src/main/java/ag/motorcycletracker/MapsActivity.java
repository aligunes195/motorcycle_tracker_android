package ag.motorcycletracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double lat;
    private double lng;
    private Marker marker;
    private boolean isMapReady;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        isMapReady = false;
        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                updateLocation();
            }
        },0,500);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        lat = -34;
        lng = 151;
        LatLng motorLoc = new LatLng(lat, lng);
        marker = mMap.addMarker(new MarkerOptions().position(motorLoc).title("Your motorcycle location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(motorLoc));
        isMapReady = true;
    }

    public void updateLocation() {
        if (!isMapReady)
            return;
        lat += 0.0001;
        lng += 0.0001;
        Log.d("tagA", "Update latlng : " + lat + ", " + lng);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                marker.setPosition(new LatLng(lat, lng));
            }
        });
    }
}
