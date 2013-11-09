package controllers;

import play.*;
import play.mvc.*;


public class Application extends Controller {

    public static Result javascriptRoutes() {
        response().setContentType("text/javascript");
        return ok(
                Routes.javascriptRouter("jsRoutes",
                        controllers.routes.javascript.Editor.new_level(),
                        controllers.routes.javascript.Editor.upload(),
                        controllers.routes.javascript.Editor.download()
                )
        );
    }
}
