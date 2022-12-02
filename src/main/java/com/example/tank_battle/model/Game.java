package com.example.tank_battle.model;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Game {

    private static Game instance = new Game();
    public final String DATABASE_PATH = "db/score.txt";
    private String player1;
    private String player2;

    private List<Avatar> avatars;

    private List<AvatarDAO> daos = new ArrayList<>();

    private Game(){};

    public static Game getInstance(){
        return instance;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public List<Avatar> getAvatars() {
        return avatars;
    }

    public void setAvatars(List<Avatar> avatars) {
        this.avatars = avatars;
    }

    public void launch(){
        try {
            File file = new File(DATABASE_PATH);

            String line = "";
            line+= player1 + " " +  "0 \n";
            line+= player2 + " " +  "0 \n";
            line+= "CPU 0";

            daos.add(new AvatarDAO(player1));
            daos.add(new AvatarDAO(player2));
            daos.add(new AvatarDAO("CPU"));

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(line.getBytes(StandardCharsets.UTF_8));
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void winner(Avatar avatar){

        try{

            File file = new File(DATABASE_PATH);

            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null){
                if(line.contains(avatar.getName())) {
                    String[] parts = line.split(" ");
                    int wins = Integer.parseInt(parts[1]) + 1;
                    line = line.replace(parts[1], wins + "");
                }

                sb.append(line);
                sb.append("\n");
            }

            fis.close();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            fos.close();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void update(){

        try{

            File file = new File(DATABASE_PATH);

            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            String line;

            while ((line = reader.readLine()) != null){

                String[] parts = line.split(" ");

                daos.set(
                        searchByName(parts[0]),
                        new AvatarDAO(parts[0], Integer.parseInt(parts[1]))
                );

            }

            fis.close();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private int searchByName(String name){
        int pos = -1;

        for(int i = 0; i < daos.size(); i++){
            if (daos.get(i).getName().equals(name))
                return i;
        }

        return pos;
    }

    public List<AvatarDAO> getDaos(){
        return daos;
    }

}
