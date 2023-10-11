package za.ac.mandela.wrpv301.models;

public class Obstacle {

    private String description; // obstacle description
    private String toolToUse;// item to use on the obstacle
    private int num;// the passage number the obstacle belong to

    public Obstacle(String description, String toolToUse, int num) {
        this.description = description;
        this.toolToUse = toolToUse;
        this.num = num;
    }

    public String getDescription() {
        return description;
    }

    public String getToolToUse() {
        return toolToUse;
    }

    public int getNum() {
        return num;
    }
}
