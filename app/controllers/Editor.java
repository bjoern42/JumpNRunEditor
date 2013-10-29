package controllers;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import models.BlockButtonInterface;
import models.implementation.BlockButton;

import play.Logger;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

import de.htwg.project42.model.GameObjects.BlockInterface;
import de.htwg.project42.model.GameObjects.LevelLoaderInterface;
import de.htwg.project42.model.GameObjects.Implementation.LevelLoader;

public class Editor extends Controller {
    public static int WIDTH = 15, HEIGHT = 10;

    public static Result index() {
        return ok(views.html.index.render());
    }

    public static Result new_level(){
        session("tool_type", String.valueOf(0));
        LinkedList<BlockButtonInterface[]> map = new LinkedList<BlockButtonInterface[]>();
        for(int i=0; i<WIDTH; i++){
            BlockButtonInterface[] column = new BlockButtonInterface[HEIGHT];
            for(int j=0; j<HEIGHT; j++){
                column[j] = new BlockButton(BlockInterface.TYP_AIR);
            }
            map.add(column);
        }
        Cache.set("map", map);
        return redirect(routes.Editor.show(0));
    }

    public static Result upload() {
        session("tool_type", String.valueOf(0));
        MultipartFormData body = request().body().asMultipartFormData();
        FilePart level = body.getFile("level");

        if (level != null) {
            File file = level.getFile();
            Cache.set("map", loadMap(file));
            return redirect(routes.Editor.show(0));
        }else{
            flash("error", "Missing file");
        }
        return redirect(routes.Editor.index());
    }

    public static Result show(int pIndex){
        List<BlockButtonInterface[]> map = (List)Cache.get("map");

        if(pIndex < 0){
            pIndex = 0;
        }
        Logger.debug("index "+pIndex);
        if(pIndex+WIDTH > map.size()-1){
            pIndex = map.size()-WIDTH;
        }
        return ok(views.html.show.render(map.subList(pIndex, pIndex + WIDTH), HEIGHT, pIndex));
    }

    public static Result changeBlockType(int x, int y, int type){
        //Logger.debug("changeBlockType called! x:"+x +" y:"+y+" type:"+type);
        List<BlockButtonInterface[]> map = (List)Cache.get("map");
        map.get(x)[y].setType(type);
        return ok(""+type);
    }

    public static Result changeToolType(int type){
        //Logger.debug("changeToolType called! type:"+type);
        session("tool_type", String.valueOf(type));
        return ok(""+type);
    }

    private static List<BlockButtonInterface[]> loadMap(File pLevel){
        List<BlockButtonInterface[]> objects = new LinkedList<BlockButtonInterface[]>();
        LevelLoaderInterface levelLoader = new LevelLoader();
        levelLoader.setInputFile(pLevel);
        int buffer[] = null;

        while((buffer = levelLoader.readNext()) != null){
            List<BlockButtonInterface> column = new LinkedList<BlockButtonInterface>();
            for(int i=0; i<buffer.length; i++){
                BlockButtonInterface block = new BlockButton(buffer[i]);
                if(buffer[i] == BlockInterface.TYP_BUTTON || buffer[i] == BlockInterface.TYP_GATE){
                    block.setIndex(buffer[++i]);
                }
                column.add(block);
            }

            objects.add(column.toArray(new BlockButtonInterface[column.size()]));
        }

        HEIGHT = objects.get(0).length;
        //Logger.debug("height = "+HEIGHT);
        return objects;
    }

    private static void logMap(List<BlockButtonInterface[]> map){
        for(BlockButtonInterface[] a:map){
            for(BlockButtonInterface b:a){
                Logger.debug("[" + b.getType() + "]");
            }
        }
    }
}
