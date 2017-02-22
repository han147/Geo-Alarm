package com.hanjeong.android.geo_alarm;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by hanjeong on 2017. 2. 20..
 */

public class TodoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AlarmModel> alarmList;
    private Context context;

    private ToDoListClickListener toDoListClickListener;

    public TodoListAdapter(List<AlarmModel> alarmList, Context context ,ToDoListClickListener toDoListClickListener) {
        this.alarmList = alarmList;
        this.context = context;
        this.toDoListClickListener = toDoListClickListener;

    }

    public interface ToDoListClickListener {
        void onListItemClick(int clickedItemIndex);
        void onCheckBoxClick(View view, int clickedItemIndex);
    }

    @Override
    public int getItemViewType(int position) {
        return position%2;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case 0:
                View TitleView = inflater.inflate(R.layout.todolist_title, viewGroup, false);
                return new TodoListTitleViewHolder(TitleView);
            case 1:
                View ContentView = inflater.inflate(R.layout.todolist_content, viewGroup, false);
                return new TodoListContentViewHolder(ContentView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AlarmModel alarm = alarmList.get(0);

        switch (getItemViewType(position)) {
            case 0:
                if(position != 0) {
                    alarm = alarmList.get(position/2);
                }
                String title = alarm.getTitle();
                String address = alarm.getAddress();
                TodoListTitleViewHolder titleViewHolder = (TodoListTitleViewHolder) holder;
                titleViewHolder.bind(title, address);

                Log.i("Bind Position",alarm.getTitle()+" : " +Integer.toString(position));
                break;
            case 1:
                alarm = alarmList.get(position/2);
                List<ToDoModel> todoList = alarm.getToDoList();
                TodoListContentViewHolder contentViewHolder = (TodoListContentViewHolder) holder;
                contentViewHolder.bind(todoList);

                Log.i("Bind Position",alarm.getTitle()+" : " +Integer.toString(position));
                break;
        }


    }



    @Override
    public int getItemCount() {
        return alarmList.size()*2;
    }



    class TodoListTitleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.todo_title)
        TextView todoTitleTextView;

        @Bind(R.id.todo_addr)
        TextView todoAddrTextView;

        public TodoListTitleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }
        public void bind(String title, String address) {
            todoTitleTextView.setText(title);
            todoAddrTextView.setText(address);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            toDoListClickListener.onListItemClick(clickedPosition);
            Log.i("holder onclick", Integer.toString(clickedPosition));
        }
    }

    class TodoListContentViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

        @Bind(R.id.todolist_content)
        LinearLayout todoListContent;

        public TodoListContentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
//
//            if(itemView instanceof CheckBox) {
//                ((CheckBox) itemView).setOnCheckedChangeListener(this);
//            }

        }

        public void bind(List<ToDoModel> todoList) {
            TypedArray contentids = context.getResources().obtainTypedArray(R.array.todolist_content_id_array);
            TypedArray checkIds = context.getResources().obtainTypedArray(R.array.todolist_check_id_array);
            for(int i=0;i<todoList.size();i++) {

                LinearLayout oneOfToDo = new LinearLayout(context);
                LinearLayout.LayoutParams lparam = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                oneOfToDo.setLayoutParams(lparam);
                oneOfToDo.setOrientation(LinearLayout.HORIZONTAL);

                TextView contentTextView = new TextView(context);
                contentTextView.setId(contentids.getResourceId(i,i));
                contentTextView.setText(todoList.get(i).getContent());
                contentTextView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

                CheckBox checkBox = new CheckBox(context);
                checkBox.setId(checkIds.getResourceId(i,i));
                checkBox.setChecked(todoList.get(i).isCheck());
                checkBox.setOnCheckedChangeListener(this);
//                checkBox.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);


                oneOfToDo.addView(contentTextView);
                oneOfToDo.addView(checkBox);

                todoListContent.addView(oneOfToDo);

            }
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            toDoListClickListener.onListItemClick(clickedPosition);
            Log.i("holder onclick", Integer.toString(clickedPosition));
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int clickedPosition = getAdapterPosition();
            toDoListClickListener.onCheckBoxClick(buttonView, clickedPosition);
        }
    }
}
