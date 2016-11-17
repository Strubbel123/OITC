package me.strubbel.oitc;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Fight {
    HashMap<Player, Integer> punkte = new HashMap<>();
    Arena arena;
    boolean aktiv;

    public Arena getArena() {
        return arena;
    }
    public void setArena(Arena arena) {
        this.arena = arena;
    }
    public void setArenaByName(String name){
        this.arena = new Arena(name);
    }
    public HashMap<Player, Integer> getPunkte() {
        return punkte;
    }
    public boolean getAktiv() {
        return aktiv;
    }
    public void setAktiv(boolean aktiv) {
        this.aktiv = aktiv;
    }
}