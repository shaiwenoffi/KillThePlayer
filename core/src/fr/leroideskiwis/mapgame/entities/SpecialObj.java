package fr.leroideskiwis.mapgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import fr.leroideskiwis.ktp.Main;
import fr.leroideskiwis.mapgame.*;

public abstract class SpecialObj extends Entity{

    protected final Game game;
    private final Texture texture;

    public abstract void execute(Game main, Map map, Player player);

    public SpecialObj(Game game, String path) {
        super(path);
        this.game = game;
        this.texture = Main.getTexture(path+".png");
    }

    public SpecialObj(Game game){
        this(game, null);
    }

    public String toString(){
        return "!";
    }

    public abstract String name();
    public Texture texture(){
        return texture == null ? new Texture(Gdx.files.internal("defaultobj.png")) : texture;
    }
    public abstract double chance();

    public Position spawn(Game main, Map map, Player player){

        return game.getRandomList(map.getEmptyCases());

    }
}
