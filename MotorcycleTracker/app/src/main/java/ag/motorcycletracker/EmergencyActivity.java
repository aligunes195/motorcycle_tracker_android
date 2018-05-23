package ag.motorcycletracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by aligunes on 23/05/2018.
 */

public class EmergencyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
