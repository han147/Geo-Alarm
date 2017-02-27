package com.hanjeong.android.geo_alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.awareness.fence.FenceState;

import io.realm.Realm;

/**
 * Created by hanjeong on 2017. 2. 10..
 */

public class LocationFenceService extends Service {




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            Log.i("intent null check", "intent is null");
            return START_STICKY;
        }

        FenceState fenceState = FenceState.extract(intent);
        if(fenceState == null){
            Log.i("fence null check", "fenceState is null");
        }
        String id = fenceState.getFenceKey();

        if(id == null) {
            Log.i("fence state test",Integer.toString(fenceState.getCurrentState()));

        } else {
            Log.i("Service Fence Key",id);
            Log.i("fence state test",Integer.toString(fenceState.getCurrentState()));
            String numberId = id.split("/")[1];
            Realm realm = Realm.getDefaultInstance();
            AlarmModel alarmModel = realm.where(AlarmModel.class).equalTo("id",Long.valueOf(numberId)).findFirst();
            Log.i("fencestate",id +" : "+fenceState.getCurrentState());
            switch (fenceState.getCurrentState()) {
                case FenceState.TRUE:

                    //TODO : receive 받았을때 notification 생성
                    showNotification(alarmModel);

//                    Log.i("id check",intent.getExtras().toString());
                    FenceUtil.unregisterFence(this, id);



                    if(alarmModel.getRepeat()) {
                        Intent setIntent = new Intent(getApplicationContext(), LocationFenceService.class);
                        PendingIntent pendingIntent = PendingIntent.getService(this, 1, setIntent,0);
                        FenceUtil.registerFences(alarmModel, getApplicationContext(), pendingIntent, new MainActivity());
                    } else {
                        //TODO : switch off
                        realm.beginTransaction();
                        alarmModel.setOnoff(false);

                        realm.copyToRealmOrUpdate(alarmModel);
                        realm.commitTransaction();

                        AlarmListAdapter alarmListAdapter = AlarmListFragment.alarmListAdapter;
                        alarmListAdapter.notifyDataSetChanged();

                    }
                    try {
                        Intent notifyIntent = new Intent(getApplicationContext(), PopupActivity.class);
                        notifyIntent.putExtra("id", id);
                        PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 1, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        notifyPendingIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
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
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT|
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("tabFlag",1);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);



        // 알림 제목.
        builder.setContentTitle(alarmModel.getTitle());

        // 알림 내용.
        builder.setContentText(alarmModel.getAddress());

        // 알림시 사운드, 진동, 불빛을 설정 가능.
        builder.setDefaults(Notification.DEFAULT_VIBRATE);

        if(alarmModel.getOnSound()) {
            builder.setDefaults(Notification.DEFAULT_SOUND);
        }

        // 알림 터치시 반응.
        builder.setContentIntent(pendingIntent);

        // 알림 터치시 반응 후 알림 삭제 여부.
        builder.setAutoCancel(true);

        // 고유ID로 알림을 생성.
        NotificationManager nm = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        String strId = Long.toString(alarmModel.getId());
        int notifyId = Integer.valueOf(strId.substring(0,6));
        nm.notify(notifyId, builder.build());
    }




}
