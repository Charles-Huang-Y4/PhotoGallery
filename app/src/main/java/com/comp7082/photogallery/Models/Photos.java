package com.comp7082.photogallery.Models;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class Photos {

    // search the device for photos
    public static ArrayList<String> findPhotos(Date startTimestamp, Date endTimestamp, String keywords,
                                               String lat, String lng) {
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/com.comp7082.photogallery/files/Pictures");

        ArrayList<String> photos = new ArrayList<String>();
        File[] fList = file.listFiles();

        if (fList != null) {
            for (File f : fList) {
                if (isValidDate(f, startTimestamp, endTimestamp) && isValidKeyword(f, keywords) &&
                    isValidLocation(f, lat, lng))
                    photos.add(f.getPath());
            }
        }
        return photos;
    }

    // check if the start and end dates are both empty or both valid dates.
    private static boolean isValidDate(File f, Date startTimestamp, Date endTimestamp) {
        return (startTimestamp == null && endTimestamp == null) ||
                (f.lastModified() >= startTimestamp.getTime() && f.lastModified() <= endTimestamp.getTime());
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
