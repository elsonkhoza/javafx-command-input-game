package za.ac.mandela.wrpv301.models.location;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import za.ac.mandela.wrpv301.models.items.Item;
import za.ac.mandela.wrpv301.models.items.Key;
import za.ac.mandela.wrpv301.models.location.Side.Door;
import za.ac.mandela.wrpv301.models.location.Side.Side;

import java.util.ArrayList;

public class Room extends Location {

    private ObservableList<Item> items = FXCollections.observableArrayList(new ArrayList<>()); //Items in the room
    private Passage[] passages = new Passage[4]; //Passages connected to the room 0-North 1-East 2-South 3-West
    private Side[] sides = new Side[4]; // sides of the room

    private boolean isEntered = false;//Keep track of if the room was entered
    private int x, y;//row,column

    public Room() {
        for (int x = 0; x < sides.length; x++)
            sides[x] = new Side();
    }

    /**
     * Set the passage connected to the room
     *
     * @param p    the passage
     * @param side the side of the room which the passage is connected to
     */
    void setPassage(Passage p, int side) {
        passages[side] = p;
    }

    public Passage getPassage(int side) {
        return passages[side];
    }

    public ObservableList<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public Item getItem(String desc) {
        for (Item item : items)
            if (item.getDescription().split(",")[0].equals(desc))
                return item;
        return null;
    }

    public boolean isVisited() {
        return isEntered;
    }

    public void visited() {
        isEntered = true;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Side getSide(int s) {
        return sides[s];
    }

    void setDoor(int s) {
        sides[s] = new Door();
    }

    /**
     * Locks the room doors
     *
     * @param key key to lock the room
     */
    public void lockDoors(Key key, int side) {
        Side s = sides[side];
        if (s instanceof Door) {
            ((Door) s).setKey(key);
        }
    }


}
