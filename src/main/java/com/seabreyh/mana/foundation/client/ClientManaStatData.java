package com.seabreyh.mana.foundation.client;

public class ClientManaStatData {
    private static int playerManaStat;
    private static int playerManaCapacity;

    public static void setCurrent(int mana_val) {
        ClientManaStatData.playerManaStat = mana_val;
    }

    public static void setCapacity(int mana_val) {
        ClientManaStatData.playerManaCapacity = mana_val;
    }

    public static int getPlayerManaStat() {
        return playerManaStat;
    }

    public static int getPlayerManaCapacity() {
        return playerManaCapacity;
    }
}
