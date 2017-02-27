package com.hanjeong.android.geo_alarm;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;


public class AddActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{
    static final LatLng SEOUL = new LatLng(37.56, 126.97);

    public static final String FENCE_RECEIVER_ACTION =
            "com.hanjeong.android.geo_alarm.FenceBootReceiver";
    public static final String FENCE_SERVICE = "com.hanjeong.android.geo_alarm.LocationFenceService";


    private GoogleApiClient mApiClient;

    private PendingIntent mPendingIntent;

    private GoogleMap googleMap;
    private Double mLatitude;
    private Double mLongitude;
    private Double mRadius;
    private String mAddress;

    private Spinner spinner;

    private Intent intent;
    private String currentLocationAddress;

    private String strId;
    private Long existId;

    private int clickedItemIndex;


    private Realm realm;

    @Bind(R.id.todo_add_btn)
    Button btnTodoAdd;

    private List<Integer> toDoIdList;

    @Bind(R.id.et_title)
    EditText titleEditText;

    @Bind(R.id.repeat_switch)
    Switch repeatSwitch;

    @Bind(R.id.todolist_area)
    LinearLayout todoListLayout;

    @Bind(R.id.tv_address)
    TextView addressTextView;

    private ArrayAdapter<Double> adapter;

    @Bind(R.id.et_todolist)
    EditText firstTodo;

    @Bind(R.id.sound_switch)
    Switch soundSwitch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .build();
        mApiClient.connect();

        spinner = (Spinner)findViewById(R.id.radius_spinner);

