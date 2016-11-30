package dit.ie.translationapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dylan on 08/11/2016.
 */
public class DBHandler extends SQLiteOpenHelper {

    //Database version
    private static final int DATABASE_VERSION = 1;
    //Database name
    private static final String DATABASE_NAME = "translationsAppDB";
    //User table name
    private static final String TABLE_USERS = "users";
    //tranlations table name
    private static final String TABLE_TRANSLATIONS = "translations";

    //User table columns names
    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    //translations table column names
    private static final String KEY_TRANS_ID = "tid";
    private static final String KEY_TRANS_USERNAME = "tuname";
    private static final String KEY_FROM_LANG = "fromlang";
    private static final String KEY_FROM_LANG_STRING = "fromlangstring";
    private static final String KEY_TO_LANG = "tolang";
    private static final String KEY_TO_LANG_STRING = "tolangstring";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USERNAME + " TEXT,"
                + KEY_PASSWORD + " TEXT" + ")";


        String CREATE_TRANSLATIONS_TABLE = "CREATE TABLE " + TABLE_TRANSLATIONS + "("
                + KEY_TRANS_ID + " INTEGER PRIMARY KEY," + KEY_TRANS_USERNAME + " TEXT,"
                + KEY_FROM_LANG + " TEXT,"
                + KEY_FROM_LANG_STRING + " TEXT," + KEY_TO_LANG + " TEXT,"
                + KEY_TO_LANG_STRING + " TEXT" + ")";

        //executes sql statement on a selected table
        db.execSQL(CREATE_CONTACTS_TABLE);
        Log.d("table", "created");

        //executes sql statement on a selected table
        db.execSQL(CREATE_TRANSLATIONS_TABLE);
        Log.d("translations table", "created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLES IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLES IF EXISTS " + TABLE_TRANSLATIONS);
        //create tables again
        onCreate(db);
    }

    //CRUD OPERATIONS

    //INSERTING A NEW USER
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_PASSWORD, user.getPassword());

        //inserting the newly created row
        db.insert(TABLE_USERS, null, values);
        //close the db connection
        db.close();
    }

    //adds a translation
    public void addTranslation(TranslationData translationData){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TRANS_USERNAME, translationData.getUsername());
        values.put(KEY_FROM_LANG, translationData.getTranslateFrom());
        values.put(KEY_FROM_LANG_STRING, translationData.getTranslateFromString());
        values.put(KEY_TO_LANG, translationData.getTranslateTo());
        values.put(KEY_TO_LANG_STRING, translationData.getTranslateToString());

        db.insert(TABLE_TRANSLATIONS, null, values);
        db.close();
    }

    public boolean searchUser(String user){
        SQLiteDatabase db = this.getReadableDatabase();
        //create a cursor to store results of a query to see if name already exists or not
        Cursor cursor = db.query(true, TABLE_USERS, null, KEY_USERNAME + "= '" + user +"'", null, null, null, null, null);
        if ((cursor.getCount() == 0) || !cursor.moveToFirst()) {
            db.close();
            return false;
        }else{
            db.close();
            return true;
        }

    }

    //gets all translations associated with a given user
    public ArrayList<TranslationData> getUserTranslations(String username){

        ArrayList<TranslationData> translationData = new ArrayList<TranslationData>();
        String selectQuery = "SELECT * FROM " + TABLE_TRANSLATIONS + " WHERE " + KEY_TRANS_USERNAME + " LIKE '" + username +"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //reference: http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/

        //loop through all rows in the database anad add them to the list
        if(cursor.moveToFirst()){
            do{
                TranslationData td = new TranslationData();
                td.setId(Integer.parseInt(cursor.getString(0)));
                td.setTranslateFrom(cursor.getString(2));
                td.setTranslateFromString(cursor.getString(3));
                td.setTranslateTo(cursor.getString(4));
                td.setTranslateToString(cursor.getString(5));

                //adding to the contact list
                translationData.add(td);
            } while (cursor.moveToNext());
        }

        //end reference
        //return the contact list
        return translationData;
    }

    //READING USER RECORDS
    //getting one user details
    public Cursor getUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user;
        Cursor cursor = db.query(true, TABLE_USERS, null, KEY_USERNAME + "= '" + username +"'",null, null, null, null, null);
        return cursor;
    }


    //UPDATING A USER
    public int updateUserPass(String username, String newPass){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_PASSWORD, newPass);

        //update the row
        return db.update(TABLE_USERS, values, KEY_USERNAME+ "=?"
                , new String[]{String.valueOf(username)});
    }

    //deletes a user from the database
    public void deleteUser(String username){
        SQLiteDatabase db = this.getWritableDatabase();

        //delete user from the user table using the username
        db.delete(TABLE_USERS, KEY_USERNAME + "=?", new String[]
                {String.valueOf(username)});

        //delets all translations associated with that user
        db.delete(TABLE_TRANSLATIONS, KEY_TRANS_USERNAME + "=?", new String[]
                {String.valueOf(username)});

        //close the database
        db.close();
    }
}