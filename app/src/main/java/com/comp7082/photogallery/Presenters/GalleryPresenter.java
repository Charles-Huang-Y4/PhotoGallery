package com.comp7082.photogallery.Presenters;

import com.comp7082.photogallery.Models.Photos;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class GalleryPresenter {

    public ArrayList<String> photos = null; // the photo array
    public int index = 0; // the index of the current displaying photo in array

    private static GalleryPresenter gp;

    private GalleryPresenter() {
        updatePhotoArray();
    }

    public static GalleryPresenter CreateInstance() {
        if (gp == null) {
            gp = new GalleryPresenter();
        }
        return gp;
    }

    // refresh the photo array with no search query
    public void updatePhotoArray() {
        photos = Photos.findPhotos(new Date(Long.MIN_VALUE), new Date(), "");
    }

    // filter the photos in the array to time and keywords
    public void findPhotos(Date startTimestamp, Date endTimestamp, String keywords) {
        photos = Photos.findPhotos(startTimestamp, endTimestamp, keywords);
    }

    // returns the current photo in array to display on screen
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

    // returns the index of the image in the array, else return -1
    public int searchPhotoArray(String imageString) {
        for (int i = 0; i < photos.size(); i++) {
            if (photos.get(i).equals(imageString)) {
                return i;
            }
        }
        return -1;
    }

    // updating the caption on a photo in the array
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
