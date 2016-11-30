package dit.ie.translationapp;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Image;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dylan on 16/11/2016.
 */
public class TranslateFragment extends Fragment implements TextToSpeech.OnInitListener{

    final int REQ_CODE_SPEECH_INPUT = 100;
    ImageView mImageView;
    ImageView speakerImage;
    ImageView addPhrase;
    String[] langArray;
    String defaultLanguage;
    TextView output;
    EditText input;
    Spinner spinner;
    Spinner spinner1;
    TextToSpeech tts;
    int localeChangeResult;
    String st;

    LinearLayout micLayout;
    LinearLayout textLayout;
    ImageView micTextActive;
    ImageView textMicActive;
    ImageView textTextActive;

    String usernameFromActivity;

    DBHandler db;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_translate, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        //creates a new database handler activity
        db = new DBHandler(getActivity());

        MainActivity activity = (MainActivity)getActivity();
        usernameFromActivity = activity.sendUsername();

        micLayout = (LinearLayout) getActivity().findViewById(R.id.layoutMicActive);
        textLayout = (LinearLayout) getActivity().findViewById(R.id.LayoutTextActive);

        textTextActive = (ImageView)getView().findViewById(R.id.keypadTextActive);
        textMicActive  = (ImageView)getView().findViewById(R.id.keypadMicActive);
        micTextActive = (ImageView)getView().findViewById(R.id.micTextActive);


        //creates a text to speech instance in the fragment
         tts = new TextToSpeech(getActivity(), this);
        //creates a variable to store speaker image info
        speakerImage = (ImageView) getView().findViewById(R.id.textToSpeech);
        //make the microphone image clickable
        addPhrase = (ImageView)getView().findViewById(R.id.addToPhrases);
        mImageView = (ImageView) getView().findViewById(R.id.image);
        //find textview from layout
        output = (TextView)getView().findViewById(R.id.translateOutput);
        //used to find the edit text in layout
        input = (EditText)getView().findViewById(R.id.EditText);
        //reads from text file. creates array to hold the languages for spinner
        readLanguages();

