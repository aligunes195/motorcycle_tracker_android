package ag.motorcycletracker;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final static String webpage = "https://project-bff.eu-gb.mybluemix.net/001/gpsValues";

    private GoogleMap mMap;
    private double lat;
    private double lng;
    private Marker marker;
    private boolean isMapReady;
    private boolean locFirstRead;
    private boolean atBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        isMapReady = false;
        locFirstRead = true;
        atBackground = false;
        initLocation();
        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                updateLocation();
            }
        },0,3000);
    }

    @Override
    public void onResume() {
        super.onResume();
        locFirstRead = true;
        atBackground = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        atBackground = true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        isMapReady = true;
    }

    public void updateLocation() {
        if (!isMapReady || atBackground)
            return;
        new GetLatLngTask().execute();
    }

    public void initLocation() {
        new GetLatLngTask().execute();
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
            List<String> gpsValues = Arrays.asList(data.split(","));
            lat = Double.parseDouble(gpsValues.get(0));
            lng = Double.parseDouble(gpsValues.get(1));
            if (locFirstRead && isMapReady) {
                locFirstRead = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LatLng motorLoc = new LatLng(lat, lng);
                        marker = mMap.addMarker(new MarkerOptions().position(motorLoc).title("Your motorcycle location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(motorLoc));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(motorLoc, 12.0f));
                    }
                });
            } else if (!locFirstRead) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        marker.setPosition(new LatLng(lat, lng));
                    }
                });
            }
            Log.d("Data", data);
        }
    }
}
