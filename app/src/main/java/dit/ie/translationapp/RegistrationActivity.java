package dit.ie.translationapp;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dylan on 14/11/2016.
 */
public class RegistrationActivity extends AppCompatActivity {

    AlertDialogManager alert;
    EditText username, password;
    Button register;
    DBHandler db;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        username = (EditText) findViewById(R.id.regusername);
        password = (EditText) findViewById(R.id.regpassword);
        register = (Button) findViewById(R.id.RegisterButton);
        db = new DBHandler(this);
        alert = new AlertDialogManager();
        //creates a new database handler to store tables


        //listener for the registration button
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String uname = username.getText().toString();
                String pword = password.getText().toString();

                //ensure username is not already taken and also that password is greater than 7 character (includes numbers and letters)
                if(uname.trim().length() > 0 && isUsernameValid(uname)== false && isPassValid(pword)) {
                    db.addUser(new User(uname, pword));
                    Toast.makeText(getApplicationContext(), uname + " :is now registered ", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    if (isPassValid(pword) == false){
                        //alert indicating that there is a problem with password
                        alert.showAlertDialog(RegistrationActivity.this, "Password not valid..", "Password must be at least 8 characters and contain only letters and numbers", false);
                    }
                    //if the password is fine then the error lies within the username
                    else{
                        alert.showAlertDialog(RegistrationActivity.this, "Username already in use..", "Please choose another username", false);
                    }
                }

            }
        });
    }

    //function to check if the password entered is valid or not
    public boolean isPassValid(String pass){
        String password = pass;
        //regex for password must contain letters numbers no whitespace @least 8 characters
        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }
    //function that checks if user name is not already taken
    public boolean isUsernameValid(String username){
        String uname = username;
        //calls the search method on the database
        return db.searchUser(uname);
    }
}
