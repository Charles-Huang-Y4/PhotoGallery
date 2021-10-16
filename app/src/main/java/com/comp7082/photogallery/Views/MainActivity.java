package com.comp7082.photogallery.Views;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.comp7082.photogallery.Presenters.GalleryPresenter;
import com.comp7082.photogallery.Presenters.LocationTagger;
import com.comp7082.photogallery.R;

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

    private LocationTagger locTagger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gp = GalleryPresenter.CreateInstance();
        if (gp.getPhotosToDisplay() != null) {
            displayPhoto(gp.getPhotosToDisplay());
        }

        locTagger = new LocationTagger(this);
    }

    private void displayPhoto(String path) {
        ImageView iv = findViewById(R.id.ivGallery);
        TextView tvTimestamp = findViewById(R.id.tvTimestamp);
        TextView tvLocation = findViewById(R.id.tvPhotoLocation);
        TextView tvPhotoID = findViewById(R.id.tvPhotoID);
        EditText etCaption = findViewById(R.id.etCaption);

        if (path == null || path.equals("")) {
            iv.setImageResource(R.mipmap.ic_launcher);
            etCaption.setText("");
            tvTimestamp.setText("");
            tvLocation.setText("");
            tvPhotoID.setText("");
        } else {
            iv.setImageBitmap(BitmapFactory.decodeFile(path));
            String[] attr = path.split("_");
            String photoID = "ID: " + attr[2] + attr[3];

            etCaption.setText(attr[1]);

            if(attr[2].length() != 8){
                Log.e("Date Error","Given date string is not length 8");
            } else {
                String formattedDate = "Date: " + attr[2].substring(6,8) + "/" + attr[2].substring(4,6) + "/" + attr[2].substring(0,4);
                tvTimestamp.setText(formattedDate);
            }

            if (attr.length >= 6) {
                String loc = "Lat, Lng: " + attr[4] + ", " + attr[5];
                tvLocation.setText(loc);
            }

            tvPhotoID.setText(photoID);
        }
    }

    public void scrollPhotos(View v) throws IOException {
        String caption = ((EditText) findViewById(R.id.etCaption)).getText().toString();

        gp.updatePhoto(gp.photos.get(gp.index), caption, gp.index);

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
        locTagger.getLocation();

        // Create an image file name
        String timeStamp = new android.icu.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "_caption_" + timeStamp + "_" +
                locTagger.getLatitude() + "_" + locTagger.getLongitude() + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpeg",storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
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
                    String from = data.getStringExtra("STARTTIMESTAMP");
                    String to = data.getStringExtra("ENDTIMESTAMP");
                    startTimestamp = format.parse(from);
                    endTimestamp = format.parse(to);

                } catch (Exception ex) {
                    startTimestamp = null;
                    endTimestamp = null;
                }
                String keywords = data.getStringExtra("KEYWORDS");
                String lat = data.getStringExtra("LATITUDE");
                String lng = data.getStringExtra("LONGITUDE");

                gp.index = 0;
                gp.findPhotos(startTimestamp, endTimestamp, keywords, lat, lng);

                if (gp.photos.size() == 0) {
                    displayPhoto(null);
                } else {
                    displayPhoto(gp.photos.get(gp.index));
                }
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView mImageView = findViewById(R.id.ivGallery);

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