        //finds spinner in the layout
        spinner = (Spinner)getView().findViewById(R.id.spinner01);

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.spinner_items, langArray);
        adapter.setDropDownViewResource(R.layout.spinner_drop_down);
        spinner.setAdapter(adapter);

        //set a default language to translate to English
        defaultLanguage = "French";
        if(!defaultLanguage.equals(null)) {
            int pos = adapter.getPosition(defaultLanguage);
            spinner.setSelection(pos);
        }

        //used to store the translation details into the database
        addPhrase.setOnClickListener(new ImageView.OnClickListener(){

            @Override
            public void onClick(View v){
                //call the savetranslation method
                saveTranslation();
            }
        });

        //onclick listener for images to change layout
        textMicActive.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                changeLayout();
                showKeyboard(v);
                input.setFocusableInTouchMode(true);
                input.requestFocus();
            }
        });

        //onclick listener for textImageClick when TextInput is active in order to inovke keyboard
        textTextActive.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                showKeyboard(v);
                input.setFocusableInTouchMode(true);
                input.requestFocus();
            }
        });

        micTextActive.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                changeLayout();
            }
        });


        //makes the text output scrollable
        output.setMovementMethod(new ScrollingMovementMethod());

        input.setOnClickListener(new EditText.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideSpeakerOutput(v);
            }
        });


        //checks to see if values are entered into edit text, if there is a value it will translate it
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //only translate if a value has been entered
                if (s.length() > 0) {
                    translateInput();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //listener to see if user has clicked outside of the keyboard
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //if there is no focus then hideKeyboard
                if (!hasFocus) {
                    showSpeakerOutput(v);
                    hideKeyboard(v);

                    //change orientation of the input methods

                } else if(hasFocus){
                    hideSpeakerOutput(v);
                }
            }
        });

        //listener for the spinner
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //translate input if another language has been chosen
                translateInput();
            }

            @Override
            public void onNothingSelected(AdapterView adapter) {

            }
        });

        //listener for text to speech image
        speakerImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if(localeChangeResult == TextToSpeech.LANG_MISSING_DATA ||
                        localeChangeResult == TextToSpeech.LANG_NOT_SUPPORTED){
                    Log.e("TTS", "This language is not supported");
                }else{
                    speakerImage.setEnabled(true);
                    speechOutput();
                }

                //call the speech output function
                speechOutput();
            }
        });

        //onClick Listener for microphone image (start recording user speech and convert to text)
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //calls function for speech input
                speechInput();

            }
        });
    }

    //function to save a chosen translation
    public void saveTranslation(){
        String transFromLang = Locale.getDefault().getDisplayLanguage().toString();
        String translateFromString = input.getText().toString();
        String translateToLang = spinner.getSelectedItem().toString();
        String translateToString = output.getText().toString();


        //add this to the database translation
        db.addTranslation(new TranslationData(usernameFromActivity, transFromLang, translateFromString, translateToLang, translateToString));

        Toast.makeText(getActivity(),"Translation added", Toast.LENGTH_LONG).show();
    }

    //function to swap the layouts appropriately
    public void changeLayout(){
        //if the micLayout is active then hide it
        if(micLayout.getVisibility()==View.VISIBLE){
            micLayout.setVisibility(View.INVISIBLE);
            textLayout.setVisibility(View.VISIBLE);
        }
        //if the text layout is visible
        else if(textLayout.getVisibility()==View.VISIBLE){
            micLayout.setVisibility(View.VISIBLE);
            textLayout.setVisibility(View.INVISIBLE);
        }
    }


    //function to hide the speaker and output for aesthetics
    public void hideSpeakerOutput(View v){

        //hides views below
        //speakerImage.setVisibility(View.INVISIBLE);
        //addPhrase.setVisibility(View.INVISIBLE);
        //mImageView.setVisibility(View.INVISIBLE);

    }

    public void showSpeakerOutput(View v){
        speakerImage.setVisibility(View.VISIBLE);
        output.setVisibility(View.VISIBLE);
        addPhrase.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.VISIBLE);
    }

    //takes in speech input
    public void speechInput(){

        String s = createLocaleCode();
        Locale l = new Locale(s);

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Now");

        try{
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        }catch(ActivityNotFoundException a){//throws exception if speech is not supported
            Toast.makeText(getActivity(),"Speech Not Supported",Toast.LENGTH_SHORT).show();
        }
    }

    //reference: https://mycodeandlife.wordpress.com/2013/02/05/android-tutorials-language-translator-app/
    //Receiving the speech input
    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data){

        super.onActivityResult(reqCode, resCode, data);

        //if the speech was successfully translated to text then translate
        switch(reqCode){
            case REQ_CODE_SPEECH_INPUT:{
                if(resCode == Activity.RESULT_OK && null != data){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    input.setText(result.get(0));

                    //translate the input
                    translateInput();
                }
                break;
            }
        }
    }

    //function to translate text uses the imported jar for Bing translator
    public String translate(String text, String lang) throws Exception{
        //need to set the Client ID and password to complete translations
        Translate.setClientId("C_14410312_");
        Translate.setClientSecret("W3IxHgCKo5iHIfOsMZ/eMu6G5dJ92PuzV88NN5cyVoc=");

        String translated;
        String langCode  = lang.toUpperCase();
        //passes the text and the language we want to translate to the function
        return translated = Translate.execute(text, Language.valueOf(langCode));
    }

    //reads list of languages in through a buffer reader in order to generate a list of languages
    public void readLanguages(){
        BufferedReader br = null;

        try{
            StringBuffer result = new StringBuffer();
            br = new BufferedReader(new InputStreamReader(getActivity().getAssets().open("languages.txt")));

            String lang;

            List<String> ls = new ArrayList<String>();
            while((lang = br.readLine())!=null){
                ls.add(lang);
            }
            langArray = ls.toArray(new String[0]);


            for(int i = 0; i < langArray.length; i++){
                //if the string in language array contains a white space
                if(langArray[i].contains(" ")){

                    //call modify String function
                    langArray[i] = modifyString(langArray[i]);
                }
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    //modifies the String if to include an underscore
    //this is due to an underscore present in the Enums in the language class related to the translation API
    //the values in the list do not contain an underscore and therefor need to be added in order to translate without error
    public String modifyString(String str){
        //use string builder to modify the string
        StringBuilder string = new StringBuilder(str);
        //get position of the white space in the string
        int pos = string.indexOf(" ");
        string.setCharAt(pos, '_');
        return string.toString();
    }

    //method to hide the keyboard from the fragment
    public void hideKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showKeyboard(View v){
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    //changes the local to that set in the spinner
    public void changeLocale(){

        //creates local code from value on spinner
        String st = createLocaleCode();
        Locale l = new Locale(st);

        localeChangeResult = tts.setLanguage(l);
    }

    //creates local code from the value in the spinner
    public String createLocaleCode(){
        //get selected value from the spinner
        String spinnerLang = spinner.getSelectedItem().toString();
        String lng = spinnerLang.toUpperCase();

        //find the local in the ENUM structure in Language class
        st = Language.valueOf(lng).toString();
        return st;
    }


    //reference: https://mycodeandlife.wordpress.com/2013/02/05/android-tutorials-language-translator-app/
    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){


            //get selected value from the spinner
            String spinnerLang = spinner.getSelectedItem().toString();
            String lng = spinnerLang.toUpperCase();

            //find the local in the ENUM structure in Language class
            String st = Language.valueOf(lng).toString();
            Locale l = new Locale(st);

            int result = tts.setLanguage(l);

            if(result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS", "This language is not supported");
            }else{
                speakerImage.setEnabled(true);
                speechOutput();
            }
        }else{
            Log.e("TTS", "Initialization failed");
        }
    }
    //end reference

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    public void speechOutput(){
        String text;
        text = output.getText().toString();
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    //reference: https://mycodeandlife.wordpress.com/2013/02/05/android-tutorials-language-translator-app/
    public void translateInput(){

        String str = input.getText().toString();
        String lCode = spinner.getSelectedItem().toString();

        //this creates a new thread in order to perform the translation
        //the result is then posted back to the main activity thread
        class backgroundTask extends AsyncTask<String, String, String> {

            String translatedText = "";

            @Override
            protected String doInBackground(String... params) {
                try {
                    String text = params[0];
                    String code = params[1];
                    translatedText = translate(text, code);
                } catch (Exception e) {
                    e.printStackTrace();
                    translatedText = e.toString();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                output.setText(translatedText);
                super.onPostExecute(result);
            }
        }
        new backgroundTask().execute(str, lCode);

        //end reference

        changeLocale();
    }
}
