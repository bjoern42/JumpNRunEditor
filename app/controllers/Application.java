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
                        controllers.routes.javascript.Editor.download(),
                        controllers.routes.javascript.Editor.getIndex(),
                        controllers.routes.javascript.Editor.setIndex(),
                        controllers.routes.javascript.Editor.changeBlockType(),
                        controllers.routes.javascript.Editor.changeToolType(),
                        controllers.routes.javascript.Editor.addColumns(),
                        controllers.routes.javascript.Editor.removeColumns()
                )
        );
    }

    public static Result contact(){
        return ok(views.html.contact.render());
    }
}
