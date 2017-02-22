package com.hanjeong.android.geo_alarm;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by hanjeong on 2017. 2. 8..
 */

public class ToDoListFragment extends Fragment implements TodoListAdapter.ToDoListClickListener{

    private List<AlarmModel> alarmList;
    public static TodoListAdapter todoListAdapter;

    @Bind(R.id.rv_todolist)
    RecyclerView todoListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_todolist,container, false);
        ButterKnife.bind(this, view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        todoListView.setLayoutManager(layoutManager);

        alarmList = getAllList();
        todoListAdapter = new TodoListAdapter(alarmList, getContext(), this);
        todoListView.setAdapter(todoListAdapter);

        for (AlarmModel alarm: alarmList) {
            Log.i("alarm list", alarm.getTitle());
            for(ToDoModel todo : alarm.getToDoList()){
                Log.i("alarm list",todo.toString());
            }

        }

        return view;
    }

    private List<AlarmModel> getAllList() {
        Realm realm = Realm.getDefaultInstance();

        List<AlarmModel> alarmList = realm.where(AlarmModel.class).findAll();

        return alarmList;
    }


    @Override
    public void onListItemClick(int clickedItemIndex) {
        Log.i("click index", Integer.toString(clickedItemIndex));
    }

    @Override
    public void onCheckBoxClick(View view, int clickedListIndex) {
        int itemIndex = clickedListIndex/2;
        Log.i("itemIndex", Integer.toString(itemIndex));
        int[] contentids = this.getResources().getIntArray(R.array.todolist_content_id_array);
        TypedArray checkIds = this.getResources().obtainTypedArray(R.array.todolist_check_id_array);


        for(int i=0;i<checkIds.length();i++) {
            int checkBoxId = checkIds.getResourceId(i,i);
            if(checkBoxId == view.getId()) {
               updateToDo(itemIndex,i, ((CheckBox)view).isChecked());
            }
        }

    }

    private void updateToDo(int itemIndex, int todoIndex, Boolean isChecked) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        AlarmModel alarmModel = alarmList.get(itemIndex);
        RealmList<ToDoModel> todoList = alarmModel.getToDoList();

        ToDoModel clickedToDo = todoList.get(todoIndex);
        Log.i("clicked TODO :",clickedToDo.getContent());

        clickedToDo.setCheck(isChecked);
//        todoList.set(todoIndex, clickedToDo);
//        alarmModel.setToDoList(todoList);

        realm.copyToRealmOrUpdate(alarmModel);
        realm.commitTransaction();

        realm.close();

    }
}
