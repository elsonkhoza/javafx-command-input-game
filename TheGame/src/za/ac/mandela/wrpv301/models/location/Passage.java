package za.ac.mandela.wrpv301.models.location;

import za.ac.mandela.wrpv301.models.Obstacle;

public class Passage extends Location {

    private Room[] connect = new Room[4]; //The rooms connected to the passage
    private Obstacle obstacle; // Obstacle associated with the passage

    public Passage() {
    }

    public Room getRoom(int s) {
        return connect[s];
    }

    /**
     * Joins/connects two rooms
     *
     * @param a     first room
     * @param b     second room
     * @param aSide side the first room is connected to, 0-north 1-east 2-south 3-west
     * @param bSide side the second room is connect to
     */
    public void joinRooms(Room a, Room b, int aSide, int bSide) {

        // connect the rooms to the passage
        a.setDoor(bSide);
        b.setDoor(aSide);
        // connect the passage to the room
        a.setPassage(this, bSide);
        b.setPassage(this, aSide);
        this.connect[aSide] = a;
        this.connect[bSide] = b;

    }

    public Obstacle getObstacle() {
        return obstacle;
    }

    public void setObstacle(Obstacle obstacle) {
        this.obstacle = obstacle;
    }
}
