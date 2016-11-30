package dit.ie.translationapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;


/**
 * Created by dylan on 14/11/2016.
 */

//http:// Reference: www.androidhive.info/2012/08/android-session-management-using-shared-preferences/
public class SessionManager {

    //Shared preferences used to store user session details
    SharedPreferences preference;
    //interface used to modify values in a shared pref object
    SharedPreferences.Editor editor;
    //interface to global information about an applications environment
    //used for intents and launching activities
    Context _context;

    int PRIVATE_MODE = 0;
    //file name used for the shared preference
    private static final String PREF_NAME = "TranslationAppPref";
    //all shared preference keys
    private static final String IS_LOGIN = "IsLoggedIn";
    //users name who is starting the session
    public static final String KEY_NAME="name";
    public static final String KEY_PASSWORD="password";

    //constructor
    public SessionManager(Context context){
        this._context = context;
        preference = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = preference.edit();
    }

    //create the login session to store the information about the user.
    public void createLoginSession(String name, String password){
        //Store login value as true
        editor.putBoolean(IS_LOGIN, true);
        //store the given name in the preference
        editor.putString(KEY_NAME, name);
        //store the given email
        editor.putString(KEY_PASSWORD, password);
        //commit the changes
        editor.commit();
    }

    //Get the stored session data by using a hashmap
    public HashMap<String, String> getUserDetails(){
        //create a new hashmap instance
        HashMap<String, String> user = new HashMap<String, String>();

        //stored users name
        user.put(KEY_NAME, preference.getString(KEY_NAME, null));

        //stored users email
        user.put(KEY_PASSWORD, preference.getString(KEY_PASSWORD, null));
        return user;
    }

    public void checkLogin(){
        if(!this.isLoggedIn()){
            //user is not currently logged in so return them to the login activity
            Intent i = new Intent(_context, LoginActivity.class);
            //close all activites related
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    //on logout we need to clear all the session details
    public void logoutUser(){
        //this clears all the data from the shared preference
        editor.clear();
        editor.commit();

        //need to redirect the user back to the login screen activity on logout
        Intent i = new Intent(_context, LoginActivity.class);
        //close all the opened activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //add new flag to start new activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //start the login activity
        _context.startActivity(i);
    }

    //used to check to see i a user is logged in
    public boolean isLoggedIn(){
        return preference.getBoolean(IS_LOGIN, false);
    }


    //end reference
}
