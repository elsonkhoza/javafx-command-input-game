package za.ac.mandela.wrpv301.models;

import za.ac.mandela.wrpv301.models.items.Item;
import za.ac.mandela.wrpv301.models.items.Key;
import za.ac.mandela.wrpv301.models.location.Passage;
import za.ac.mandela.wrpv301.models.location.Room;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Map {

    private ArrayList<Room> rooms;
    private ArrayList<Passage> passages;
    private Player player;

    public Map() {
    }

    // File paths
    private String playerUrl, roomsUrl, connectionsUrl, obstaclesUrl;

    /**
     * Save rooms,items, and the player data in a file
     * @throws Exception if fails to load the files
     */
    public void saveGame() throws Exception {

        PrintWriter printWriter = new PrintWriter(new File("TheGame/data/paused/Rooms.txt"));
        for (Room r : rooms) {
            char v;
            if(r.isVisited())
                v='t';
            else v='f';
            printWriter.println("room,"+v+"," + r.getNum() + "," + r.getX() + "," + r.getY()+","+ r.getDescription().split(":")[0]);
            for (Item item : r.getItems()) {
                if (item instanceof Key)
                    printWriter.println(item.getDescription() +"," + ((Key) item).getRoomNum()+","+((Key) item).getSide());
                else printWriter.println(item.getDescription());
            }
        }
        printWriter.close();

        printWriter = new PrintWriter(new File("TheGame/data/paused/Player.txt"));
        printWriter.println(player.getPlace().getNum());
        for (Item item : player.getItems()) {
            if (item instanceof Key)
                printWriter.println(item.getDescription() + "," + ((Key) item).getRoomNum()+","+((Key) item).getSide());
            else printWriter.println(item.getDescription());
        }
        printWriter.close();
        printWriter = new PrintWriter(new File("TheGame/data/paused/Obstacles.txt"));
        for (Passage p : passages)
            if (p.getObstacle() != null) {
                Obstacle ob = p.getObstacle();
                printWriter.println(ob.getDescription() + "," + ob.getToolToUse() + "," + p.getNum());
            }
        printWriter.close();


    }

    /**
     * load the rooms, passages, items and player information from a file
     * @throws Exception if fails to load to load the filed
     */
    public void loadGame() throws Exception {

        this.rooms = new ArrayList<>();
        this.passages = new ArrayList<>();
        this.player=new Player();
        getFiles();
        loadRooms();
        loadConnections();
        loadPlayer();
    }

    /**
     *  Loads the rooms
     * @throws Exception
     */
    private void loadRooms() throws Exception {
        Scanner scanner = new Scanner(new File(roomsUrl));
        String data[];
        int index = 0;
        while (scanner.hasNextLine()) {
            data = scanner.nextLine().split(",");
            if (data[0].equals("room")) {
                // room
                Room room = new Room();
                room.setNum(Integer.parseInt(data[2]));
                room.setDescription(data[5]);
                if(!data[1].equals("f"))
                    room.visited();
                room.setPos(Integer.parseInt(data[3]), Integer.parseInt(data[4]));
                index = room.getNum() - 1;
                rooms.add(index, room);
            } else {
                // items in that room
                if (data[0].equals("keys"))
                    rooms.get(index).addItem(new Key(Integer.parseInt(data[2]),Integer.parseInt(data[3]), data[0]+","+data[1]));
                else rooms.get(index).addItem(new Item(data[0]+","+data[1]));
            }
        }
        scanner.close();


    }

    /**
     * Loads the connections and join the room
     * @throws Exception fails to load the file
     */
    private void loadConnections() throws Exception {
        Scanner scanner = new Scanner(new File(connectionsUrl));
        String data[];
        while (scanner.hasNextLine()) {
            data = scanner.nextLine().split(",");

            // room numbers and the side they are connected to
            int r1Num = Integer.parseInt(data[0]);
            int r2Num = Integer.parseInt(data[1]);
            int r1Side = Integer.parseInt(data[2]);
            int r2Side = Integer.parseInt(data[3]);

            Passage p = new Passage();
            p.setDescription(data[4]);
            p.setNum(Integer.parseInt(r1Num + "" + r2Num + "" + r1Side + "" + r2Side));
            // connect rooms
            p.joinRooms(rooms.get(r1Num - 1), rooms.get(r2Num - 1), r1Side, r2Side);
            passages.add(p);

        }
        scanner.close();
        //lock rooms
        for (Room r : rooms) {
            for (Item item : r.getItems())
                if (item instanceof Key)
                    rooms.get(((Key) item).getRoomNum() - 1).lockDoors((Key) item,((Key) item).getSide());
        }
        // setting the obstacles
        scanner = new Scanner(new File(obstaclesUrl));
        while (scanner.hasNextLine()) {
            data = scanner.nextLine().split(",");
            Obstacle ob = new Obstacle(data[0], data[1], Integer.parseInt(data[2]));
            for (Passage p : passages)
                if (p.getNum() == ob.getNum())
                    p.setObstacle(ob);
        }
        scanner.close();
    }

    /**
     * Loads the information
     * @throws Exception fails to load the file
     */
    private void loadPlayer() throws Exception {
        Scanner scanner = new Scanner(new File(playerUrl));
        String data = scanner.nextLine();
        if (data.length() < 4)
            player.setPlace(rooms.get(Integer.parseInt(data) - 1));
        else {
            for (Passage p : passages)
                if (p.getNum() == Integer.parseInt(data))
                    player.setPlace(p);
        }

        while (scanner.hasNextLine()) {
            String[] item = scanner.nextLine().split(",");
            if (item[0].equals("keys"))
                player.collectItem(new Key(Integer.parseInt(item[2]), Integer.parseInt(item[3]),item[0]+","+item[1]));
            else player.collectItem(new Item(item[0]+","+item[1]));

        }

        scanner.close();
    }

    /**
     * Get the files paths depending on the state of the game
     * @throws Exception
     */
    private void getFiles() throws Exception {
        Scanner scanner = new Scanner(new File("TheGame/data/gameState.txt"));
        int s = Integer.parseInt(scanner.nextLine());
        scanner.close();
        // files to load if the game is started
        if (s == 0) {
            roomsUrl = "TheGame/data/start/Rooms.txt";
            playerUrl = "TheGame/data/start/player.txt";
            obstaclesUrl = "TheGame/data/start/Obstacles.txt";
        } else {
            // files load if the same if resumed
            roomsUrl = "TheGame/data/paused/Rooms.txt";
            playerUrl = "TheGame/data/paused/player.txt";
            obstaclesUrl = "TheGame/data/paused/Obstacles.txt";
        }
        connectionsUrl = "TheGame/data/start/Connections.txt";


    }

    /**
     * saves an indicator of the game state
     *
     * @param n 0- game paused 1- game restarted
     * @throws Exception if something goes wrong like loading the file
     */
    public void setState(Integer n) throws Exception {
        FileWriter writer = new FileWriter(new File("TheGame/data/gameState.txt"));
        writer.write(n.toString());
        writer.close();
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public ArrayList<Passage> getPassages() {
        return passages;
    }

    public Player getPlayer() {
        return player;
    }
}





