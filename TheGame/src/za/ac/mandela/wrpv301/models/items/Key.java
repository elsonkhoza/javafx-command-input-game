package za.ac.mandela.wrpv301.models.items;

public class Key extends Item {


    private int roomNum;// key number
    private int side; // the side the door is

    public Key(int keyNum, int side, String description) {
        super(description);
        this.side=side;
        this.roomNum = keyNum;
    }

    public int getRoomNum() {
        return roomNum;
    }

    public int getSide() {
        return side;
    }
}
