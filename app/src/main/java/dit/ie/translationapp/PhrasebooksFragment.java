package dit.ie.translationapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by dylan on 16/11/2016.
 */
public class PhrasebooksFragment extends Fragment {

    DBHandler db;
    ArrayList<TranslationData> translationDatas;
    ListView listView;
    private static TranslateItemCustomeAdapter translateItemCustomeAdapter;
    public PhrasebooksFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_phrasebooks, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        db = new DBHandler(getActivity());
        MainActivity activity = (MainActivity)getActivity();
        listView = (ListView)getView().findViewById(R.id.translationsList);
        translationDatas = new ArrayList<>();

        //get all translations for the logged in user from the database

        translationDatas = db.getUserTranslations(activity.sendUsername());


        translateItemCustomeAdapter = new TranslateItemCustomeAdapter(translationDatas,getActivity());
        listView.setAdapter(translateItemCustomeAdapter);
    }
}
