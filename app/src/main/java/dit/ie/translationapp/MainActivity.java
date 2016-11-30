package dit.ie.translationapp;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//imports needed for the Bing Translator
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import java.util.HashMap;

/**
 * Created by dylan on 14/11/2016.
 */
public class MainActivity extends AppCompatActivity {

    private String[] mNavDrawItemTitles;
    private DrawerLayout mDrawLay;
    private ListView mDrawList;
    private ImageView mImageView;

    private ActionBarDrawerToggle mDrawTogg;

    //set the properties for the title display
    private CharSequence mDrawTitle;
    private CharSequence mTitle;

    HashMap<String, String> user;

    SessionManager ses;
    Button butLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = mDrawTitle = getTitle();

        //Call a new session class instance
        ses = new SessionManager(getApplicationContext());
        TextView lblUsername = (TextView)findViewById(R.id.lblUsername);
        TextView lblEmail = (TextView)findViewById(R.id.lblEmail);

        user = ses.getUserDetails();
        ses.checkLogin();

        //Drawer Navigation initialisation
        mNavDrawItemTitles = getResources().getStringArray(R.array.nav_draw_item_arr);
        mDrawLay = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawList = (ListView)findViewById(R.id.left_drawer);

        //List the Drawer Items
        ObjectDrawerItem[] dItem = new ObjectDrawerItem[4];

        dItem[0] = new ObjectDrawerItem(R.mipmap.translate, "Translate");
        dItem[1] = new ObjectDrawerItem(R.mipmap.phrasebooks, "My Phrasebooks");
        dItem[2] = new ObjectDrawerItem(R.mipmap.myaccount, "My Account");
        dItem[3] = new ObjectDrawerItem(R.mipmap.logout, "Logout");

        //Create the adapter
        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.listview_item_row, dItem);

        //set the adapter
        mDrawList.setAdapter(adapter);

        //Create a listener for menu item click
        mDrawList.setOnItemClickListener(new DrawerItemClickListener());

        //reference: http://www.androidcode.ninja/android-navigation-drawer-example/
        mDrawLay = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawTogg = new ActionBarDrawerToggle(
                this,
                mDrawLay,
                R.string.drawer_open,
                R.string.drawer_close
        ) {

            //called when a drawer is closed and in a completely settled state
            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
            }

            //called when a drawer is settled and completely open
            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawTitle);
                mDrawList.bringToFront();
                mDrawLay.requestLayout();
            }
        };

        mDrawLay.addDrawerListener(mDrawTogg);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //end reference

        if (savedInstanceState ==  null){
            selectItem(0);
        }


        //Button to logout the user
       // butLogout = (Button)findViewById(R.id.btnLogout);

        Toast.makeText(getApplicationContext(), "User Login Status: " + ses.isLoggedIn(), Toast.LENGTH_LONG).show();


        String name = user.get(SessionManager.KEY_NAME);
        String password = user.get(SessionManager.KEY_PASSWORD);


    }

    //Reference: //reference: http://www.androidcode.ninja/android-navigation-drawer-example/
    // to change up caret
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawTogg.syncState();
    }

    //gets the position of the item selected in the drawer
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            selectItem(position);
        }
    }

    //end reference

    public String sendUsername(){
        String uname = user.get(SessionManager.KEY_NAME);
        return uname;
    }

    public String sendPassword(){
        String pword = user.get(SessionManager.KEY_PASSWORD);
        return pword;
    }

    //reference: http://www.androidcode.ninja/android-navigation-drawer-example/

    private void selectItem(int position){

        Fragment fragment = null;


        switch (position){
            case 0:
                fragment = new TranslateFragment();
                break;
            case 1:
                fragment = new PhrasebooksFragment();
                break;
            case 2:
                fragment = new MyAccountFragment();
                break;
            case 3:
                logoutUser();
            default:
                break;
        }

        //end reference

        if(fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            mDrawList.setItemChecked(position, true);
            mDrawList.setSelection(position);
            setTitle(mNavDrawItemTitles[position]);
            mDrawLay.closeDrawer(mDrawList);
        }else{
            Log.e("MainActivity", "Error in Creating Fragment");
        }
    }


    //reference: http://www.androidcode.ninja/android-navigation-drawer-example/
    //used to make the app icon a toggle function
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(mDrawTogg.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //end reference

    public void setTitle(CharSequence title){
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    public void logoutUser(){
        ses.logoutUser();
    }


}
