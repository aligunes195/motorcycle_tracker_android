package ag.motorcycletracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Created by aligunes on 23/05/2018.
 */

public class EmergencyActivity extends AppCompatActivity {

    TextView description;
    TextView email1;
    TextView email2;
    TextView email3;
    Button addEmails;
    CheckBox thiefModeCheckbox;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        description = (TextView) findViewById(R.id.description);
        email1 = (TextView) findViewById(R.id.email1);
        email2 = (TextView) findViewById(R.id.email2);
        email3 = (TextView) findViewById(R.id.email3);
        dialog = new ProgressDialog(this);

        thiefModeCheckbox = findViewById(R.id.checkbox_thief_mode);
        thiefModeCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox)v).isChecked();
                new SendThiefMode().execute(checked);
            }
        });

        addEmails = (Button) findViewById(R.id.add_emergency_emails);
        addEmails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EmergencyAddActivity.class);
                startActivity(intent);
            }
        });

        new GetNumber().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetNumber().execute();
    }

    public String getHttpData() {
        String urlString = "https://project-bff.eu-gb.mybluemix.net/phones";
        BufferedReader in = null;
        String data = "";
        try
        {
            HttpClient client = new DefaultHttpClient();
            URI website = new URI(urlString);
            HttpGet request = new HttpGet();
            request.setURI(website);
            HttpResponse response = client.execute(request);
            response.getStatusLine().getStatusCode();

            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String l = "";
            String nl = System.getProperty("line.separator");
            while ((l = in.readLine()) != null){
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

    public void thiefModeHttpSend(boolean thiefModeActive) {
        String urlString = "https://project-bff.eu-gb.mybluemix.net/thief";
        try
        {
            String data = "?constant=0";
            if (thiefModeActive)
                data = "?constant=1";
            HttpClient client = new DefaultHttpClient();
            URI website = new URI(urlString + data);
            HttpGet request = new HttpGet();
            request.setURI(website);
            client.execute(request);
        }
        catch (Exception e)
        {
            Log.d("HttpException", e.getMessage());
        }
    }

    private String[] renderData(String data) {
        Log.d("Data", data);
        String [] emails = {"", "", ""};
        JSONArray arr = null;
        try {
            arr = new JSONArray(data);
            for (int i = 0; i < emails.length && i < arr.length(); i++) {
                emails[i] = arr.getJSONObject(i).getString("no");
            }
        } catch (JSONException e) {
            Log.d("JsonError", e.getMessage());
        }
        return emails;
    }

    public class GetNumber extends AsyncTask<Void, Void, String []> {

        @Override
        protected void onPreExecute() {
            dialog.setCancelable(false);
            dialog.setTitle("Getting Emails...");
            dialog.show();
        }

        @Override
        protected String[] doInBackground(Void... params) {
            String data = getHttpData();
            return renderData(data);
        }

        @Override
        protected void onPostExecute(String [] params) {
            if (dialog.isShowing())
                dialog.dismiss();
            if (params[0].equals("")) {
                email1.setVisibility(View.INVISIBLE);
            } else {
                email1.setText(params[0]);
            }
            if (params[1].equals("")) {
                email2.setVisibility(View.INVISIBLE);
            } else {
                email2.setText(params[1]);
            }
            if (params[2].equals("")) {
                email3.setVisibility(View.INVISIBLE);
            } else {
                email3.setText(params[2]);
            }
            if (!(params[0].equals("") && params[1].equals("") && params[2].equals(""))) {
                description.setText("These are contact emails that are registered:");
                addEmails.setVisibility(View.INVISIBLE);
            }
        }
    }

    public class SendThiefMode extends AsyncTask<Boolean, Void, String> {

        @Override
        protected void onPreExecute() {
            dialog.setCancelable(false);
            dialog.setTitle("Setting info");
            dialog.show();
        }

        @Override
        protected String doInBackground(Boolean... params) {
            thiefModeHttpSend(params[0]);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String param) {
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }
}
