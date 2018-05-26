package ag.motorcycletracker;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URI;

public class EmergencyAddActivity extends AppCompatActivity {

    private EditText sendEmail1;
    private EditText sendEmail2;
    private EditText sendEmail3;
    private Button saveButton;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_add);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sendEmail1 = (EditText) findViewById(R.id.sendEmail1);
        sendEmail2 = (EditText) findViewById(R.id.sendEmail2);
        sendEmail3 = (EditText) findViewById(R.id.sendEmail3);
        dialog = new ProgressDialog(this);

        saveButton = (Button) findViewById(R.id.save_emergency_emails);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEmailsClick();
            }
        });
    }

    private void saveEmailsClick() {
        if (!checkTexts())
            return;
        String email1 = sendEmail1.getText().toString();
        String email2 = sendEmail2.getText().toString();
        String email3 = sendEmail3.getText().toString();
        new SendNumber().execute(email1, email2, email3);
    }

    private boolean checkTexts() {
        if (sendEmail1.getText().toString().equals("")) {
            sendEmail1.setError("Email 1 cannot be empty.");
            sendEmail1.requestFocus();
            return false;
        }
        return true;
    }

    public class SendNumber extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            dialog.setCancelable(false);
            dialog.setTitle("Saving...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String urlString = "https://project-bff.eu-gb.mybluemix.net/addphones";
            try
            {
                for (int i = 0; i < params.length; i++) {
                    if (params[i].equals(""))
                        continue;
                    String data = "?no=" + params[i];
                    HttpClient client = new DefaultHttpClient();
                    URI website = new URI(urlString + data);
                    HttpGet request = new HttpGet();
                    request.setURI(website);
                    client.execute(request);
                }
            }
            catch (Exception e)
            {
                Log.d("HttpException", e.getMessage());
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String param) {
            if (dialog.isShowing())
                dialog.dismiss();
            onBackPressed();
        }
    }
}
