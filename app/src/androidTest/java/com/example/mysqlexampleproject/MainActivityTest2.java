package com.example.mysqlexampleproject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest2 {


    Activity mainActivity;
    @Rule
    public ActivityTestRule<MainActivity> mainActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void initiateActivity() {
        mainActivity = mainActivityRule.getActivity();
    }

    @Test
    public void doNothing() {
        onView(withId(R.id.goToBluetooth)).check(matches(isDisplayed()));
        onView(withId(R.id.goToChangeData)).check(matches(isDisplayed()));
        onView(withId(R.id.goToGetData)).check(matches(isDisplayed()));
    }

    @Test
    public void goToBluetooth() {
        onView(withId(R.id.goToBluetooth)).perform(click());
        onView(withId(R.id.FindBluetooth)).check(matches(isDisplayed()));
        onView(withId(R.id.DeviceListView)).check(matches(isDisplayed()));
        onView(withId(R.id.getBack)).check(matches(isDisplayed()));

    }

    @Test
    public void goToGetData() {
        onView(withId(R.id.goToGetData)).perform((click()));
        onView(withId(R.id.myListView)).check(matches(isDisplayed()));
        onView(withId(R.id.progressTextView)).check(matches(withText("")));
        onView(withId(R.id.getDataButton)).check(matches(isDisplayed()));
        onView(withId(R.id.getBack)).check(matches(isDisplayed()));

    }

    @Test
    public void bluetoothFindDevices() {
        onView(withId(R.id.goToBluetooth)).perform(click());
        onView(withId(R.id.FindBluetooth)).perform(click());
        onView(withId(R.id.getBack)).perform(click());
    }

    @Test
    public void getDataClickButtons() {
        onView(withId(R.id.goToGetData)).perform(click());
        onView(withId(R.id.getDataButton)).perform(click());
        onView(withId(R.id.getBack)).perform(click());
    }


}
