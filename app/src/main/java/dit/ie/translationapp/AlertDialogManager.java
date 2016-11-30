package dit.ie.translationapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

//reference: http://www.androidhive.info/2012/08/android-session-management-using-shared-preferences/
/**
 * Created by dylan on 15/11/2016.
 */
public class AlertDialogManager {

    public void showAlertDialog(Context context, String title, String message,
    Boolean status){

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        //setting the dialog title
        alertDialog.setTitle(title);
        //setting the dialog message
        alertDialog.setMessage(message);

        if(status != null){
            //set the icon
            alertDialog.setIcon((status) ? R.mipmap.success : R.mipmap.fail);
        }

        //setting the Okay Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener(){
           public void onClick(DialogInterface dialog, int which){
           }
        });

        //show the alert message
        alertDialog.show();
    }

    //end reference
}
