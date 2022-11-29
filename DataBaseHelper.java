package com.example.myapplication;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.myapplication.ToDoAdapter.ToDoModel;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends EventService {

    private SQLiteDatabase db;

    private static  final String DATABASE_NAME = "TODO_LIST";
    private static  final String TABLE_NAME = "TODO_TABLE";
    private static  final String ID = "ID";
    private static  final String TASK = "TASK";
    private static  final String STATUS = "STATUS";
    public DataBaseHelper(Context ctx) {
        super(ctx);
    }
    public void insertTask(ToDoModel model){
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID , model.getTask());
        values.put(STATUS , 0);
        db.insert(TABLE_NAME , null , values);
    }

    public void updateTask(int id , String task){
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TASK , task);
        db.update(TABLE_NAME , values , "ID=?" , new String[]{String.valueOf(id)});
    }

    public void updateStatus(int id , int status){
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STATUS , status);
        db.update(TABLE_NAME , values , "ID=?" , new String[]{String.valueOf(id)});
    }


    public void deleteTask(int id ){
        db = this.getWritableDatabase();
        db.delete(TABLE_NAME , "ID=?" , new String[]{String.valueOf(id)});
    }

    public List<ToDoModel> getAllTasks(){

        db = this.getWritableDatabase();
        Cursor cursor = null;
        List<ToDoModel> modelList = new ArrayList<>();

        db.beginTransaction();
        try {
            cursor = db.query(TABLE_NAME , null , null , null , null , null , null);
            if (cursor !=null){
                if (cursor.moveToFirst()){
                    do {
                        ToDoModel task = new ToDoModel();
                       // task.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                       // task.setTask(cursor.getString(cursor.getColumnIndex(TASK)));
                       // task.setStatus(cursor.getInt(cursor.getColumnIndex(STATUS)));
                        modelList.add(task);

                    }while (cursor.moveToNext());
                }
            }
        }finally {
            db.endTransaction();
            cursor.close();
        }
        return modelList;
    }

}