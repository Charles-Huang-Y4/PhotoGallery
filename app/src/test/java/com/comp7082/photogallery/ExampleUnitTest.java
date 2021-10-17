package com.comp7082.photogallery;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.comp7082.photogallery.Models.Photo;
import com.comp7082.photogallery.Models.Photos;
import com.comp7082.photogallery.Presenters.GalleryPresenter;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private File testEmptyFile;
    private File testPhotoFile;
    private Date testFakeDateStart;
    private Date testFakeDateEnd;
    public ArrayList<String> photos;

    /**
     * This setup function will be called before
     * all the tests
     */
    @Before
    public void setup() {
        testEmptyFile = new File("");
        testPhotoFile = new File("_caption_20211017_024322_37.421_-122.0_602124373613480832.jpeg");
        testFakeDateStart = new Date("Tues, 3 Jul 2001 12:08:56 -0700");
        testFakeDateEnd = new Date("Wed, 4 Jul 2001 12:08:56 -0700");
        photos = null;
    }

    // THIS DOESN'T WORK BECAUSE WE DON'T MOCK THE ENVIRONMENT
//    @Test
//    public void checkSearchPhotoArray() {
//        int index = GalleryPresenter.CreateInstance().searchPhotoArray(testEmptyFile.toString());
//        assertEquals(-1, index);
//
//    }
}