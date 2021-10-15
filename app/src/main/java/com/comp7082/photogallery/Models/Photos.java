package com.comp7082.photogallery.Models;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class Photos {

    public static ArrayList<String> findPhotos(Date startTimestamp, Date endTimestamp, String keywords) {
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
}
