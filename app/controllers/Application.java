package controllers;

import play.*;
import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import models.implementation.Login;
import models.implementation.User;

public class Application extends Controller {

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
        return ok(views.html.contact.render());
    }

    public static Result login(){
        return ok( views.html.login.render(form(Login.class)));
    }

    @Security.Authenticated(Secured.class)
    public static Result logout(){
        session().clear();
        return ok(views.html.logout.render());
    }

    public static Result authenticate(){
        Form<Login> loginForm = form(Login.class).bindFromRequest("user", "password");

        if(loginForm.hasErrors()){
            return badRequest(views.html.login.render(loginForm));
        }else{
            session().clear();
            session("user", loginForm.get().user);
            return redirect(routes.Application.index());
        }
    }


}
