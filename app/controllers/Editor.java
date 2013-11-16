package controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    public static int WIDTH = 15, HEIGHT = 10, INDEX_OFFSET = 100;

    public static Result new_level(){
        LinkedList<BlockButtonInterface[]> map = new LinkedList<BlockButtonInterface[]>();
        for(int i=0; i<WIDTH; i++){
            BlockButtonInterface[] column = new BlockButtonInterface[HEIGHT];
            for(int j=0; j<HEIGHT; j++){
                column[j] = new BlockButton(BlockInterface.TYP_AIR);
            }
            map.add(column);
        }
        Cache.set("map", map);
        session("tool_type", String.valueOf(0));
        session("current_index", String.valueOf(INDEX_OFFSET));
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
        return redirect(routes.Editor.new_level());
    }

    public static Result download(){
        List<BlockButtonInterface[]> map = (List)Cache.get("map");
        try{
            File file = saveMap(map);
            response().setContentType("application/x-download");
            response().setHeader("Content-disposition","attachment; filename="+file.getName());
            return ok(file);
        }catch(IOException e){
            return ok("failure");
        }
    }

    public static Result show(int pIndex){
        List<BlockButtonInterface[]> map = (List)Cache.get("map");

        if(map == null){
            return redirect(routes.Editor.new_level());
        }
        if(pIndex < 0){
            pIndex = 0;
        }
        if(pIndex+WIDTH > map.size()-1){
            pIndex = map.size()-WIDTH;
        }
        return ok(views.html.show.render(map.subList(pIndex, pIndex + WIDTH), HEIGHT, pIndex));
    }

    public static Result changeBlockType(int x, int y, int type){
        List<BlockButtonInterface[]> map = (List)Cache.get("map");
        map.get(x)[y].setType(type);
        return ok(""+type);
    }

    public static Result getIndex(int x, int y){
        List<BlockButtonInterface[]> map = (List)Cache.get("map");
        int index = map.get(x)[y].getIndex();
        if(index == -1 && map.get(x)[y].getType() == BlockInterface.TYP_BUTTON){
            index = getNextFreeIndex(map);
            map.get(x)[y].setIndex(index);
        }
        return ok(""+index);
    }

    public static Result setIndex(int x, int y, int index){
        List<BlockButtonInterface[]> map = (List)Cache.get("map");
        if(map.get(x)[y].getType() == BlockInterface.TYP_GATE){
            map.get(x)[y].setIndex(index);
            return ok(""+index);
        }else{
            return ok("failure");
        }
    }

    public static Result changeToolType(int type){
        session("tool_type", String.valueOf(type));
        return ok(""+type);
    }

    public static Result addColumns(int amount){
        List<BlockButtonInterface[]> map = (List)Cache.get("map");
        for(int i=0; i<amount; i++){
            BlockButtonInterface[] column = new BlockButtonInterface[HEIGHT];
            for(int j=0; j<HEIGHT; j++){
                column[j] = new BlockButton(BlockInterface.TYP_AIR);
            }
            map.add(column);
        }
        return ok(String.valueOf(map.size()));
    }

    public static Result removeColumns(int amount){
        List<BlockButtonInterface[]> map = (List)Cache.get("map");
        for(int i=0; i<amount; i++){
            if(map.size() > WIDTH){
                map.remove(map.size()-1);
            }
        }
        return ok(String.valueOf(map.size()));
    }

    private static List<BlockButtonInterface[]> loadMap(File pLevel){
        List<BlockButtonInterface[]> objects = new LinkedList<BlockButtonInterface[]>();
        LevelLoaderInterface levelLoader = new LevelLoader();
        levelLoader.setInputFile(pLevel);
        int buffer[] = null;
        int currentIndex = 0;

        while((buffer = levelLoader.readNext()) != null){
            List<BlockButtonInterface> column = new LinkedList<BlockButtonInterface>();
            for(int i=0; i<buffer.length; i++){
                BlockButtonInterface block = new BlockButton(buffer[i]);
                if(buffer[i] == BlockInterface.TYP_BUTTON || buffer[i] == BlockInterface.TYP_GATE){
                    block.setIndex(buffer[++i]);
                    if(block.getIndex()>currentIndex){
                        currentIndex = block.getIndex();
                    }
                }
                column.add(block);
            }

            objects.add(column.toArray(new BlockButtonInterface[column.size()]));
        }

        session("current_index", String.valueOf(currentIndex));
        HEIGHT = objects.get(0).length;
        return objects;
    }

    private static File saveMap(List<BlockButtonInterface[]> map) throws IOException {
        File file = File.createTempFile("map", ".lvl");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        for(BlockButtonInterface[] line:map){
            for(int i=0; i<line.length; i++){
                BlockButtonInterface blockButton = line[i];
                writer.write(""+blockButton.getType());
                if(blockButton.getIndex() != -1){
                    writer.write(","+(blockButton.getIndex()));
                }
                if(i+1<line.length){
                    writer.write(",");
                }
            }
            writer.newLine();
            writer.flush();
        }
        return file;
    }

    private static void logMap(List<BlockButtonInterface[]> map){
        for(BlockButtonInterface[] a:map){
            for(BlockButtonInterface b:a){
                Logger.debug("[" + b.getType() + "]");
            }
        }
    }

    private static int getNextFreeIndex(List<BlockButtonInterface[]> map){
        int currentIndex = Integer.parseInt(session("current_index"));
        session("current_index", String.valueOf(currentIndex+1));
        return Integer.parseInt(session("current_index"));
    }
}
