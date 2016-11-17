package me.strubbel.oitc;

import org.bukkit.Location;

import java.util.List;

public class Arena {

    private String name;
    private String displayName;
    private Location lobby;
    private List<Location> spawns;

    public Arena(String name){
        this.name = name.toLowerCase();
        this.displayName = ArenaManager.getArenaName(name);
        this.lobby = ArenaManager.getArenaLobby(name);
        this.spawns = ArenaManager.getArenaSpawns(name);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName(){
        return getDisplayName();
    }

    public Location getRandomSpawn(){
        List<Location> all = this.getSpawns();
        return(all.get((int)(Math.random()*spawns.size())));
    }

    public Location getLobby() {
        return lobby;
    }

    public List<Location> getSpawns() {
        return spawns;
    }
}
