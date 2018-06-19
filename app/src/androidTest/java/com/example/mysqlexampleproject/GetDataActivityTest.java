package com.example.mysqlexampleproject;


import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class GetDataActivityTest {

    getDataActivity activity;

    @Rule
    public ActivityTestRule<getDataActivity> mainActivityRule = new ActivityTestRule<>(getDataActivity.class);



    @Before
    public void createBluetooth() {
        activity = mainActivityRule.getActivity();
        activity.setPassword("skod");
    }

    @Test
    public void testGetData() throws InterruptedException {
        activity.initiateGetData();
        TimeUnit.SECONDS.sleep(15);
        assertThat(activity.getListViewAdapter() != null,is(true));
    }
}
