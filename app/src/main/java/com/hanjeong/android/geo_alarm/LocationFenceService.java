package com.hanjeong.android.geo_alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.awareness.fence.FenceState;

import io.realm.Realm;

/**
 * Created by hanjeong on 2017. 2. 10..
 */

public class LocationFenceService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private FenceState fenceState;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        fenceState = FenceState.extract(intent);
        String id = fenceState.getFenceKey();
        Log.i("Service intent id",intent.getStringExtra("id"));
        if(id == null) {
            Log.i("fence state test",Integer.toString(fenceState.getCurrentState()));

        } else {
            Log.i("Service Fence Key",id);
            Log.i("fence state test",Integer.toString(fenceState.getCurrentState()));
            Realm realm = Realm.getDefaultInstance();
            AlarmModel alarmModel = realm.where(AlarmModel.class).equalTo("id",Long.valueOf(id)).findFirst();

            switch (fenceState.getCurrentState()) {
                case FenceState.TRUE:
                    //TODO : receive 받았을때 notification 생성
                    Toast.makeText(getApplicationContext(), "You must do it!",Toast.LENGTH_LONG);
                    showNotification(alarmModel);

                    Log.i("id check",intent.getExtras().toString());
                    break;
                case FenceState.FALSE:
                    Log.i("fencestate",fenceState.toString());
                    break;
                case FenceState.UNKNOWN:
                    Toast.makeText(getApplicationContext(), "Oops, your location status is unknown!",Toast.LENGTH_LONG);

                    break;
            }
        }

        return START_STICKY;
    }

    public void showNotification(AlarmModel alarmModel) {
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);


//        // 알림 출력 시간.
//        builder.setWhen(System.currentTimeMillis());

        // 알림 제목.
        builder.setContentTitle(alarmModel.getTitle());

        // 알림 내용.
        builder.setContentText("content");

//        // 알림시 사운드, 진동, 불빛을 설정 가능.
//        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS);
//
//        // 알림 터치시 반응.
//        builder.setContentIntent(pendingIntent);

        // 알림 터치시 반응 후 알림 삭제 여부.
        builder.setAutoCancel(true);
//
//        // 우선순위.
//        builder.setPriority(NotificationCompat.PRIORITY_MAX);

//        // 행동 최대3개 등록 가능.
//        builder.addAction(R.mipmap.ic_launcher, "Show", pendingIntent);
//        builder.addAction(R.mipmap.ic_launcher, "Hide", pendingIntent);
//        builder.addAction(R.mipmap.ic_launcher, "Remove", pendingIntent);

        // 고유ID로 알림을 생성.
        NotificationManager nm = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        nm.notify(123456, builder.build());
    }


}
