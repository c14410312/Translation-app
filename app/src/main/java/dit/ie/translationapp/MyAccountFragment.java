package dit.ie.translationapp;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by dylan on 27/11/2016.
 */
public class MyAccountFragment extends Fragment{

    TextView loggedInusername;
    EditText newPass;
    Button updateBut;
    Button deleteBut;

    String curUname;
    DBHandler db;
    AlertDialogManager alert;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_myaccount, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        db = new DBHandler(getActivity());
        final MainActivity activity = (MainActivity)getActivity();

        alert = new AlertDialogManager();


        //variables needed for update and delete
        loggedInusername = (TextView)getActivity().findViewById(R.id.currentUname);
        newPass = (EditText)getActivity().findViewById(R.id.newPassword);
        updateBut = (Button)getActivity().findViewById(R.id.updateBut);
        deleteBut = (Button)getActivity().findViewById(R.id.deleteAcc);

        curUname = activity.sendUsername();
        loggedInusername.setText("Username: " + curUname);



        //set onclick listeners for buttons and edit text
        updateBut.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                db.updateUserPass(curUname, newPass.toString());
                Toast.makeText(getActivity(),"Password has been updated", Toast.LENGTH_LONG).show();
            }
        });

        deleteBut.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), curUname + " :is now deleted ", Toast.LENGTH_LONG).show();
                db.deleteUser(curUname);
                activity.logoutUser();
            }
        });
    }


}
