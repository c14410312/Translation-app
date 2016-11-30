package dit.ie.translationapp;

/**
 * Created by dylan on 14/11/2016.
 */
public class User {
    private int _id;
    private String username;
    private String password;


    //default constructor
    public User(){
    }

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public User(int id,String username, String password){
        this._id = id;
        this.username = username;
        this.password = password;
    }

    //setters
    public void setId(int id){
        this._id = id;
    }

    public void setUsername(String username){
        this.username=username;
    }

    public void setPassword(String password){
        this.password=password;
    }


    //getters
    public int getId(){
        return _id;
    }

    public String getUsername(){
        return username;
    }
    public String getPassword(){
        return password;
    }

}
