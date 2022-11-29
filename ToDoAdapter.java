package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;
import android.content.DialogInterface;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {

    private List<ToDoModel> mList;
    private EventToDoList activity;
    private DataBaseHelper myDB;

    public ToDoAdapter(DataBaseHelper myDB , EventToDoList activity){
        this.activity = activity;
        this.myDB = myDB;

    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_to_do_list , parent , false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ToDoModel item = mList.get(position);
        holder.todoCheckBox.setText(item.getTask());
        holder.todoCheckBox.setChecked(toBoolean(item.getStatus()));
        holder.todoCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    myDB.updateStatus(item.getId() , 1);
                }else
                    myDB.updateStatus(item.getId() , 0);
            }
        });
    }

    public boolean toBoolean(int num){
        return num!=0;
    }

    public Context getContext(){
        return activity;
    }

    public void setTasks(List<ToDoModel> mList){
        this.mList = mList;
        notifyDataSetChanged();
    }

    public void deleteTask(int position){
        ToDoModel item = mList.get(position);
        myDB.deleteTask(item.getId());
        mList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position){
        ToDoModel item = mList.get(position);

        Bundle bundle = new Bundle();
        bundle.putInt("id" , item.getId());
        bundle.putString("task" , item.getTask());

        AddNewTask task = new AddNewTask();
        task.setArguments(bundle);
        task.show(activity.getSupportFragmentManager() , task.getTag());


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        CheckBox todoCheckBox;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            todoCheckBox = itemView.findViewById(R.id.todoCheckBox);
        }
    }

    public static class ToDoModel {
        private int id, status;
        private String task;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getTask() {
            return task;
        }

        public void setTask(String task) {
            this.task = task;
        }
    }

    public static class AddNewTask extends BottomSheetDialogFragment {
        public static final String TAG = "AddNewTask";

        //widgets
        private EditText mEditText;
        private Button mSaveButton;

        private DataBaseHelper myDb;

        public static AddNewTask newInstance(){
            return new AddNewTask();
        }
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.activity_to_do_list, container , false);
            return v;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            mEditText = view.findViewById(R.id.newTaskText);
            mSaveButton = view.findViewById(R.id.newTaskButton);

            myDb = new DataBaseHelper(getActivity());

            boolean isUpdate = false;

            final Bundle bundle = getArguments();
            if (bundle != null){
                isUpdate = true;
                String task = bundle.getString("task");
                mEditText.setText(task);

                if (task.length() > 0 ){
                    mSaveButton.setEnabled(false);
                }

            }
            mEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().equals("")){
                        mSaveButton.setEnabled(false);
                        mSaveButton.setBackgroundColor(Color.GRAY);
                    }else{
                        mSaveButton.setEnabled(true);
                        mSaveButton.setBackgroundColor(getResources().getColor(R.color.primary));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            final boolean finalIsUpdate = isUpdate;
            mSaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = mEditText.getText().toString();

                    if (finalIsUpdate){
                        myDb.updateTask(bundle.getInt("id") , text);
                    }else{
                        ToDoModel item = new ToDoModel();
                        item.setTask(text);
                        item.setStatus(0);
                        myDb.insertTask(item);
                    }
                    dismiss();

                }
            });
        }

        @Override
        public void onDismiss(@NonNull DialogInterface dialog) {
            super.onDismiss(dialog);
            Activity activity = getActivity();
            if (activity instanceof OnDialogCloseListener){
                ((OnDialogCloseListener)activity).onDialogClose(dialog);
            }
        }
    }

    public interface OnDialogCloseListener {
        void onDialogClose(DialogInterface dialogInterface);

    }
}
