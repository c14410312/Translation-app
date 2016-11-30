package dit.ie.translationapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by dylan on 14/11/2016.
 */
public class LoginActivity extends AppCompatActivity{

    EditText username, password;
    Button loginButton;
    Button registerButton;
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
    //Session Manager
    SessionManager ses;
    //database
    DBHandler db;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //create a new session manager instance
        ses = new SessionManager(getApplicationContext());

        //create database instance
        db = new DBHandler(this);
        //get the input values from user
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        //check if the user is logged in or not
        Toast.makeText(getApplicationContext(), "User Login Status: " + ses.isLoggedIn(), Toast.LENGTH_LONG).show();

        loginButton = (Button)findViewById(R.id.LoginButton);
        registerButton = (Button)findViewById(R.id.RegisterButton);

        //listener for the register button
        registerButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                //starts up the registration activity if the button is clicked
                Intent i = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(i);
                finish();
            }
        });

        //create a listener for the click event on the button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //create varaibles for username and password
                String uname = username.getText().toString();
                String pword = password.getText().toString();

                //check if the username or password box is filled
                if(uname.trim().length() > 0 && pword.trim().length()>0){
                    //check if the username matches the inputed value.
                    //check if password associated with that username is identical
                    if(isUserValid(uname, pword)){
                        ses.createLoginSession(uname, pword);

                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();
                    }else{
                        //show error message
                        // username / password doesn't match
                        alert.showAlertDialog(LoginActivity.this, "Login failed..", "Username/Password is incorrect", false);
                    }
                }else{
                    // username / password doesn't match
                    alert.showAlertDialog(LoginActivity.this, "Login failed..", "Please enter valid username and password", false);
                }
            }
        });
    }

    public boolean isUserValid(String username, String password){

        User user;
        Cursor result;

        //returns the cursor into result
        result = db.getUser(username);

        if (result != null && result.moveToFirst()) {
            user = new User(Integer.parseInt(result.getString(0)),
                    result.getString(1), result.getString(2));
            //Check if the users password matches entered password
            if(user.getPassword().equals(password)){
                return true;
            }else{
                return false;
            }
        }
        //calls the search method on the database
        //isvalid stores the result of the search query (true:false)

        return false;
    }
}
