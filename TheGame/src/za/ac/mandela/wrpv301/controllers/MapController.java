package za.ac.mandela.wrpv301.controllers;


import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import za.ac.mandela.wrpv301.models.Map;
import za.ac.mandela.wrpv301.models.location.Location;
import za.ac.mandela.wrpv301.models.location.Passage;
import za.ac.mandela.wrpv301.models.location.Room;
import za.ac.mandela.wrpv301.models.location.Side.Door;
import za.ac.mandela.wrpv301.models.location.Side.Side;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

class MapController {

    private Map m;

    // view
    private Scene scene;
    private Pane map;

    // Images to be displayed on the map
    private ImageView player;
    private ImageView killer;
    private ImageView wall;
    private ImageView bag;
    private ImageView hug;
    private ImageView snake;
    private ImageView dog;

    MapController(Scene scene) {
        this.scene = scene;
    }

    /**
     * Connect to the map model
     *
     * @param m map model
     */
    void connect(Map m, Pane map) throws Exception {
        this.map = map;
        this.m = m;
        drawMap();
    }

    /**
     * Insert the player's avatar at current position
     * @param loc player's location

     */
    void start(Location loc) throws Exception {
        if (loc instanceof Room) {
            Rectangle r = (Rectangle) scene.lookup("#" + loc.getNum());
            player.setX(r.getX());
            player.setY(r.getY());
        } else {

            String n = "" + loc.getNum();
            Line l = (Line) scene.lookup("#" + n);
            int x = 0, y = 0;
            if ("31".contains("" + n.charAt(3))) {
                x = -50;
                y = -25;
            }
            player.setY(l.getEndY() + y);
            player.setX(l.getEndX() + x);
        }

        revealRoom(loc);
    }

    /**
     * Draw the map
     *
     * @throws Exception if something goes wrong
     */
    private void drawMap() throws Exception {

        // Load Images
        loadImages();

        // Draw rooms
        for (Room room : m.getRooms()) {
            int row = (room.getX() - 1) * 60, col = (room.getY() - 1) * 60;
            drawRoom(col, row, room);
        }


        // Draw the connections between the room
        for (Passage passage : m.getPassages()) {
            // Positions
            double x1 = 0, y1 = 0, x2 = 0, y2 = 0, n = 1;
            // Get the passage address number
            String nn = "" + passage.getNum();
            for (int x = 3; x >= 0; x--) {
                // Get the room connected to the passage
                Room room = passage.getRoom(x);
                if (room != null) {
                    Rectangle r = (Rectangle) scene.lookup("#" + room.getNum());
                    // get the positions of the rooms on the map
                    if (n == 1) {
                        x1 = r.getX();
                        y1 = r.getY();
                        n++;
                    } else {
                        x2 = r.getX();
                        y2 = r.getY();
                        n--;
                    }
                }
            }
            // Check the alignment of the connection whether it is vertical or horizontal and draw on the map
            if (!"31".contains("" + nn.charAt(3)))
                map.getChildren().add(drawPassage(x1 + 30, y1, x2 + 30, y2 + 60, nn));
            else map.getChildren().add(drawPassage(x1 + 60, y1 + 30, x2, y2 + 30, nn));

        }

        // Draw the icons on the map
        drawIcons();
        drawLockedDoors();

    }


    /**
     * Loads the images
     *
     * @throws Exception if something goes wrong when loading images
     */
    private void loadImages() throws Exception {
        player = new ImageView(new Image(new FileInputStream("TheGame/images/av.png")));
        player.setFitHeight(50);
        player.setFitWidth(50);
        hug = new ImageView(new Image(new FileInputStream("TheGame/images/hug.png")));
        hug.setFitHeight(40);
        hug.setFitWidth(50);
        dog = new ImageView(new Image(new FileInputStream("TheGame/images/dog.png")));
        dog.setFitHeight(40);
        dog.setFitWidth(50);
        bag = new ImageView(new Image(new FileInputStream("TheGame/images/bag.png")));
        killer = new ImageView(new Image(new FileInputStream("TheGame/images/killer.png")));
        killer.setFitHeight(50);
        killer.setFitWidth(50);
        wall = new ImageView(new Image(new FileInputStream("TheGame/images/wall.png")));
        wall.setFitHeight(50);
        wall.setFitWidth(50);
        snake = new ImageView(new Image(new FileInputStream("TheGame/images/snake.png")));
        snake.setFitHeight(50);
        snake.setFitWidth(50);
    }

    /**
     * Draws images on the map
     */
    private void drawIcons() {
        for (Passage p : m.getPassages())
            if (p.getObstacle() != null) {
                String n = "" + p.getNum();
                Line line = (Line) scene.lookup("#" + n);
                String ob = p.getObstacle().getDescription().split(" ")[0];
                if ("31".contains("" + n.charAt(3)))
                    drawObstacles(ob, line.getEndX() - 50, line.getEndY() - 30);
                else drawObstacles(ob, line.getEndX(), line.getEndY());
            }

    }

