package com.hanjeong.android.geo_alarm;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by hanjeong on 2017. 2. 8..
 */

public class AlarmListFragment extends Fragment implements AlarmListAdapter.ListItemClickListener{

    private List<AlarmModel> alarmList;
    public static AlarmListAdapter alarmListAdapter;

    @Bind(R.id.rv_alarm)
    RecyclerView alarmListView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_list, container, false);

        ButterKnife.bind(this, view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        alarmListView.setLayoutManager(layoutManager);

        alarmList = getAllList();
        alarmListAdapter = new AlarmListAdapter(alarmList, getContext(), this);
        alarmListView.setAdapter(alarmListAdapter);

//        SeparatorDecoration decoration = new SeparatorDecoration(getContext(), getResources().getColor(R.color.grey), 1);
//        alarmListView.addItemDecoration(decoration);

        return view;
    }


    private List<AlarmModel> getAllList() {
        Realm realm = Realm.getDefaultInstance();

        List<AlarmModel> alarmList = realm.where(AlarmModel.class).equalTo("alarmDelete", false).findAll();

        return alarmList;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent intent = new Intent(getContext(), AddActivity.class);
        AlarmModel clickedAlarm = alarmList.get(clickedItemIndex);
        intent.putExtra("id", clickedAlarm.getId());
        intent.putExtra("clickedItemIndex", clickedItemIndex);

        startActivity(intent);
    }

    @Override
    public void onSwitchChange(int changeItemIndex, Boolean isChecked) {
        AlarmModel changedAlarm = alarmList.get(changeItemIndex);


        Intent intent = new Intent(getContext(), LocationFenceService.class);

        intent.putExtra("id",changedAlarm.getId());
        //TODO: 4번째 파라마터 flag 값 확인
        PendingIntent mPendingIntent = PendingIntent.getService(getContext(), 1, intent,0);

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        changedAlarm.setOnoff(isChecked);
        realm.copyToRealmOrUpdate(changedAlarm);
        realm.commitTransaction();

        if(isChecked) {
            FenceUtil.registerFences(changedAlarm, getContext(), mPendingIntent, getActivity());
        } else {
            FenceUtil.unregisterFence(getContext(), Long.toString(changedAlarm.getId()));
        }

    }

    @Override
    public void onClickDeleteButton(int clickedItemIndex) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        AlarmModel deletedModel = alarmList.get(clickedItemIndex);

        if(deletedModel.getToDoList().size() > 0) {
            deletedModel.setAlarmDelete(true);

            realm.copyToRealmOrUpdate(deletedModel);
        } else {
            deletedModel.deleteFromRealm();

        }


        realm.commitTransaction();

        alarmList = getAllList();

        AlarmListAdapter alarmListAdapter = AlarmListFragment.alarmListAdapter;
        alarmListAdapter.notifyDataSetChanged();
    }
}
