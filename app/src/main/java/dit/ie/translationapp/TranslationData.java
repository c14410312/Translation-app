package dit.ie.translationapp;

/**
 * Created by dylan on 27/11/2016.
 */
public class TranslationData {
    int id;
    String translateFrom;
    String translateFromString;
    String translateTo;
    String translateToString;
    String username;

    public TranslationData(){

    }
    public TranslationData(String username, String translateFrom, String translateFromString, String translateTo, String translateToString){
        this.username = username;
        this.translateFrom = translateFrom;
        this.translateFromString = translateFromString;
        this.translateTo = translateTo;
        this.translateToString = translateToString;
    }

    public TranslationData(int id, String username, String translateFrom, String translateFromString, String translateTo, String translateToString){
        this.id = id;
        this.username = username;
        this.translateFrom = translateFrom;
        this.translateFromString = translateFromString;
        this.translateTo = translateTo;
        this.translateToString = translateToString;
    }

    //getters
    public String getUsername(){return username;}
    public String getTranslateFrom(){
        return translateFrom;
    }

    public String getTranslateFromString(){
        return translateFromString;
    }

    public String getTranslateTo(){
        return translateTo;
    }

    public String getTranslateToString(){
        return translateToString;
    }

    //setters
    public void setId(int id){this.id = id;}

    public void setTranslateFrom(String str){
        this.translateFrom = str;
    }

    public void setTranslateFromString(String str){
        this.translateFromString = str;
    }

    public void setTranslateTo(String str){
        this.translateTo = str;
    }

    public void setTranslateToString(String str){
        this.translateToString = str;
    }

}
