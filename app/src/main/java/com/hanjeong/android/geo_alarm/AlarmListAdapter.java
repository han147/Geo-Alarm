package com.hanjeong.android.geo_alarm;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by hanjeong on 2017. 2. 18..
 */

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.AlarmListViewHolder>{

    private List<AlarmModel> alarmList;
    private Context context;
    private ListItemClickListener listItemClickListener;



    public AlarmListAdapter(List<AlarmModel> alarmList, Context context, ListItemClickListener listItemClickListener) {
        this.alarmList = alarmList;
        this.context = context;
        this.listItemClickListener = listItemClickListener;
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
        void onSwitchChange(int changeItemIndex, Boolean isChecked);
        void onClickDeleteButton(int clickedItemIndex);
    }

    @Override
    public AlarmListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.alarm_list_item, viewGroup, false);
        AlarmListViewHolder alarmListViewHolder = new AlarmListViewHolder(view);



        return alarmListViewHolder;
    }

    @Override
    public void onBindViewHolder(AlarmListViewHolder holder, int position) {
        AlarmModel alarm = alarmList.get(position);
        String title = alarm.getTitle();
        String address = alarm.getAddress();
        Boolean onoff = alarm.getOnoff();

        holder.bind(title, address, onoff);

    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    class AlarmListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

        @Bind(R.id.alarm_title)
        TextView alarmTitleTextView;

        @Bind(R.id.alarm_address)
        TextView alarmAddressTextView;

        @Bind(R.id.onoff_switch)
        Switch onoffSwitch;

        public AlarmListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

            onoffSwitch.setOnCheckedChangeListener(this);
        }

        public void bind(String title, String address, Boolean alarmOn) {
            alarmTitleTextView.setText(title);
            alarmAddressTextView.setText(address);
            onoffSwitch.setChecked(alarmOn);


        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            listItemClickListener.onListItemClick(clickedPosition);
            Log.i("alarm clicked position", Integer.toString(clickedPosition));

        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            buttonView.setOnClickListener(new );
//            int clickedPosition = getAdapterPosition();
//            listItemClickListener.onSwitchChange(clickedPosition, isChecked);
        }

        @OnClick(R.id.onoff_switch)
        public void checkedChanged() {
            Boolean isChecked = onoffSwitch.isChecked();
            int clickedPosition = getAdapterPosition();
            listItemClickListener.onSwitchChange(clickedPosition, isChecked);

        }

        @OnClick(R.id.alarm_delete_button)
        public void onClickDeleteButton() {
            int clickedPosition = getAdapterPosition();
            listItemClickListener.onClickDeleteButton(clickedPosition);
        }
    }
}
