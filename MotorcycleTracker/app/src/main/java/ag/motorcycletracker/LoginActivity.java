package ag.motorcycletracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class LoginActivity extends AppCompatActivity {

    private EditText mNodeIdView;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkIfLoginNotNeeded();
        dialog = new ProgressDialog(this);
        mNodeIdView = (EditText) findViewById(R.id.nodeId);
        mNodeIdView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    private void checkIfLoginNotNeeded() {
        String nodeId = "";
        try {
            InputStream inputStream = this.openFileInput("nodeId.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                nodeId = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        if (verifyNode(nodeId)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    private void saveNodeId(String nodeId) {
        try {
            OutputStreamWriter outputStreamWriter
                    = new OutputStreamWriter(this.openFileOutput("nodeId.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(nodeId);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("login activity", "File write failed: " + e.toString());
        }
    }

    private void attemptLogin() {
        new LoginTask(mNodeIdView.getText().toString()).execute();
    }

    private boolean verifyNode(String mNodeId) {
        if (!mNodeId.equals(""))
            return true;
        return false;
    }

    private class LoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mNodeId;

        protected LoginTask(String nodeId) {
            mNodeId = nodeId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
                return verifyNode(mNodeId);
            } catch (InterruptedException e) {
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
            dialog.setCancelable(false);
            dialog.setTitle("Checking...");
            dialog.setMessage("Please wait");
            dialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (dialog.isShowing())
                dialog.dismiss();
            if (success) {
                saveNodeId(mNodeId);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            } else {
                mNodeIdView.setError(getString(R.string.error_incorrect_password));
                mNodeIdView.requestFocus();
            }
        }
    }
}

