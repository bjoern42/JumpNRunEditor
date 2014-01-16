package controllers;

import play.*;
import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import models.implementation.Login;
import models.implementation.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import play.libs.F.Promise;
import play.libs.OpenID;

public class Application extends Controller {
    private static List<String> users = new ArrayList<String>();

    public static List<String> getUsers(){
        if(session().get("user") == null){
            return null;
        }
        return users;
    }

    public static Result index() {
        return redirect(controllers.routes.Editor.show(0));
    }

    public static Result javascriptRoutes() {
        response().setContentType("text/javascript");
        return ok(
                Routes.javascriptRouter("jsRoutes",
                        controllers.routes.javascript.Editor.new_level(),
                        controllers.routes.javascript.Editor.upload(),
                        controllers.routes.javascript.Editor.download(),
                        controllers.routes.javascript.Editor.getIndex(),
                        controllers.routes.javascript.Editor.setIndex(),
                        controllers.routes.javascript.Editor.changeBlockType(),
                        controllers.routes.javascript.Editor.changeToolType(),
                        controllers.routes.javascript.Editor.addColumns(),
                        controllers.routes.javascript.Editor.removeColumns(),
                        controllers.routes.javascript.Editor.establishWebSocket(),
                        controllers.routes.javascript.Application.logout()
                )
        );
    }

    public static Result contact(){
        return ok(views.html.contact.render(Application.getUsers()));
    }

    public static Result login(){
        return ok( views.html.login.render(form(Login.class), Application.getUsers()));
    }

    @Security.Authenticated(Secured.class)
    public static Result logout(){
        clearSession();
        return ok(views.html.logout.render(Application.getUsers()));
    }

    public static Result authenticate(){
        Form<Login> loginForm = form(Login.class).bindFromRequest("user", "password");

        if(loginForm.hasErrors()){
            return badRequest(views.html.login.render(loginForm, Application.getUsers()));
        }else{
            loginUser(loginForm.get().user);
            return redirect(routes.Application.index());
        }
    }


    public static Result auth(){
        String providerUrl = "https://www.google.com/accounts/o8/id";
        String returnToUrl = routes.Application.verify().absoluteURL(request());

        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("Email", "http://schema.openid.net/contact/email");
        attributes.put("FirstName", "http://schema.openid.net/namePerson/first");
        attributes.put("LastName", "http://schema.openid.net/namePerson/last");

        Promise<String> redirectUrl = OpenID. redirectURL(providerUrl, returnToUrl, attributes);
        return redirect(redirectUrl.get());
    }


    public Result verify(){
        Promise<OpenID.UserInfo> userInfoPromise = OpenID. verifiedId();
        OpenID.UserInfo userInfo = userInfoPromise.get();
        loginUser(userInfo.attributes.get("Email"));
        return redirect(routes.Application.index());
    }


    private static void loginUser(String user){
        users.add(user);
        session().clear();
        session("user", user);
    }

    private static void clearSession(){
        users.remove(session().get("user"));
        session().clear();
    }
}
