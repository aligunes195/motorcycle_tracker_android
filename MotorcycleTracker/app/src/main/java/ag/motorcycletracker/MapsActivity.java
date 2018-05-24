package ag.motorcycletracker;

import android.content.Intent;
import android.os.AsyncTask;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final static String webpage = "https://project-bff.eu-gb.mybluemix.net/bff";

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
        new GetLatLngTask().execute();
        Log.d("tagA", "Update latlng : " + lat + ", " + lng);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                marker.setPosition(new LatLng(lat, lng));
            }
        });
    }

    public String getHttpData() {
        BufferedReader in = null;
        String data = "";
        try
        {
            HttpClient client = new DefaultHttpClient();
            URI website = new URI(webpage);
            HttpGet request = new HttpGet();
            request.setURI(website);
            HttpResponse response = client.execute(request);
            response.getStatusLine().getStatusCode();

            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String l = "";
            String nl = System.getProperty("line.separator");
            while ((l = in.readLine()) !=null){
                sb.append(l + nl);
            }
            in.close();
            data = sb.toString();
        }
        catch (Exception e)
        {
            Log.d("HttpException", e.getMessage());
        }
        finally
        {
            if (in != null){
                try{
                    in.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    private class GetLatLngTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return getHttpData();
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(final String data) {
            // Burası düzenlenmeli !!!
            lat += 0.0001;
            lng += 0.0001;
            Log.d("Data", data);
        }
    }
}
