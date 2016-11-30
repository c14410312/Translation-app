package dit.ie.translationapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by dylan on 16/11/2016.
 */
public class DrawerItemCustomAdapter extends ArrayAdapter<ObjectDrawerItem> {

    Context mContext;
    int layoutResourceId;
    ObjectDrawerItem data[] = null;

    public DrawerItemCustomAdapter(Context mContext, int layoutResourceId, ObjectDrawerItem[] data){
        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    //Reference: http://www.androidcode.ninja/android-navigation-drawer-example/
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View listItem = convertView;

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        ImageView imageViewIcon = (ImageView)listItem.findViewById(R.id.imageView);
        TextView textView = (TextView)listItem.findViewById(R.id.tVname);

        ObjectDrawerItem folder = data[position];

        imageViewIcon.setImageResource(folder.icon);
        textView.setText(folder.name);

        return listItem;

        //end reference
    }

}
