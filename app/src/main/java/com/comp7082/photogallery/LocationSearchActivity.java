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
    }

    public void cancel(final View v) {
        finish();
    }

    public void go(final View v) {
        Intent i = new Intent();
        EditText lat = (EditText) findViewById(R.id.etLatitude);
        EditText lng = (EditText) findViewById(R.id.etLongitude);

        i.putExtra("Latitude", lat.getText() != null ? lat.getText().toString() : "");
        i.putExtra("Longitude", lng.getText() != null ? lng.getText().toString() : "");

        setResult(RESULT_OK, i);

        finish();
    }

}