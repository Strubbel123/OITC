package me.strubbel.oitc;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class ArenaManager {



	private static Plugin plugin = Bukkit.getPluginManager().getPlugin("OITC");
	private static Configuration cfg = plugin.getConfig();
	private OitcCore m;
	public ArenaManager(OitcCore main){
		this.m = main;
	}



	public static void setLobby(Player p, String a){
		ConfigurationSection section = cfg.createSection("Arena." + a.toLowerCase());
		section.set("name", a);
		section.set("world", p.getLocation().getWorld().getName());
		section.set("x", p.getLocation().getX());
		section.set("y", p.getLocation().getY());
		section.set("z", p.getLocation().getZ());
		plugin.saveConfig();
	}


	public static void setSpawn(Player p, String a){
		int b = 0;
		if(plugin.getConfig().contains("Arena." + a.toLowerCase() + ".Spawns")){
			b = plugin.getConfig().getConfigurationSection("Arena." + a.toLowerCase() + ".Spawns").getKeys(false).size();
		}
		ConfigurationSection section = cfg.createSection("Arena." + a.toLowerCase() + ".Spawns." + b);

		section.set("x", p.getLocation().getX());
		section.set("y", p.getLocation().getY());
		section.set("z", p.getLocation().getZ());
		plugin.saveConfig();
	}

	public static Location getArenaLobby(String a){
		ConfigurationSection section = cfg.getConfigurationSection("Arena." + a);
		System.out.println(a);
		World w = Bukkit.getServer().getWorld(section.getString("world"));
		double x = section.getDouble("x");
		double y = section.getDouble("y");
		double z = section.getDouble("z");
		return(new Location(w,x,y,z));
	}

	public static List<Location> getArenaSpawns(String a){
		ConfigurationSection section = cfg.getConfigurationSection("Arena." + a + ".Spawns");
		List<Location> list = new ArrayList<>();

		for(int i = 0; i < section.getKeys(false).size(); i++) {
			World w = Bukkit.getServer().getWorld(plugin.getConfig().getString("Arena." + a + ".world"));
			double x = section.getDouble(i + ".x");
			double y = section.getDouble(i + ".y");
			double z = section.getDouble(i + ".z");
			list.add(new Location(w, x, y, z));
		}

		return(list);
	}

	public static String getArenaName(String name){
		String displayName = cfg.getString("Arena." + name + ".name");
		return(displayName);
	}

	public static List<String> listArenas(){
		List<String> list = new ArrayList<>();
		for(String a : cfg.getConfigurationSection("Arena").getKeys(false)){
			list.add(cfg.getString("Arena." + a + ".name"));
		}
		return(list);
	}
}