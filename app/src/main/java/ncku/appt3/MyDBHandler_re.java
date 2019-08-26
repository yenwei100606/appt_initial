package ncku.appt3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class MyDBHandler_re extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DB.db";
    public static final String TABLE_PRODUCTS = "data";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_VALUE = "_value";
    public static final String COLUMN_DATE = "_date";

    //We need to pass database information along to superclass
    public MyDBHandler_re(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
        String query = "CREATE TABLE " + TABLE_PRODUCTS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_VALUE + " TEXT, " +COLUMN_DATE+" TEXT"+
                ");";
                */
        String query = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT, " + COLUMN_VALUE +" TEXT"+
                ");";
        db.execSQL(query);
    }

    //Lesson 51
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    //Add a new row to the database
    public long addProduct(object_re object){
        long la;
        ContentValues values = new ContentValues();
        values.put(COLUMN_VALUE, object.get_value());
        values.put(COLUMN_DATE, object.get_date());
        SQLiteDatabase db = getWritableDatabase();
        la=db.insert(TABLE_PRODUCTS, null, values);
        //db.close();
        return la;
    }


    //Delete a product from the database
    public void deleteProduct(String id){
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_ID + " = " + id + ";");
    }


    //for string
    public String[] databaseToString(){
        int a=0;
        ArrayList myList = new ArrayList();
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE 1";// why not leave out the WHERE  clause?

        //Cursor points to a location in your results
        Cursor recordSet = db.rawQuery(query, null);
        //Move to the first row in your results
        recordSet.moveToFirst();

        //Position after the last row means the end of the results
        while (!recordSet.isAfterLast()) {
            // null could happen if we used our empty constructor
            if (recordSet.getString(recordSet.getColumnIndex("_id")) != null) {
                myList.add( recordSet.getString(recordSet.getColumnIndex("_date")));
            }
            recordSet.moveToNext();
        }
        db.close();
        Object list[] = myList.toArray();
        String[] str = Arrays.copyOf(list, list.length, String[].class);
        return str;
    }

    // for property class
    public ArrayList<Property> databaseToString_value(){
        int a=0;
        ArrayList<Property> myList = new ArrayList<>();
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE 1";// why not leave out the WHERE  clause?

        //Cursor points to a location in your results
        Cursor recordSet = db.rawQuery(query, null);
        //Move to the first row in your results
        recordSet.moveToLast();

        //Position after the last row means the end of the results
        while (!recordSet.isBeforeFirst()) {
            // null could happen if we used our empty constructor
            if (recordSet.getString(recordSet.getColumnIndex("_id")) != null) {
                myList.add( new Property(recordSet.getString(recordSet.getColumnIndex("_date")),recordSet.getString(recordSet.getColumnIndex("_value"))));
            }
            recordSet.moveToPrevious();
        }
        db.close();
        return myList;
    }

    //for object_re
    public ArrayList<object_id> databaseToString_id(){
        int a=0;
        ArrayList<object_id> myList = new ArrayList<>();
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE 1";// why not leave out the WHERE  clause?

        //Cursor points to a location in your results
        Cursor recordSet = db.rawQuery(query, null);
        //Move to the first row in your results
        recordSet.moveToLast();

        //Position after the last row means the end of the results
        while (!recordSet.isBeforeFirst()) {
            // null could happen if we used our empty constructor
            if (recordSet.getString(recordSet.getColumnIndex("_id")) != null) {
                myList.add( new object_id(recordSet.getString(recordSet.getColumnIndex("_date")),recordSet.getString(recordSet.getColumnIndex("_value")), recordSet.getInt(recordSet.getColumnIndex("_id"))));
            }
            recordSet.moveToPrevious();
        }
        db.close();
        return myList;
    }
}
