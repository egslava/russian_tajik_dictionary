package ru.egslava.dictionary;

import android.app.Application;

//import com.google.analytics.tracking.android.GoogleAnalytics;
//import com.google.analytics.tracking.android.Tracker;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EApplication;

/**
 * Created by egslava on 20/12/14.
 */

import java.util.HashMap;

@EApplication
public class MainApplication extends Application {

    Tracker tracker;

    @AfterInject
    void init(){
        getTracker();
//        tracker.send(new HitBuilders.ScreenViewBuilder().build());
//        tracker.send(new HitBuilders.AppViewBuilder().build());
    }
    synchronized Tracker getTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            tracker = analytics.newTracker(R.xml.app_tracker);
        }
        return tracker;
    }
}
