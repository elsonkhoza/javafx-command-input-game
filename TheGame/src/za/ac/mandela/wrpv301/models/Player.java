package za.ac.mandela.wrpv301.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import za.ac.mandela.wrpv301.models.items.Item;
import za.ac.mandela.wrpv301.models.location.Location;

import java.util.ArrayList;

public class Player {

    private ObservableList<Item> items = FXCollections.observableArrayList(new ArrayList<>());//Items the player collected.
    private Location place; // The room/passage the placer is.

    public Player() {

    }

    public void collectItem(Item item) {
        items.add(item);
    }

    public void dropItem(Item item) {
        items.remove(item);
    }

    public ObservableList<Item> getItems() {
        return items;
    }

    public Item getItem(String desc) {
        for (Item item : items)
            if (item.getDescription().split(",")[0].equals(desc))
                return item;
        return null;
    }

    public void setPlace(Location place) {
        this.place = place;
    }

    public Location getPlace() {
        return place;
    }
}
