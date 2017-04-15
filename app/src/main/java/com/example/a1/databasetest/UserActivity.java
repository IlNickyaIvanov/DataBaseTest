package com.example.a1.databasetest;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UserActivity extends AppCompatActivity {
    EditText etID;
    static EditText etStartx;
    static EditText etStarty;
    static EditText etEndx;
    static EditText etEndy;
    public static String name,map="";
    public static int lines,columns;
    Button delButton;
    Button saveButton;
    DatabaseHelper sqlHelper;
    static SQLiteDatabase db;
    Cursor userCursor;
    static long userId=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        etStartx = (EditText) findViewById(R.id.etSTX);
        etStarty = (EditText) findViewById(R.id.etSTY);
        etEndx = (EditText) findViewById(R.id.etENDX);
        etEndy = (EditText) findViewById(R.id.etENDY);

        delButton = (Button) findViewById(R.id.deleteButton);
        saveButton = (Button) findViewById(R.id.saveButton);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        db = dbHelper.open();
        //db = ActivityLevelMenu.dbHelperMylvl.getReadableDatabase();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getLong("id");
        }
        // если 0, то добавление
        if (userId > 0) {
            // получаем элемент по id из бд
            userCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                    "_id" + "=?", new String[]{String.valueOf(userId)});
            userCursor.moveToFirst();

            etStartx.setText(userCursor.getString(userCursor.getColumnIndex("startx")));
            etStarty.setText(userCursor.getString(userCursor.getColumnIndex("starty")));
            etEndx.setText(userCursor.getString(userCursor.getColumnIndex("endx")));
            etEndy.setText(userCursor.getString(userCursor.getColumnIndex("endy")));

            userCursor.close();
        } else {
            // скрываем кнопку удаления
            delButton.setVisibility(View.GONE);
        }
    }

    public void coline(View view) {
        switch (view.getId()){
            case (R.id.colplus):
                columns++;
                break;
            case (R.id.colmin):
                columns--;
                break;
            case (R.id.lineplus):
                lines++;
                break;
            case (R.id.linemin):
                lines--;
                break;
            default:break;
        }
    }
    public void save(View view){
        userCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                "_id" + "=?", new String[]{String.valueOf(userId)});
        userCursor.moveToFirst();
        if (userId > 0) Utils.EditAlert("Введите имя:",
                userCursor.getString(userCursor.getColumnIndex("name")),this,this);
        else Utils.EditAlert("Введите имя:",null,this,this);
        userCursor.close();
    }
    public static void upload(){
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_NAME, name);
        int startx = (!etStartx.getText().toString().equals(""))? Integer.parseInt(etStartx.getText().toString()) : 0;
        int starty = (!etStarty.getText().toString().equals(""))? Integer.parseInt(etStarty.getText().toString()) : 0;
        int endx = (!etEndx.getText().toString().equals(""))? Integer.parseInt(etEndx.getText().toString()) : 0;
        int endy = (!etEndy.getText().toString().equals(""))? Integer.parseInt(etEndy.getText().toString()) : 0;
        cv.put("startx", startx);
        cv.put("starty", starty);
        cv.put("endx", endx);
        cv.put("endy", endy);
        cv.put("lines",lines);
        cv.put("columns",columns);
        cv.put("map", map);
        if (userId > 0) {
            db.update(DatabaseHelper.TABLE, cv, DatabaseHelper.COLUMN_ID + "=" + String.valueOf(userId), null);
        } else {
            db.insert(DatabaseHelper.TABLE, null, cv);
        }
    }
    public void delete(View view){
        db.delete(DatabaseHelper.TABLE, "_id = ?", new String[]{String.valueOf(userId)});
        goHome();
    }
    private  void goHome(){
        // закрываем подключение
        db.close();
        // переход к главной activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        this.finish();
    }
}