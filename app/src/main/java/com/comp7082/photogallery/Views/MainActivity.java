package com.comp7082.photogallery.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri; import android.os.Bundle; import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View; import android.widget.EditText;
import android.widget.ImageView; import android.widget.TextView;

import com.comp7082.photogallery.Presenters.GalleryPresenter;
import com.comp7082.photogallery.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SEARCH_ACTIVITY_REQUEST_CODE = 2;

    String mCurrentPhotoPath;

    private GalleryPresenter gp;
    private String newPhotoString;

    private FusedLocationProviderClient fusedLocationClient;
    private String locLatitude;
    private String locLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gp = GalleryPresenter.CreateInstance();
        if (gp.getPhotosToDisplay() != null) {
            displayPhoto(gp.getPhotosToDisplay());
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
    }

    private void displayPhoto(String path) {
        ImageView iv = (ImageView) findViewById(R.id.ivGallery);
        TextView tv = (TextView) findViewById(R.id.tvTimestamp);
        EditText et = (EditText) findViewById(R.id.etCaption);

        if (path == null || path.equals("")) {
            iv.setImageResource(R.mipmap.ic_launcher);
            et.setText("");
            tv.setText("");
        } else {
            iv.setImageBitmap(BitmapFactory.decodeFile(path));
            String[] attr = path.split("_");
            Log.e("HELP", attr[0]);
            et.setText(attr[1]);
            tv.setText(attr[2]);
        }
    }

    public void scrollPhotos(View v) throws IOException {
        gp.updatePhoto(gp.photos.get(gp.index),
                ((EditText) findViewById(R.id.etCaption)).getText().toString(), gp.index);
        switch (v.getId()) {
            case R.id.btnPrev:
                gp.decrementIndex();
                break;
            case R.id.btnNext:
                gp.incrementIndex();
                break;
            default:
                break;
        }
        displayPhoto(gp.getPhotosToDisplay());
    }

    public void searchPhotos(View v) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, SEARCH_ACTIVITY_REQUEST_CODE);
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    /**
     * Get the user's location. If permission is not granted initially, try requesting it.
     */
    private void getLocation() {
        // Permissions denied
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        // Get location
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                locLatitude = String.valueOf(location.getLatitude());
                locLatitude = locLatitude.substring(0,Math.min(locLatitude.length(),6));

                locLongitude = String.valueOf(location.getLongitude());
                locLongitude = locLongitude.substring(0,Math.min(locLongitude.length(),6));

            }
        });

    }

    // Taking Photos
    public void takePhoto(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.comp7082.photogallery.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                newPhotoString = String.valueOf(photoFile);

            }

        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new android.icu.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "_caption_" + timeStamp + "_" + locLongitude + "_" + locLatitude + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpeg",storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d("sendhelp", mCurrentPhotoPath);
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // when searching
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startTimestamp , endTimestamp;
                try {
                    String from = (String) data.getStringExtra("STARTTIMESTAMP");
                    String to = (String) data.getStringExtra("ENDTIMESTAMP");
                    startTimestamp = format.parse(from);
                    endTimestamp = format.parse(to);
                } catch (Exception ex) {
                    startTimestamp = null;
                    endTimestamp = null;
                }
                String keywords = (String) data.getStringExtra("KEYWORDS");

                gp.index = 0;
                gp.findPhotos(startTimestamp, endTimestamp, keywords);

                if (gp.photos.size() == 0) {
                    displayPhoto(null);
                } else {
                    displayPhoto(gp.photos.get(gp.index));
                }
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView mImageView = (ImageView) findViewById(R.id.ivGallery);
            Log.d("HELP", mCurrentPhotoPath);
            mImageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
            gp.updatePhotoArray();
            gp.index = gp.searchPhotoArray(newPhotoString);
            if (gp.index >= 0) {
                if (gp.photos.size() == 0) {
                    displayPhoto(null);
                } else {
                    displayPhoto(gp.photos.get(gp.index));
                }
            }
        }
    }

}