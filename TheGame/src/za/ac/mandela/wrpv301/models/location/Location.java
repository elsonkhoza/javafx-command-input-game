package za.ac.mandela.wrpv301.models.location;

public class Location {

    private String description; // location description
    private int number; // location number/address

    public Location() {

    }

    public String getDescription() {
        return description;
    }

    public int getNum() {
        return number;
    }

    public void setNum(int num) {
        this.number = num;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
