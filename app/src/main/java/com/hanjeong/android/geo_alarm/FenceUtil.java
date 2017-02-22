package com.hanjeong.android.geo_alarm;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;

import java.util.List;

/**
 * Created by hanjeong on 2017. 2. 10..
 */

public class FenceUtil {

    static GoogleApiClient mApiClient;
    public static final String IN_LOCATION_FENCE_KEY = "IN_LOCATION_FENCE_KEY";
    public static final String ENTERING_LOCATION_FENCE_KEY = "ENTERING_LOCATION_FENCE_KEY";
    public static final int STATUS_ENTERING = 2;
    public static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 940;

    public static GoogleApiClient initApiClient(Context context) {
        mApiClient = new GoogleApiClient.Builder(context)
                .addApi(Awareness.API)
                .build();
        mApiClient.connect();

        return mApiClient;
    }


    public static void registerFences(AlarmModel alarmModel, final Context context, PendingIntent mPendingIntent, Activity activity) {
        mApiClient = initApiClient(context);

        // ACCESS_FINE_LOCATION permission이 없는 경우
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
            // ACCESS_FINE_LOCATION permission이 있는 경우
        } else {
            //Fence 생성
            String id = Long.toString(alarmModel.getId());
            Double latitude = alarmModel.getLatitude();
            Double longitude = alarmModel.getLongitude();
            Double radius = alarmModel.getRadius();

            AwarenessFence inLocationFence = LocationFence.in(latitude,longitude,radius, 1);
            AwarenessFence exitingLocationFence = LocationFence.exiting(latitude,longitude, radius);
            AwarenessFence enteringLocationFence = LocationFence.entering(latitude,longitude, radius);



            //Fence 등록
            Awareness.FenceApi.updateFences(
                    mApiClient,
                    new FenceUpdateRequest.Builder()
                            .addFence(id, inLocationFence, mPendingIntent)
                            .addFence(id, exitingLocationFence, mPendingIntent)
                            .addFence(id, enteringLocationFence, mPendingIntent)
                            .build())
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Toast.makeText(context,"Fence Registered",Toast.LENGTH_SHORT)
                                        .show();

                            } else {
                                Toast.makeText(context,"Fence Not Registered",Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });

        }
    }

    public static void unregisterFence(Context context, final String fenceKey) {
        mApiClient = new GoogleApiClient.Builder(context)
                .addApi(Awareness.API)
                .build();
        mApiClient.connect();

        Awareness.FenceApi.updateFences(
                mApiClient,
                new FenceUpdateRequest.Builder()
                        .removeFence(fenceKey)
                        .build()).setResultCallback(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {
                Log.i("unregisterFence", "Fence " + fenceKey + " successfully removed.");
            }

            @Override
            public void onFailure(@NonNull Status status) {
                Log.i("unregisterFence", "Fence " + fenceKey + " could NOT be removed.");
            }
        });
    }

    public void isServiceRunningCheck(Context context) {
//        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (LocationFenceService.class.equals(service.service.getClassName())) {
//                Log.i("ServiceCheck",service.toString());
//            }
//        }
        /* 실행중인 service 목록 보기 */
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(50);

        for(int i=0; i<rs.size(); i++){
            ActivityManager.RunningServiceInfo rsi = rs.get(i);
            Log.d("run service","Package Name : " + rsi.service.getPackageName());
        }

    }

}
