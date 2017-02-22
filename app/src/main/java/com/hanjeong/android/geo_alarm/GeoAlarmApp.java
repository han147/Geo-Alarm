package com.hanjeong.android.geo_alarm;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by hanjeong on 2017. 2. 14..
 */

public class GeoAlarmApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(getApplicationContext());
        Realm.setDefaultConfiguration(
                new RealmConfiguration.Builder()
                        .name("alarmApp")
                        .build()
        );
    }
}
