package za.ac.mandela.wrpv301.models.location.Side;

import za.ac.mandela.wrpv301.models.items.Key;

public class Door extends Side {

    private Key key; // key to open the door
    private boolean isLocked = false; // to keep track of the door status

    public Door() {
    }

    public void setKey(Key key) {
        this.key = key;
        isLocked = true;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public boolean isMatch(Key key) {
        if (this.key.getRoomNum() == key.getRoomNum()&&this.key.getSide()==key.getSide()) {
            isLocked = false;
            return true;
        } else return false;
    }


}
