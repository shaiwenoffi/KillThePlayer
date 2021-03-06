package fr.leroideskiwis.mapgame;

import fr.leroideskiwis.mapgame.entities.Enemy;
import fr.leroideskiwis.mapgame.entities.Obstacle;
import fr.leroideskiwis.mapgame.entities.Player;
import fr.leroideskiwis.plugins.events.OnEnnemyDeath;
import fr.leroideskiwis.plugins.events.OnEntitySpawn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Map implements Cloneable{

    private final Entity[][] content;
    private final Game game;

    public Map(Game main, int x, int y){
        this(main, new Entity[y][x]);
    }

    public Map(Game main, Entity[][] content){
        this.game = main;
        this.content = content;
        Obstacle border = new Obstacle();
        for(int i = 0; i < content[0].length; i++){
            content[0][i] = border;
        }

        for(int i = 0; i < content[0].length; i++){
            content[i][0] = border;
        }

        for(int i = 0; i < content.length; i++){
            content[i][content[i].length-1] = border;
        }

        for(int i = 0; i < content[content.length-1].length; i++){
            content[content.length-1][i] = border;
        }
    }

    /**
     *
     * @return in args[0] the line size and args[1] the column size
     */

    public int getSize(){
        return content.length;
    }

    /**
     *
     * @param pos the position of the object
     * @return the Object who is situate in x y
     */

    public Entity getObject(Position pos){
        return content[pos.getY()][pos.getX()];
    }

    public Entity getObject(int x, int y){
        return getObject(new Position(x, y));
    }

    public List<Position> getPositions(){
        List<Position> positions = new ArrayList<>();

        for (int x = 0; x < content.length; x++) {

            for (int y = 0; y < content[x].length; y++) {

                positions.add(new Position(x, y));

            }

        }
        return positions;

    }

    /**
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param o the object to compare
     * @return true if the object in x y is equals to o
     */

    public boolean isObject(int x, int y, Object o){
        if(o == null) return getObject(x, y) == null;
        if(getObject(x,y) == null) return false;
        return getObject(x, y).equals(o);
    }

    /**
     * Use this to set an object in the coordinates x y
     * <b>WARNING : this method don't replace the object, then it don't go to set the object if there already an object in. If you want to replace, use Map#replaceObject</b>
     *
     * @param pos the coordinates
     * @param entity the object to replace in x y.
     * @return false if there are already an object in x y
     * @see Map#replaceObject(int, int, Entity)
     */

    public boolean setObject(Position pos, Entity entity){
        if(getObject(pos.getX(), pos.getY()) == null) {
            OnEntitySpawn event = new OnEntitySpawn(entity, pos);
            game.getPluginManager().callEvent(event);
            if(event.isCancelled()) return false;
            content[pos.getY()][pos.getX()] = entity;
        }
        else return false;
        return true;
    }

    public boolean setObject(int x, int y, Entity o){
        return setObject(new Position(x, y), o);
    }

    /**
     *
     * Replace the object in x y by null
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */

    public void deleteObject(int x, int y){
        deleteObject(new Position(x, y));
    }
    public void deleteObject(Position pos) {
        if(getObject(pos) instanceof Player) return;
        replaceObject(pos.getX(), pos.getY(), null);
    }

    /**
     *
     * Replace the object in x y by the object <b>newObject</b> in parameters
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param newObject the object to replace with
     */

    public void replaceObject(int x, int y, Entity newObject){
        if(getObject(x, y) instanceof Enemy) {
            OnEnnemyDeath event = new OnEnnemyDeath(new Position(x, y), (Enemy) getObject(x, y));
            game.getPluginManager().callEvent(event);
            if(event.isCancelled()) return;
        }

        content[y][x] = newObject;
    }

    /**
     * Place an object randomly in the map
     * @param o The object to generate
     */

    public void generateRandom(Entity o){

        setObject(game.getRandomList(getPositions().stream().filter(this::isNull).collect(Collectors.toList())), o);

    }

    public List<Position> getNoEmptyCases(){
        List<Position> positions = new ArrayList<>();

        for (int x = 0; x < content.length; x++) {
            for (int y = 0; y < content[x].length; y++) {

                if(content[y][x] != null) positions.add(new Position(x, y));

            }
        }

        return positions;
    }

    public List<Position> getEmptyCases(){
        List<Position> positions = new ArrayList<>();

        for (int x = 0; x < content.length; x++) {
            for (int y = 0; y < content[x].length; y++) {

                if(content[y][x] == null) positions.add(new Position(x, y));

            }
        }

        return positions;
    }

    /**
     * Get the locations of a specific object (for example player)
     *
     * @param o the object to locate
     * @return the locations of all object o
     */

    public Position getPositionByObject(Entity o){
        for (int x = 0; x < content.length; x++) {
            for (int y = 0; y < content[x].length; y++) {
                Object obj = getObject(x, y);
                if(obj == null) continue;

                if(obj.equals(o)) return new Position(x, y);

            }
        }

        return null;
    }

    public Position getRandomPositionSurrounding(Position position){
        int choice = new Random().nextInt(4)+1;

        switch(choice){
            case 1:
                return position.add(1, 0);
            case 2:
                return position.add(0, 1);
            case 3:
                return position.add(-1, 0);
            case 4:
                return position.add(0, -1);
            default:
                return null;
        }

    }

    public boolean isNull(Position position){
        return getObject(position) == null;
    }

    /**
     * Get the locations of a specific type
     *
     * @param clazz the class to locate
     * @return the locations of all objects who is type clazz
     */

    public List<Position> getPositionsByType(Class<?> clazz){
        List<Position> positions = new ArrayList<>();
        for (int x = 0; x < content.length; x++) {
            for (int y = 0; y < content[x].length; y++) {
                Object obj = getObject(x, y);
                if(obj == null) continue;

                if(obj.getClass() == clazz || obj.getClass().getAnnotatedSuperclass().getType() == clazz) positions.add(new Position(x, y));

            }
        }

        return positions;
    }

    public <T> List<T> getObjectsByType(Class<T> clazz){

        if(!(clazz.getSuperclass() == Entity.class)) return null;

        List<Position> positions = getPositionsByType(clazz);
        List<T> objects = new ArrayList<>();

        for(Position position : positions){
            objects.add((T)getObject(position));
        }

        return objects;

    }

    public String toString(){
        StringBuilder builder = new StringBuilder();

        for(Object[] array : content){
            for(Object c : array){
                builder.append("[");
                builder.append(c == null ? " " : c);
                builder.append("] ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public Entity[][] getContent(){
        return content;
    }

    public void replaceObject(Position positionByObject, Entity newObject) {
        replaceObject(positionByObject.getX(), positionByObject.getY(), newObject);
    }

}
