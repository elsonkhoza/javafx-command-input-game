package za.ac.mandela.wrpv301.models.items;

public class Item {

    private String description; // items description

    public Item(String description) {
        this.description=description;
    }

    @Override
    public String toString()
    {
        return description.split(",")[0];
    }
    public String getDescription() {
        return description;
    }

}
