package com.comp7082.photogallery.Presenters;

import com.comp7082.photogallery.Models.Photos;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class GalleryPresenter {

    public ArrayList<String> photos = null;

    public int index = 0; // the index of the current displaying photo in array

    public GalleryPresenter() {
        updatePhotoArray();
    }

    public void updatePhotoArray() {
        photos = Photos.findPhotos(new Date(Long.MIN_VALUE), new Date(), "");
    }

    public void findPhotos(Date startTimestamp, Date endTimestamp, String keywords) {
        photos = Photos.findPhotos(startTimestamp, endTimestamp, keywords);
    }


    // returns the current photo to display
    public String getPhotosToDisplay() {
        if (photos.size() == 0) {
            return null;
        } else {
            return photos.get(index);
        }
    }

    public void incrementIndex() {
        if (photos.size() > 0 && index < photos.size() - 1) {
            index++;
        }
    }

    public void decrementIndex() {
        if (photos.size() > 0 && index > 0) {
            index--;
        }
    }

    public int searchPhotoArray(String imageString) {
        for (int i = 0; i < photos.size(); i++) {
            if (photos.get(i).equals(imageString)) {
                return i;
            }
        }
        return -1;
    }

    public void updatePhoto(String path, String caption, int i) {
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



}
