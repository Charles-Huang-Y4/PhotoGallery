package com.comp7082.photogallery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.comp7082.photogallery.Views.MainActivity;

import java.util.regex.Pattern;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExampleInstrumentedTest {
    private UiDevice device;

    @Before
    public void setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.comp7082.photogallery", appContext.getPackageName());
    }

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void snapPhotoAndCheckCaption() throws UiObjectNotFoundException {
        onView(withId(R.id.snap)).perform(click());
        device.findObject(By.res("com.android.camera2:id/shutter_button")
                .desc("Shutter").clazz("android.widget.ImageView").text(Pattern.compile(""))
                .pkg("com.android.camera2")).clickAndWait(Until.newWindow(), 1000);
        device.findObject(By.res("com.android.camera2:id/done_button"))
                .clickAndWait(Until.newWindow(), 1000);
        onView(withId(R.id.etCaption)).check(matches(withText("caption")));
    }

    @Test
    public void renamePhotoCaption() {
        onView(withId(R.id.snap)).perform(click());
        device.findObject(By.res("com.android.camera2:id/shutter_button")
                .desc("Shutter").clazz("android.widget.ImageView").text(Pattern.compile(""))
                .pkg("com.android.camera2")).clickAndWait(Until.newWindow(), 1000);
        device.findObject(By.res("com.android.camera2:id/done_button"))
                .clickAndWait(Until.newWindow(), 1000);
        onView(withId(R.id.etCaption)).check(matches(withText("caption")));
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.etCaption)).perform(clearText(), replaceText("test"));
    }

    @Test
    // YOUR EMULATOR SCREEN HAS TO BE ON FOR THIS TO WORK
    // This doesn't work until a photo with the word caption is in the list of photos
    public void useSearchForCaptionImage(){
        onView(withId(R.id.snap)).perform(click());
        device.findObject(By.res("com.android.camera2:id/shutter_button"))
                .clickAndWait(Until.newWindow(), 1000);
        device.findObject(By.res("com.android.camera2:id/done_button"))
                .clickAndWait(Until.newWindow(), 1000);
        onView(withId(R.id.etCaption)).check(matches(withText("caption")));
        onView(withId(R.id.btnSearch)).perform(click());
        onView(withId(R.id.etFromDateTime)).perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.etToDateTime)).perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.etKeywords)).perform(typeText("caption"), closeSoftKeyboard());
        onView(withId(R.id.go)).perform(click());
        onView(withId(R.id.etCaption)).check(matches(withText("caption")));
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.btnPrev)).perform(click());
    }

    @Test
    public void checkUploadFunction() {
        onView(withId(R.id.btnUpload)).perform(click());
        device.pressBack();
    }

}
