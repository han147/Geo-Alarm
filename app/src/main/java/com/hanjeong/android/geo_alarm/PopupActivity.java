package com.hanjeong.android.geo_alarm;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

public class PopupActivity extends Activity {


    @Bind(R.id.popup_todolist_area)
    LinearLayout popupLayout;

    @Bind(R.id.popup_title)
    TextView popupTitleTextView;

    @Bind(R.id.popup_address)
    TextView popupAddressTextView;

    @Bind(R.id.go_activity_btn)
    Button goActivityButton;

    @Bind(R.id.confirm_btn)
    Button confrimButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        ButterKnife.bind(this);
        this.setFinishOnTouchOutside(false);

        Intent intent = getIntent();

        String strId = intent.getStringExtra("id").split("/")[1];
        Long id;
        if(strId != null) {
            id = Long.valueOf(strId);

            Realm realm = Realm.getDefaultInstance();
            AlarmModel alarmModel = realm.where(AlarmModel.class).equalTo("id",id).findFirst();

//            customDialog = new CustomDialog(this, alarmModel, leftListener, rightListener);
//
//            customDialog.show();

            popupTitleTextView.setText(alarmModel.getTitle());

            popupAddressTextView.setText(alarmModel.getAddress());

            List<ToDoModel> toDoModelList = alarmModel.getToDoList();

            for(ToDoModel todo : toDoModelList) {
                TextView todoTextView = new TextView(this);
                todoTextView.setText(" - "+todo.getContent());
                todoTextView.setTextSize(getResources().getDimension(R.dimen.popup_todo_text_size));

                popupLayout.addView(todoTextView);
            }

            goActivityButton.setOnClickListener(leftListener);
            confrimButton.setOnClickListener(rightListener);


        }


    }

    private View.OnClickListener leftListener = new View.OnClickListener() {
        public void onClick(View v) {

            //TODO: TODOLIST Fragment 띄워주기
            finish();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("tabFlag",1);
            startActivity(intent);
        }
    };

    private View.OnClickListener rightListener = new View.OnClickListener() {
        public void onClick(View v) {

            finish();
        }
    };
}
