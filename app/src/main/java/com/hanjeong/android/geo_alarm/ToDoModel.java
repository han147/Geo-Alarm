package com.hanjeong.android.geo_alarm;

import io.realm.RealmObject;

/**
 * Created by hanjeong on 2017. 2. 14..
 */

public class ToDoModel extends RealmObject {
    String content;
    boolean check;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
