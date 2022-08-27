package com.seabreyh.mana.client;

public class ClientManaStatData {
    private static int playerManaStat;

    public static void set(int mana_val) {
        ClientManaStatData.playerManaStat = mana_val;
    }

    public static int getPlayerManaStat() {
        return playerManaStat;
    }
}