    /**
     * Draws or Inserts obstacles on the map
     * @param icon icon to be drawn
     * @param x x position
     * @param y y position
     */
    private void drawObstacles(String icon, double x, double y) {
        switch (icon) {
            case "guard": {
                killer.setX(x);
                killer.setY(y);
                map.getChildren().add(killer);
                break;
            }
            case "snake": {
                snake.setX(x);
                snake.setY(y);
                map.getChildren().add(snake);
                break;
            }
            case "wall": {
                wall.setX(x);
                wall.setY(y);
                map.getChildren().add(wall);
                break;
            }case "dog": {
                dog.setX(x);
                dog.setY(y);
                map.getChildren().add(dog);
                break;
            }
        }
    }

    /**
     *  Draws locked doors
     * @throws Exception fails to load the image icon
     */
    private void drawLockedDoors() throws Exception {

        for (Room room : m.getRooms())
            for (int x = 0; x < 4; x++) {
                // The side the door is at ( north,south, east or west)
                Side s = room.getSide(x);
                if (s instanceof Door) {
                    // Get the door
                    Door d = (Door) s;
                    // check if the is locked
                    if ((d.isLocked())) {
                        // Draw the door
                        Rectangle r = (Rectangle) scene.lookup("#" + room.getNum());
                        ImageView door = new ImageView(new Image(new FileInputStream("TheGame/images/door.png")));
                        double xx = r.getX();
                        double yy = r.getY();
                        // Getting the positions of the side the door is at
                        if (x == 0)
                            yy -= 30;
                        if (x == 1)
                            xx += 30;
                        if (x == 2)
                            yy += 30;
                        if (x == 3)
                            xx -= 30;
                        door.setFitHeight(50);
                        door.setFitWidth(50);
                        door.setY(yy);
                        door.setX(xx);
                        map.getChildren().add(door);
                    }
                }
            }
    }

    /**
     * draws a room
     *
     * @param x    horizontal position
     * @param y    vertical position
     * @param room the room
     * @return a room
     */
    private Rectangle drawRoom(double x, double y, Room room) throws FileNotFoundException {

        // Create a squared room
        Rectangle r = new Rectangle(x, y, 60, 60);
        // Add the room on the map
        map.getChildren().add(r);
        // Check if the room was visited before
        if (room.isVisited()) {
            // Reveal the room
            bagIcon(room, r);
        } else r.setFill(Color.BLACK);
        // Tooltip to display the room description on mouse hover
        Tooltip tip = new Tooltip(room.getDescription());
        Tooltip.install(r, tip);
        // Set the view room id
        r.setId("" + room.getNum());

        return r;
    }

    /**
     *  Displays a bag if a room has items in it
     * @param room object
     * @param r shape
     * @throws FileNotFoundException
     */
    private void bagIcon(Room room, Rectangle r) throws FileNotFoundException {
       // show the room
        r.setFill(Color.GRAY);
        // Check if the room has items
        if (!(room.getItems()).isEmpty()) {
            // remove the player avatar first
            map.getChildren().remove(player);
            // load and set the bag icon positions
            ImageView b = new ImageView(new Image(new FileInputStream("TheGame/images/bag.png")));
            b.setY(r.getY());
            b.setX(r.getX());
            // add the bag
            map.getChildren().add(b);
            // add the avatar
            map.getChildren().add(player);
        }
    }

    /**
     * removes an icon from the map
     *
     * @param icon the name of the icon
     */
    void removeIcon(String icon) {
        switch (icon) {
            case "guard":
                map.getChildren().remove(killer);
                break;
            case "wall":
                map.getChildren().remove(wall);
                break;
            case "bag":
                map.getChildren().remove(bag);
                break;
            case "snake":
                map.getChildren().remove(snake);
                break;
                case "dog":
                map.getChildren().remove(dog);
                break;
        }
    }

    /**
     * draws a connection
     *
     * @param x1  horizontal start position
     * @param y1  vertical start position
     * @param x2  horizontal end position
     * @param y2  vertical end position
     * @param num passage number
     * @return a passage
     */
    private Line drawPassage(double x1, double y1, double x2, double y2, String num) {
        Line line = new Line(x1, y1, x2, y2);
        line.setStrokeWidth(10);
        line.setStroke(Color.GRAY);
        Tooltip.uninstall(line, new Tooltip(num));
        line.setId(num);
        return line;
    }

    /**
     * Display the room entered by the player
     *
     * @param loc players location
     */
    void revealRoom(Location loc) throws Exception {
        if (loc instanceof Room) {
            // get the room on the map
            Rectangle r = (Rectangle) scene.lookup("#" + loc.getNum());
            r.setFill(Color.GRAY);
            Room room = (Room) loc;
            // get if it was visited before
            if (!room.isVisited())
                bagIcon(room, r);
            // set the room to be visited
            room.visited();
        }
    }

    void gameOver()
    {
        Rectangle r = (Rectangle) scene.lookup("#10");
        hug.setY(r.getY());
        hug.setX(r.getX());
        map.getChildren().remove(player);
        map.getChildren().addAll(hug);
    }


    /**
     * Moves the player's avatar on the map
     *
     * @param n direction number
     */
    void move(int n) {
        switch (n) {
            case 0:
                player.setTranslateY(player.getTranslateY() - 60);
                break;
            case 2:
                player.setTranslateY(player.getTranslateY() + 60);
                break;
            case 3:
                player.setTranslateX(player.getTranslateX() - 60);
                break;
            case 1:
                player.setTranslateX(player.getTranslateX() + 60);
                break;
        }
    }
}
