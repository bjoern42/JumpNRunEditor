package models.implementation;

import java.util.Map;
import java.util.HashMap;

public class User{
    private static Map<String, String> HARDCODED_USERS = new HashMap<String,String>();
    private String user;

    static{
        HARDCODED_USERS.put("Tobi", "42");
        HARDCODED_USERS.put("Björn", "42");
    }

    public static User authenticate(String user, String password){
        if(HARDCODED_USERS.get(user) != null && HARDCODED_USERS.get(user).equals(password)){
            return new User(user);
        }
        return null;
    }

    public User(String user){
        this.user = user;
    }

    public String getUsername(){
        return user;
    }
}
