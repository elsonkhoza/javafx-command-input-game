package za.ac.mandela.wrpv301.controllers;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import za.ac.mandela.wrpv301.models.*;
import za.ac.mandela.wrpv301.models.items.Item;
import za.ac.mandela.wrpv301.models.items.Key;
import za.ac.mandela.wrpv301.models.location.Location;
import za.ac.mandela.wrpv301.models.location.Passage;
import za.ac.mandela.wrpv301.models.location.Room;
import za.ac.mandela.wrpv301.models.location.Side.Door;

import java.io.File;

public class GameController {


    private Player player;
    private Map map;
    private MapController mc;
    private Pane m;

    public GameController(Scene scene) throws Exception {

        this.map = new Map();
        mc = new MapController(scene);
        this.scene = scene;
        obtainViewReferences();
        play();
        startGame();

    }

    //View Attributes
    private Scene scene;
    private TextField textField;
    private TextArea screen;
    private Button restartBtn, pauseBtn, resumeBtn;
    private ListView<Item> playerItems;
    private RadioButton muteCheckBox;

    //Keep track of the game
    private Door curDoor = null;
    private Passage curPassage = null;
    private Location prevLoc = null;
    private MediaPlayer mediaPlayer = null;


    /**
     * Get player requests
     */
    private void play() {

        // obtain user input from the view
        textField.setOnAction(event -> {
            String c = textField.getText();
            display(c);
            if (!c.isEmpty()) {
                try {
                    evaluateInput(c);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            textField.setText("");
        });
        // restart the game
        restartBtn.setOnAction(event -> {
            try {
                restartGame();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // pause the game
        pauseBtn.setOnAction(event -> {
            try {
                saveCurrentGameState();
                display("You have saved the current game state");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        resumeBtn.setOnAction(event -> {
            try {
                map.setState(1);
                startGame();
                map.setState(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // mute the game sound
        muteCheckBox.setOnAction(event -> {
            if (muteCheckBox != null && muteCheckBox.isSelected())
                mediaPlayer.stop();
        });
    }

    /**
     * Start the game
     *
     * @throws Exception if something goes wrong
     */
    private void startGame() throws Exception {
        mediaPlayer = null;
        playSound("over");
        m.getChildren().clear();
        // load the game
        map.loadGame();
        // connect to the map controller
        mc.connect(map, m);
        this.player = map.getPlayer();
        // set the listView to show player's items
        playerItems.setItems(player.getItems());
        // clear the screen
        screen.clear();
        // Welcome massage
        display("Welcome to save your friend!");
        display(
                "Instructions: To play the game type in short phrases into the command line below. " +
                        "Below the command line is a screen that shows a list of commands you can enter. Typing e moves you from the position you are currently at to the east position. " +
                        "To use an item , type \"use\" followed by the item name you want to use, \"take\" and \"drop\" also helps you to interact with the items and you can also examine the items. Type in \"see\" to see items in a room." +
                        " If you feel like not continuing with the game you can click the save button below to save the current state of the game and resume later by clicking the resume button later when starting the game." +
                        " You can also click the restart button to restart the game" +
                        " any time. So let us begin. Good luck in finding your friend!"
        );
        screen.appendText("\n ");
        // display the player current location
        display("You are currently "+player.getPlace().getDescription());
        prevLoc = player.getPlace();
        // update the map to display the players avatar
        mc.start(player.getPlace());

    }

    /**
     * Reset the game to the initial state
     *
     * @throws Exception if something goes wrong in setting the game state
     */
    private void restartGame() throws Exception {

        // set the game state to zero ( the game is restarted)
        map.setState(0);
        //  start the game
        startGame();
    }

    /**
     * Saves the game current sate
     *
     * @throws Exception if the model fails to load the files
     */
    private void saveCurrentGameState() throws Exception {
        // save the paused gamed
        map.saveGame();
    }

    /**
     * Obtain references from the view
     */
    private void obtainViewReferences() {
        m = (Pane) scene.lookup("#map");
        textField = (TextField) scene.lookup("#text");
        screen = (TextArea) scene.lookup("#screen");
        restartBtn = (Button) scene.lookup("#restart");
        pauseBtn = (Button) scene.lookup("#pause");
        resumeBtn = (Button) scene.lookup("#resume");
        playerItems = (ListView<Item>) scene.lookup("#items");
        muteCheckBox = (RadioButton) scene.lookup("#mute");
    }

    /**
     * takes and evaluates  user input
     *
     * @param command user input command
     */
    private void evaluateInput(String command) throws Exception {
        String[] split = command.split(" ");
        String firstWord = split[0], secWord = "";

        if (split.length > 1)
            secWord = split[1].toLowerCase();

        switch (firstWord) {
            case "n":
            case "e":
            case "w":
            case "s": {
                movePlayer(directionIndex(firstWord.charAt(0)));
                break;
            }
            case "drop": {
                if (!secWord.isEmpty())
                    dropItem(secWord);
                break;
            }
            case "grab":
            case "take": {
                if (!secWord.isEmpty())
                    takeItem(secWord);
                break;
            }
            case "examine": {
                if (!secWord.isEmpty())
                    examineItem(secWord);
                break;
            }
            case "use": {
                if (!secWord.isEmpty())
                    useItem(secWord);
                break;
            }
            case "unlock": {
                if (curDoor != null)
                    unlockDoor();
                else display("No door is locked");
                break;
            }
            case "see":
            {
                Location cur=player.getPlace();
                // no items in a passage
                if(cur instanceof Passage)
                    display("no items");
                else {
                    Room r=(Room)cur;
                    if(r.getItems().isEmpty())
                        display("no items");
                    else
                        //  the items
                        for(Item item:r.getItems())
                      display("There is: "+item.getDescription().split(",")[0]);
                }
                break;
            }
            case"eat":
            case "consume": {
                Item item = player.getItem(secWord);
                if (item == null) {
                    display("You dont have " + secWord);
                    return;
                }
                // the if the item they want to consume is water or food
                if (secWord.equals("food") || secWord.equals("water")) {
                    player.dropItem(item);
                    display("You have consumed your " + secWord);
                } else {
                    display("You cannot consume that  that!");
                }
                break;
            }
            default:
                display("I don't understand that.");
                break;
        }
    }

    /**
     * Moves the player from one location to another
     *
     * @param direction the direction the user desires to go
     * @throws Exception if something goes wrong
     */

    private void movePlayer(int direction) throws Exception {

        //Get player's current location
        Location curLoc = player.getPlace();
        Location nextLoc = curLoc;
        Door door = null;
        // If the player was in a room
        if (curLoc instanceof Room) {
            Room r = (Room) curLoc;
            //Check if the side that the player player was to exist with is a door
            if (r.getSide(direction) instanceof Door) {
                //Get the door
                door = (Door) r.getSide(direction);
                nextLoc = r.getPassage(direction);
            }
        } else {
            //The player was in a passage
            Passage p = (Passage) curLoc;
            // get the room the player wants to exist
            Room r = p.getRoom(direction);
            if (r != null) {
                // check if there is an obstacle preventing the player to pass
                Obstacle ob = p.getObstacle();
                if (!(prevLoc.getNum() == r.getNum()) && ob != null) {

                    display("There is a " + ob.getDescription());
                    playSound(ob.getDescription().split(" ")[0] + 1);
                    curPassage = p;
                    return;
                }
                //get the door
                door = (Door) r.getSide(oppositeDirection(direction));
                nextLoc = r;
            }
        }
        curPassage = null;
        mediaPlayer.stop();

        if (door == null) {
            display("Oops!, You cannot go in that direction, there is a wall");
        }  // check if the door is locked
        else if (door.isLocked()) {
            display("The door is locked, you need to open it with a keys that matches");
            this.curDoor = door;
        } else {
            curDoor=null;
            prevLoc = curLoc;
            //Player enters the place
            player.setPlace(nextLoc);
            //Update the map
            mc.revealRoom(player.getPlace());
            mc.move(direction);
            //Display cur location description
            display(nextLoc.getDescription());
            // check if the game is over
            gameOver();
        }
    }

    /**
     * Examines an item
     *
     * @param item items examined
     */
    private void examineItem(String item) {
        Location cur = player.getPlace();
        // Examine the items in the room
        if (cur instanceof Room) {
            Room room = (Room) cur;
            Item item1 = room.getItem(item);
            if (item1 != null) {
                display(item1.getDescription().split(",")[1]);
                return;
            }
        }
        // Examine the items the player is carrying
        Item item1 = player.getItem(item);
        if (item1 != null) {
            display(item1.getDescription().split(",")[1]);
            return;
        }

        display("There is no " + item + " to examine");

    }

    /**
     * Use an items
     *
     * @param item item to be used
     */
    private void useItem(String item) {
        // check if the player has the items want to use
        if (player.getItem(item) == null) {
            display("You do not have a " + item);
            return;
        }
        if (curPassage != null) {
            String ob = curPassage.getObstacle().getDescription().split(" ")[0];
            // check if the player uses the correct tool/item
            if (curPassage.getObstacle().getToolToUse().contains(item)) {
                //Update the map
                mc.removeIcon(ob);
                //play the sound
                playSound(item);
                playSound(ob);
                //remove the obstacle from the passage
                curPassage.setObstacle(null);
                curPassage = null;
                display("You can now pass");
                // score.setText((sc));
                return;
            } else {
                // The player uses a wrong item on the obstacle
                display("You cannot use that. It does not work.");
                return;
            }
        }
        // If the player suddenly use an item for no reason
        display("Use a " + item + " for what? there is no need for using it now.");

    }

    /**
     * Enable a player to take an item
     *
     * @param item item the player wants to take
     */
    private void takeItem(String item) {

        Location cur = player.getPlace();
        // There are items played in a passage
        if (cur instanceof Passage)
            display("There are items in this passage");
        else {
            Room room = (Room) cur;
            for (Item item1 : room.getItems()) {
                //check if item is available in that room
                if (item1.getDescription().split(",")[0].equals(item)) {
                    display(item + " successfully taken");
                    // add to the player's list
                    playerItems.getItems().add(item1);
                    // remove from the room
                    room.removeItem(item1);
                    return;
                }
            }
            display("There is no " + item + " in this room");
        }
    }

    /**
     * Drops an item
     *
     * @param theItem item to be drops
     */
    private void dropItem(String theItem) {
        Item item = player.getItem(theItem);
        if (item != null) {
            Location cur = player.getPlace();
            // check if the player was in a room
            if (cur instanceof Room) {
                player.dropItem(item);
                ((Room) cur).addItem(item);
                display(theItem + " successfully dropped");
            } else display("Cannot drop the item in a passage, you can only drop items in a room.");
        } else display("You do not have a " + theItem + " to drop.");
    }

    /**
     * Unlocks a locked door
     */
    private void unlockDoor() {

        for (Item item : player.getItems()) {
            // check if the player has the key
            if (item instanceof Key) {
                Key k = (Key) item;
                if (curDoor.isMatch(k)) {
                    display("You have successfully unlocked the door, it is now opened, you can now go");
                    playSound("door");
                    curDoor=null;
                    return;
                }
            }
        }
        display("You do not have the key to open this door.");
    }

    /**
     * Displays text on the screen
     *
     * @param text text to be displayed
     */
    private void display(String text) {
        screen.appendText("\n>" + text);
    }

    private void gameOver() {
        if (player.getPlace().getNum() == 10) {
            display("Wait! here is your friend. congratulations! you have finally found your friend!.");
            display("Game over!");
            mc.gameOver();
            playSound("theme");
        }
    }

    /**
     * Plays sound
     *
     * @param name of the sound
     */
    private void playSound(String name) {
        String file = "";
        // get the file path of the sound
        if (!muteCheckBox.isSelected()) {
            switch (name) {
                case "gun":
                    file = "TheGame/sounds/gun.mp3";
                    break;
                case "knife":
                    file = "TheGame/sounds/knife.mp3";
                    break;
                case "guard1":
                    file = "TheGame/sounds/hum.mp3";
                    break;
                case "wall":
                    file = "TheGame/sounds/glass.mp3";
                    break;

                case "over":
                    file = "TheGame/sounds/over.mp3";
                    break;

                case "dog1":
                    file = "TheGame/sounds/dogbark.mp3";
                    break;
                case "door":
                    file = "TheGame/sounds/door.mp3";
                    break;
                case "snake":
                    file = "TheGame/sounds/snake.mp3";
                    break;
                case "theme":
                    file = "TheGame/sounds/theme.mp3";
                    break;

            }
            // play the sound
            if (!file.isEmpty()) {
                Media sound = new Media(new File(file).toURI().toString());
                mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.play();
            }
        }
    }

    /**
     * Convert a char direction to a number
     *
     * @param s char direction
     * @return direction number
     */
    private int directionIndex(char s) {

        switch (s) {
            case 'n':
                return 0;
            case 'e':
                return 1;
            case 's':
                return 2;
            case 'w':
                return 3;

        }
        return 5;
    }

    /**
     * Obtain an opposite direction of any direction
     *
     * @param d direction number
     * @return opposition direction
     */
    private int oppositeDirection(int d) {
        switch (d) {
            case 2:
                return 0;
            case 3:
                return 1;
            case 0:
                return 2;
            case 1:
                return 3;

        }
        return 5;
    }

}
