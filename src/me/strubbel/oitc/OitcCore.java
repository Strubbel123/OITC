package me.strubbel.oitc;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class OitcCore extends JavaPlugin implements Listener {

    public static ArrayList<Fight> fights = new ArrayList<>();

    public Scoreboard board;
    public Objective kills;


    @Override
    public void onEnable(){
        this.getServer().getPluginManager().registerEvents(new EventManager(this), this);
        System.out.println("[OITC] Plugin erfolgreich geladen!");
        board = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        kills = board.registerNewObjective("test", "dummy");
        saveConfig();
    }



    //Tod
    public void onKill(Player p1, Player p2){
        for(Fight f : fights){
            if(f.getPunkte().containsKey(p2)){
                f.getPunkte().put(p2, f.getPunkte().get(p2) + 1);

                //Nachrichten und Scoreboard
                p1.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GRAY + "Du wurdest von " + ChatColor.GOLD + p2.getName() + ChatColor.GRAY +  " getötet!");
                p2.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GRAY + "Du hast " + ChatColor.GOLD + p1.getName() + ChatColor.GRAY +  " getötet!");
                Score score = kills.getScore(p2);
                score.setScore(f.getPunkte().get(p2));
                p2.setExp(0);
                p2.setLevel(f.getPunkte().get(p2));

                //Itemvergabe und setInventory
                p2.getInventory().addItem(new ItemStack(Material.ARROW, 1));
                p1.teleport(f.getArena().getRandomSpawn());
                this.setInventory(p1);

                //Spielende
                if(f.getPunkte().get(p2) >= 25){
                    for(Player all : getServer().getOnlinePlayers()){
                        all.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GOLD + p2.getName() + ChatColor.GRAY + "hat das Spiel gewonnen!");
                    }
                    onGameEnd(f);
                }
                break;
            }
        }

    }


    //Kitvergabe
    public void setInventory(Player p){
        p.setGameMode(GameMode.ADVENTURE);
        p.setHealth(20);
        p.setFoodLevel(20);
        p.getInventory().clear();
        p.getInventory().addItem(new ItemStack(Material.WOOD_SWORD, 1));
        p.getInventory().addItem(new ItemStack(Material.BOW, 1));
        p.getInventory().addItem(new ItemStack(Material.ARROW, 1));
    }


    //Spielstart
    public void onGameStart(Fight f, String Arena){
        f.setAktiv(true);
        kills.setDisplayName("§2§lKills");
        kills.setDisplaySlot(DisplaySlot.BELOW_NAME);

        for(Player all : f.getPunkte().keySet()){
            all.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GRAY + "Das Spiel wird nun gestartet!");
            all.setExp(0);
            all.setLevel(0);
            all.setScoreboard(board);
            Score score = kills.getScore(all);
            score.setScore(0);
            this.setInventory(all);
            all.teleport(f.getArena().getRandomSpawn());
        }
    }


    //Spielende
    public void onGameEnd(Fight f){
        for(Player p : f.getPunkte().keySet()){
            p.teleport(p.getWorld().getSpawnLocation());
        }
        board.clearSlot(DisplaySlot.BELOW_NAME);
        fights.remove(f);
    }


    //Kommandos
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdlabel, String[] args){
        Player p = (Player) sender;

        if(cmd.getName().equalsIgnoreCase("oitc")){

            if(args.length == 0)return false;

            //START
            if(args[0].equalsIgnoreCase("start") && args.length == 2){
                if(fights.size() != 0){
                    for(Fight f : fights){
                        if(f.getArena().getName().equalsIgnoreCase(args[1])){
                            if(!f.getAktiv() && f.getPunkte().size() >= 1){
                                onGameStart(f, args[1]);
                                return true;

                            } else if(f.getAktiv()){
                                p.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GRAY + "Das Spiel läuft bereits.");
                                return true;

                            } else {
                                p.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GRAY + "Es fehlen noch Spieler. " + ChatColor.GOLD + "(" + f.getPunkte().size() + "/2)");
                                return true;
                            }
                        } else {
                            p.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GRAY + "Die Arena " + ChatColor.GOLD + args[1] + ChatColor.GRAY + " existiert nicht.");
                            return true;
                        }
                    }
                } else {
                    p.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GRAY + "Es befindet sich niemand in dieser Arena.");
                    return true;
                }
            } else if(args[0].equalsIgnoreCase("start") && args.length != 2){
                p.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GRAY + "Gib eine " + ChatColor.GOLD + "Arena" + ChatColor.GRAY + " an.");
                return true;
            }

            //STOP
            if(args[0].equalsIgnoreCase("stop") && args.length == 2){
                if(fights.size() >= 0){
                    for(Fight f : fights){
                        if(f.getArena().getName().equalsIgnoreCase(args[1])){
                            if(f.getAktiv()){
                                for(Player all : getServer().getOnlinePlayers()){
                                    all.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GRAY + "Das Spiel wurde beendet.");
                                }
                                this.onGameEnd(f);
                                return true;
                            } else {
                                p.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GRAY + "Das Spiel wurde noch nicht gestartet!");
                                return true;
                            }
                        } else {
                            p.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GRAY + "Die Arena " + ChatColor.GOLD + args[1] + ChatColor.GRAY + " existiert nicht.");
                            return true;
                        }
                    }
                } else {
                    p.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GRAY + "Es läuft gerade kein Spiel in dieser Arena.");
                    return true;
                }
            } else if(args[0].equalsIgnoreCase("stop") && args.length != 2){
                p.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GRAY + "Gib eine " + ChatColor.GOLD + "Arena" + ChatColor.GRAY + " an.");
                return true;
            }

            //JOIN
            if((args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("add")) && args.length > 1){
                if(getConfig().contains("Arena." + args[1].toLowerCase())){
                    //falls für andere Spieler
                    Player p1 = p;
                    if(args.length==3){
                        p1 = Bukkit.getPlayerExact(args[2]);
                        p.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GOLD + p1.getName() + ChatColor.GRAY + " wurde dem Spiel hinzugefügt.");
                    }
                    p1.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GRAY + "Du wurdest dem Spiel hinzugefügt.");

                    //Nach Fight suchen
                    boolean found = false;
                    Fight f = new Fight();
                    for(Fight fight : fights){
                        if(fight.getArena().getName().equalsIgnoreCase(args[1])){
                            f = fight;
                            found = true;
                            break;
                        }
                    }

                    //Neuen Fight erstellen
                    if(!found) {
                        f = new Fight();
                        f.setArenaByName(args[1]);
                        fights.add(f);
                    }

                    f.getPunkte().put(p1, 0);
                    return true;
                } else {
                    p.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GRAY + "Die Arena " + ChatColor.GOLD + args[1] + ChatColor.GRAY + " existiert nicht!");
                    return true;
                }
            } else if(args[0].equalsIgnoreCase("join") && args.length < 2){
                p.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GRAY + "Gib eine " + ChatColor.GOLD + "Arena" + ChatColor.GRAY + " an!");
                return true;
            }


            //REMOVE
            if(args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("leave")){
                Player p1 = p;
                for(Fight f : fights){
                    if(args.length==2){
                        p1 = Bukkit.getPlayerExact(args[1]);
                        p.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GOLD + p1.getName() + ChatColor.GRAY + " wurde aus dem Spiel entfernt.");
                    }
                    if(f.getPunkte().containsKey(p1)){
                        if(f.getAktiv()){
                            f.getPunkte().remove(p1);
                            p1.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GRAY + "Du wurdest aus dem Spiel entfernt.");
                            return true;

                        } else {
                            p.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GRAY + "Du kannst während dem Spiel keine Spieler entfernen.");
                            return true;
                        }
                    }
                }
            }

            //SETSPAWN
            if(args[0].equalsIgnoreCase("setspawn") && args.length == 2){
                if(getConfig().contains("Arena." + args[1].toLowerCase())){
                    ArenaManager.setSpawn(p, args[1]);
                    p.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GRAY + "Der Spawn wurde gesetzt.");
                    return true;
                } else {
                    p.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GRAY + "Die Arena " + ChatColor.GOLD + args[1] + ChatColor.GRAY + " existiert nicht!");
                    return true;
                }
            } else if(args[0].equalsIgnoreCase("setspawn") && args.length != 2){
                p.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GRAY + "Gib eine " + ChatColor.GOLD + "Arena" + ChatColor.GRAY + " an.");
                return true;
            }

            //SETLOBBY
            if(args[0].equalsIgnoreCase("setlobby") && args.length == 2){
                ArenaManager.setLobby(p,args[1]);
                p.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GRAY + "Arena wurde erstellt.");
                return true;
            } else if(args[0].equalsIgnoreCase("setlobby") && args.length < 2){
                p.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GRAY + "Gib eine " + ChatColor.GOLD + "Arena" + ChatColor.GRAY + " an.");
                return true;
            }

            //LISTArenaS
            if(args[0].equalsIgnoreCase("list")){
                if(ArenaManager.listArenas().size() == 0){
                    p.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GRAY + "Es wurden noch keine Arenen gespeichert.");
                    return true;
                }
                p.sendMessage(ChatColor.GREEN + "[OITC] " + ChatColor.GRAY + "Liste aller Arenen:");
                int nr = 1;
                for(String a : ArenaManager.listArenas()){
                    p.sendMessage("§6" + nr + ". §7" + a);
                    nr++;
                }
                return true;
            }

        //KITSYSTEM
        }
        return false;
    }
}