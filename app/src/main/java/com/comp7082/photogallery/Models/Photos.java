package com.comp7082.photogallery.Models;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Photos {

    // search the device for photos
    public static ArrayList<String> findPhotos(Date startTimestamp, Date endTimestamp, String keywords,
                                               String lat, String lng) {
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/com.comp7082.photogallery/files/Pictures");

        ArrayList<String> photos = new ArrayList<>();
        File[] fList = file.listFiles();
        ArrayList<File> fileArray = new ArrayList<>();

        fileArray.stream().filter(f -> isValidDate(f, startTimestamp, endTimestamp) &&
                isValidKeyword(f, keywords) &&
                isValidLocation(f, lat, lng));

        if (fList != null) {
            for (File f : fList) {
                Log.e("File", f.toString());
                if (isValidDate(f, startTimestamp, endTimestamp) && isValidKeyword(f, keywords) &&
                    isValidLocation(f, lat, lng))
                    photos.add(f.getPath());
            }

        }
        return photos;
    }

    // check if the start and end dates are both empty or both valid dates.
    private static boolean isValidDate(File f, Date startTimestamp, Date endTimestamp) throws NullPointerException {
        return (startTimestamp == null && endTimestamp == null) ||
                (f.lastModified() >= Objects.requireNonNull(startTimestamp).getTime() && f.lastModified() <= endTimestamp.getTime());
    }

    // check if the file contains the keywords or was left empty.
    private static boolean isValidKeyword(File f, String keywords) {
        return (keywords.equals("") || f.getPath().contains(keywords));
    }

    // check if the file has the correct location or was left empty.
    private static boolean isValidLocation(File f, String lat, String lng) {
        return (lat.equals("") && lng.equals("")) || (f.getPath().contains(lat) && f.getPath().contains(lng));
    }


}
