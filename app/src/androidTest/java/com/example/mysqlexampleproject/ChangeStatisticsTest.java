package com.example.mysqlexampleproject;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ChangeStatisticsTest {


    ChangeStatistics activity;

    @Rule
    public ActivityTestRule<ChangeStatistics> mainActivityRule = new ActivityTestRule<>(ChangeStatistics.class);



    @Before
    public void createBluetooth() {
        activity = mainActivityRule.getActivity();
        activity.setPassword("skod");
    }

    @Test
    public void testGetDataStatistic() throws InterruptedException {
        activity.initiateGetDataStatistics();
        TimeUnit.SECONDS.sleep(15);
        assertThat(activity.getMoustUsedAdapter() != null,is(true));
        assertThat(activity.getAverageAdapter() != null,is(true));
    }


}