        // TODO: resource로 빼기
        Double[] items = new Double[]{100.0, 200.0, 300.0};
        adapter = new ArrayAdapter<Double>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);

        toDoIdList = Arrays.asList(R.id.et_todolist, R.id.todo_list_2, R.id.todo_list_3, R.id.todo_list_4, R.id.todo_list_5);


        realm = Realm.getDefaultInstance();

        intent = getIntent();
        existId = intent.getLongExtra("id",-1);
        if(existId == -1) {
            getSupportActionBar().setTitle("등록");

            getCurrentLocation();
            Long id = System.currentTimeMillis();
            strId = Long.toString(id);

        } else {
            getSupportActionBar().setTitle("수정");
            AlarmModel existAlarmModel = realm.where(AlarmModel.class).equalTo("id",existId).findFirst();

            showExistAlarm(existAlarmModel);
            strId = Long.toString(existId);
            clickedItemIndex = intent.getIntExtra("clickedItemIndex",-1);

        }




        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


        intent = new Intent(this, LocationFenceService.class);
        intent.setAction(FenceUtil.FENCE_RECEIVER_ACTION);
        //TODO: 4번째 파라마터 flag 값 확인
        mPendingIntent = PendingIntent.getService(this, 1, intent,0);




        btnTodoAdd.setOnClickListener(this);


    }

    private void showExistAlarm(AlarmModel alarmModel) {
        titleEditText.setText(alarmModel.getTitle());
        mLatitude = alarmModel.getLatitude();
        mLongitude = alarmModel.getLongitude();
        addressTextView.setText(alarmModel.getAddress());

        int spinnerPosition = adapter.getPosition(alarmModel.getRadius());
        spinner.setSelection(spinnerPosition);

        repeatSwitch.setChecked(alarmModel.getRepeat());
        soundSwitch.setChecked(alarmModel.getOnSound());

        List<ToDoModel> todoList = alarmModel.getToDoList();
        if(todoList.size()>0) {
            firstTodo.setText(todoList.get(0).getContent());
        }
        for(int i=1;i<todoList.size();i++) {
            addList();
            EditText sibling = (EditText)findViewById(toDoIdList.get(i-1));
            View view = ((LinearLayout) sibling.getParent()).findViewById(R.id.todo_delete_btn);

            ((LinearLayout) sibling.getParent()).findViewById(R.id.todo_add_btn).setVisibility(View.INVISIBLE);
            ((LinearLayout) sibling.getParent()).findViewById(R.id.todo_delete_btn).setVisibility(View.INVISIBLE);
            EditText tempEditText = (EditText)findViewById(toDoIdList.get(i));
            tempEditText.setText(todoList.get(i).getContent());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);

        } else if(id == R.id.add_menu_btn) {
            // TODO: db저장
            AlarmModel alarmData = getAlarmData();
            saveAlarm(alarmData);
            startService(intent);
            // fence 등록
            if(existId == -1) {
                FenceUtil.registerFences(alarmData,this, mPendingIntent, this);
            } else {
                AlarmListFragment.alarmListAdapter.notifyItemChanged(clickedItemIndex);
                updateFence(strId);
            }


            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(final GoogleMap map) {

        googleMap = map;
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

        if(existId != -1) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLatitude, mLongitude)));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                LatLng location_center = cameraPosition.target;
                mLatitude = location_center.latitude;
                mLongitude = location_center.longitude;
                Log.i("Location","centerlocation"+cameraPosition.toString());

            }


        });

        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                mAddress = findAddress(mLatitude, mLongitude);
                addressTextView.setText(mAddress);
            }
        });




    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case FenceUtil.PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                //권한 생성되면 다시 registerFences() 호출
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        getCurrentLocation();
                } else {
                    //TODO : 권한을 받을 수 없는 경우 설정화면으로 넘어갈 수 있게 하기
                    Toast.makeText(getApplicationContext(),"위치에 접근할 수 없습니다",Toast.LENGTH_LONG)
                            .show();
                }
            }
        }
    }
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    FenceUtil.PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        }
        Awareness.SnapshotApi.getLocation(mApiClient)
                .setResultCallback(new ResultCallback<LocationResult>() {
                    @Override
                    public void onResult(@NonNull LocationResult locationResult) {

                        if (locationResult.getStatus().isSuccess()) {
                            Location location = locationResult.getLocation();
                            mLatitude = location.getLatitude();
                            mLongitude = location.getLongitude();
                            Log.i("Location","currentlocation"+location.toString());
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLatitude, mLongitude)));
                            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                        } else {
                            Log.i("location get","Fail");
                        }
                    }
                });
    }


    private String findAddress(double lat, double lng) {
        StringBuffer bf = new StringBuffer();
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);
        List<Address> address;
        try {
            if (geocoder != null) {
                // 세번째 인수는 최대결과값인데 하나만 리턴받도록 설정했다
                address = geocoder.getFromLocation(lat, lng, 1);
                // 설정한 데이터로 주소가 리턴된 데이터가 있으면
                if (address != null && address.size() > 0) {
                    // 주소
                    currentLocationAddress = address.get(0).getAddressLine(0).toString();

                }
            }

        } catch (IOException e) {
            Toast.makeText(this, "주소취득 실패" + e.getMessage()
                    , Toast.LENGTH_LONG).show();

            e.printStackTrace();
        }
        return currentLocationAddress;
    }

    public void addList() {
        int twoDp = (int) getResources().getDimension(R.dimen.padding_button);
        int eightDp = (int) getResources().getDimension(R.dimen.margin_button);
        int buttonSize = (int) getResources().getDimension(R.dimen.add_or_del_button_size);

        LinearLayout newListContent = new LinearLayout(this);
        newListContent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        newListContent.setOrientation(LinearLayout.HORIZONTAL);

        EditText newEditText = new EditText(this);
        newEditText.setId(toDoIdList.get(todoListLayout.getChildCount()-1));
        newEditText.setMaxLines(1);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        layoutParams.setMargins(eightDp,0,eightDp,0);
        newEditText.setLayoutParams(layoutParams);
        newEditText.setInputType(InputType.TYPE_CLASS_TEXT);



        Button newDeleteButton = new Button(this);
        layoutParams = new LinearLayout.LayoutParams(buttonSize,buttonSize);
        layoutParams.setMargins(0,0,eightDp,0);
        newDeleteButton.setLayoutParams(layoutParams);
        newDeleteButton.setPadding(twoDp,twoDp,twoDp,twoDp);

        newDeleteButton.setText("-");
        newDeleteButton.setTextColor(getResources().getColor(R.color.primary_light));
        newDeleteButton.setId(R.id.todo_delete_btn);
        newDeleteButton.setBackgroundColor(getResources().getColor(R.color.primary));
        newDeleteButton.setOnClickListener(this);


        newListContent.addView(newEditText);
        newListContent.addView(newDeleteButton);

        if(todoListLayout.getChildCount() < 5) {
            Button newAddButton = new Button(this);
            newAddButton.setLayoutParams(layoutParams);
            newAddButton.setPadding(twoDp,twoDp,twoDp,twoDp);
            newAddButton.setText("+");
            newAddButton.setTextColor(getResources().getColor(R.color.primary_light));

            newAddButton.setId(R.id.todo_add_btn);
            newAddButton.setOnClickListener(this);
            newAddButton.setBackgroundColor(getResources().getColor(R.color.primary));

            newListContent.addView(newAddButton);
        }

        todoListLayout.addView(newListContent);

        newEditText.requestFocus();

    }



    @Override
    public void onClick(View v) {


        if(v.getId() == R.id.todo_add_btn) {

            addList();


            v.setVisibility(View.INVISIBLE);

            ((LinearLayout) v.getParent()).findViewById(R.id.todo_delete_btn).setVisibility(View.INVISIBLE);

        } else if(v.getId() == R.id.todo_delete_btn) {

            LinearLayout parentLL = (LinearLayout)v.getParent();
            todoListLayout.removeView(parentLL);


            LinearLayout siblingLayout = (LinearLayout) todoListLayout.getChildAt(todoListLayout.getChildCount()-1);

            if(todoListLayout.getChildCount()-1 > 1) {
                siblingLayout.getChildAt(1).setVisibility(View.VISIBLE);
            }
            siblingLayout.getChildAt(2).setVisibility(View.VISIBLE);
        }

    }

    private AlarmModel getAlarmData() {
        AlarmModel alarmData = new AlarmModel();
        alarmData.setId(Long.valueOf(strId));

        alarmData.setTitle(titleEditText.getText().toString());
        mRadius = (Double)spinner.getSelectedItem();
        alarmData.setRadius(mRadius);
        alarmData.setLatitude(mLatitude);
        alarmData.setLongitude(mLongitude);
        alarmData.setAddress(mAddress);

        RealmList<ToDoModel> toDoList = new RealmList<ToDoModel>();

//        ToDoModel toDoModel = new ToDoModel();
//        EditText todoEditText = (EditText) findViewById(R.id.et_todolist);
//        toDoModel.setContent(todoEditText.getText().toString());
//        toDoModel.setCheck(false);
//        toDoList.add(toDoModel);

        for(int i=0;i<todoListLayout.getChildCount()-1;i++) {
            ToDoModel toDoModel = new ToDoModel();

            EditText todoEditText = (EditText) findViewById(toDoIdList.get(i));
            if(!(todoEditText.getText().toString().equals(""))) {
                toDoModel.setContent(todoEditText.getText().toString());
                toDoModel.setCheck(false);
                toDoList.add(toDoModel);
            }

        }

        alarmData.setToDoList(toDoList);

        alarmData.setTodoCount(toDoList.size());

        alarmData.setRepeat(repeatSwitch.isChecked());

        alarmData.setOnSound(soundSwitch.isChecked());

        alarmData.setOnoff(true);

        alarmData.setAlarmDelete(false);


        return alarmData;
    }

    private void saveAlarm(AlarmModel alarmData) {
//        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();


        AlarmModel insertAlarm = realm.copyToRealmOrUpdate(alarmData);


        realm.commitTransaction();
        AlarmListAdapter alarmListAdapter = AlarmListFragment.alarmListAdapter;
//        alarmListAdapter.notifyItemChanged(alarmListAdapter.getItemCount()+1);
        alarmListAdapter.notifyDataSetChanged();

        TodoListAdapter todoListAdapter = ToDoListFragment.todoListAdapter;
        todoListAdapter.notifyDataSetChanged();


    }

    protected void updateFence(final String fenceKey) {
        Awareness.FenceApi.updateFences(
                mApiClient,
                new FenceUpdateRequest.Builder()
                        .removeFence(fenceKey)
                        .build()).setResultCallback(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {
                Realm realm = Realm.getDefaultInstance();
                AlarmModel alarmData = realm.where(AlarmModel.class).equalTo("id",Long.valueOf(fenceKey)).findFirst();
                FenceUtil.registerFences(alarmData,getApplicationContext(), mPendingIntent, getParent());
//                Log.i("unregisterFence", getParent().getClass().toString());
                Log.i("unregisterFence", "Fence " + fenceKey + " successfully removed.");
            }

            @Override
            public void onFailure(@NonNull Status status) {
                Log.i("unregisterFence", "Fence " + fenceKey + " could NOT be removed.");
            }
        });
    }

}
