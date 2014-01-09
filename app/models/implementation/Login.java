package models.implementation;

import models.implementation.User;

public class Login implements models.Login {
    public String user;
    public String password;


    public String validate(){
        if(User.authenticate(this.user, this.password) == null){
            return "invalid user or password";
        }
        return null;
    }
}
