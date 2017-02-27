package com.hanjeong.android.geo_alarm;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by hanjeong on 2017. 2. 9..
 */

public class AlarmModel extends RealmObject{
    @PrimaryKey
    Long id;

    Double latitude;
    Double longitude;
    String address;

    Double radius;

    String title;
    Boolean repeat;

    RealmList<ToDoModel> toDoList;
    int todoCount;

    Boolean onoff;

    Boolean alarmDelete;

    Boolean onSound;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getRepeat() {
        return repeat;
    }

    public void setRepeat(Boolean repeat) {
        this.repeat = repeat;
    }

    public RealmList<ToDoModel> getToDoList() {
        return toDoList;
    }

    public void setToDoList(RealmList<ToDoModel> toDoList) {
        this.toDoList = toDoList;
    }

    public int getTodoCount() {
        return todoCount;
    }

    public void setTodoCount(int todoCount) {
        this.todoCount = todoCount;
    }

    public Boolean getOnoff() {
        return onoff;
    }

    public void setOnoff(Boolean onoff) {
        this.onoff = onoff;
    }

    public Boolean getAlarmDelete() {
        return alarmDelete;
    }

    public void setAlarmDelete(Boolean alarmDelete) {
        this.alarmDelete = alarmDelete;
    }

    public Boolean getOnSound() {
        return onSound;
    }

    public void setOnSound(Boolean onSound) {
        this.onSound = onSound;
    }
}
