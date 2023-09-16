package com.javagame.game;

public class WorldCreation {
    public static int[][] create2DMap(int tilewidth,int tileheight,int fill){
        int[][] Map = new int[tilewidth][tileheight];
        for(int x = 0; x < tilewidth; x++) {
            for(int y = 0; y < tileheight; y++) {
                Map[x][y] = fill;
            }
        }
        return Map;
    }
}
