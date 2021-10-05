package com.comp7082.photogallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri; import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SEARCH_ACTIVITY_REQUEST_CODE = 2;
    private static final int LOCATION_ACTIVITY_REQUEST_CODE = 3;
    String mCurrentPhotoPath;
    private ArrayList<String> photos = null;
    private int index = 0;
    private FusedLocationProviderClient fusedLocationClient;
    private final int locationRequestCode = 1000;
    private double locLatitude;
    private double locLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), "");
        if (photos.size() == 0) {
            displayPhoto(null);
        } else {
            displayPhoto(photos.get(index));
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

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
            }
        }
    }

    private ArrayList<String> findPhotos(Date startTimestamp, Date endTimestamp, String keywords) {
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/com.comp7082.photogallery/files/Pictures");
        ArrayList<String> photos = new ArrayList<String>();
        File[] fList = file.listFiles();
        if (fList != null) {
            for (File f : fList) {
                if (((startTimestamp == null && endTimestamp == null) || (f.lastModified() >= startTimestamp.getTime()
                        && f.lastModified() <= endTimestamp.getTime())
                ) && (keywords == "" || f.getPath().contains(keywords)))
                    photos.add(f.getPath());
            }
        }
        return photos;
    }

    /**
     * Launch "Search by Keyword" Activity.
     * @param v the view
     */
    public void searchPhotos(View v) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, SEARCH_ACTIVITY_REQUEST_CODE);
    }

    public void scrollPhotos(View v) {
        updatePhoto(photos.get(index), ((EditText) findViewById(R.id.etCaption)).getText().toString(), index);
        switch (v.getId()) {
            case R.id.btnPrev:
                if (index > 0) {
                    index--;
                }
                break;
            case R.id.btnNext:
                if (index < (photos.size() - 1)) {
                    index++;
                }
                break;
            default:
                break;
        }
        displayPhoto(photos.get(index));
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
            et.setText(attr[1]);
            tv.setText(attr[2]);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "_caption_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startTimestamp, endTimestamp;
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
                index = 0;

                photos = findPhotos(startTimestamp, endTimestamp, keywords);
                if (photos.size() == 0) {
                    displayPhoto(null);
                } else {
                    displayPhoto(photos.get(index));
                }
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView mImageView = (ImageView) findViewById(R.id.ivGallery);
            mImageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));

            getLocation();

            photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), "");
            displayPhoto(photos.get(index));
        }

        if (requestCode == LOCATION_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // display the photos that match the location???
            }
        }
    }

    /**
     * Update the photo's caption.
     * @param path photo stored location
     * @param caption the photo's description
     * @param i index
     */
    private void updatePhoto(String path, String caption, int i) {
        String[] attr = path.split("_");
        if (attr.length >= 3) {
            String newName = attr[0] + "_" + caption + "_" + attr[2] + "_" + attr[3] + "_";
            File to = new File(newName);
            File from = new File(path);
            if (from.renameTo(to)) {
                photos.set(i, newName);
            }
        }
    }

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
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        locLatitude = location.getLatitude();
                        locLongitude = location.getLongitude();

                        Log.e("location", "Lat: " + locLatitude + ", Long: " + locLongitude);
                    } else {
                        Log.e("Location Null Error", "Location not found.");
                    }
                });
    }

    public void sharePhotos(View view) {
        // remove restrictions
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        // This works for other things outside of facebook
//        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        shareIntent.setType("image/*");
//        Log.e("Photo", photos.get(index));
//        File photoFile = new File(photos.get(index));
//        // Log.e("Photo File", photoFile.toString());
//        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photoFile));
//        startActivity(Intent.createChooser(shareIntent, "Share images..."));

        // test
        ImageView ivImage = (ImageView) findViewById(R.id.ivGallery);
        // Get access to the URI for the bitmap
        ivImage.buildDrawingCache();
        Bitmap bitmap = ivImage.getDrawingCache();
        Uri bmpUri = getBitmapFromDrawable(bitmap);
        if (bmpUri != null) {
            // Construct a ShareIntent with link to image
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.setType("image/*");
            // Launch sharing dialog for image
            startActivity(Intent.createChooser(shareIntent, "Share Image"));
        } else {
            Log.e("Failure", "Attempt to share photo failed.");
        }
    }

    /**
     * Method when launching drawable within Glide.
     * @param bmp The bitmap
     * @return bitmap Uri
     */
    public Uri getBitmapFromDrawable(Bitmap bmp){

        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            // See https://youtu.be/5xVh-7ywKpE?t=25m25s
            File file =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();

            bmpUri = Uri.fromFile(file);
            // **Note:** For API < 24, you may use bmpUri = Uri.fromFile(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    /**
     * Launch the "Search Photos by Location" activity.
     * @param v the view
     */
    public void searchLocation(View v) {
        Intent intent = new Intent(this, LocationSearchActivity.class);
        startActivityForResult(intent, LOCATION_ACTIVITY_REQUEST_CODE);
    }
}