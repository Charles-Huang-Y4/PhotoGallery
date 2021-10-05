package com.comp7082.photogallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LocationSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);

        // Anything else to do here?
    }

    public void cancel(final View v) {
        finish();
    }

    /**
     * Take the user input for Latitude and Longitude and pass it back to MainActivity,
     * where the photos that match the inputted lat & long will be displayed.
     * @param v the view
     */
    public void go(final View v) {
        Intent i = new Intent();
        EditText lat = (EditText) findViewById(R.id.etLatitude);
        EditText lng = (EditText) findViewById(R.id.etLongitude);
        //lat.setInputType();

        i.putExtra("Latitude", lat.getText() != null ? lat.getText().toString() : "");
        i.putExtra("Longitude", lng.getText() != null ? lng.getText().toString() : "");

        setResult(RESULT_OK, i);

        finish();
    }

}