package dit.ie.translationapp;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import android.speech.tts.TextToSpeech;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by dylan on 27/11/2016.
 */
public class TranslateItemCustomeAdapter extends ArrayAdapter<TranslationData> implements View.OnClickListener{


    private ArrayList<TranslationData> dset;
    Context context;

    private static class ViewHolder{
        TextView transTo;
        TextView transToString;
        TextView transFrom;
        TextView transFromString;
        ImageView img;
    }

    public TranslateItemCustomeAdapter(ArrayList<TranslationData> data, Context context){
        super(context, R.layout.translations_row_item, data);
        this.dset = data;
        this.context = context;
    }


    @Override
    public void onClick(View v){
        int position = (Integer) v.getTag();
        Object obj = getItem(position);
        TranslationData translationData = (TranslationData)obj;
    }

    // Reference: The following code is from http://www.journaldev.com/10416/android-listview-with-custom-adapter-example-tutorial

    //convert view used to reuse the old view of possible
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //get the item related to this position
        TranslationData translationData = getItem(position);

        ViewHolder viewHolder;

        final View result;

        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.translations_row_item, parent, false);
            viewHolder.transFrom = (TextView) convertView.findViewById(R.id.translateFrom);
            viewHolder.transFromString = (TextView) convertView.findViewById(R.id.translateFromString);
            viewHolder.transTo = (TextView) convertView.findViewById(R.id.translateTo);
            viewHolder.transToString = (TextView) convertView.findViewById(R.id.translateToString);
            //viewHolder.img = (ImageView)convertView.findViewById(R.id.speakerimg);

            //make it scrollable
            viewHolder.transToString.setMovementMethod(new ScrollingMovementMethod());
            result=convertView;

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
            result = convertView;
        }

        viewHolder.transFrom.setText(translationData.getTranslateFrom());
        viewHolder.transFromString.setText(translationData.getTranslateFromString());
        viewHolder.transTo.setText(translationData.getTranslateTo());
        viewHolder.transToString.setText(translationData.getTranslateToString());
        //viewHolder.img.setOnClickListener(this);
        //viewHolder.img.setTag(position);

        return convertView;
    }
    //END REFERENCE
}
