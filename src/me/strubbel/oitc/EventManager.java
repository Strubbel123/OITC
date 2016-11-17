package me.strubbel.oitc;

import me.strubbel.oitc.weapons.Shotgun;
import me.strubbel.oitc.weapons.Sniper;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class EventManager implements Listener {

    private OitcCore m;
    public EventManager(OitcCore main){
        this.m = main;
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e){
        for(Fight f : OitcCore.fights){
            if(f.getPunkte().containsKey(e.getPlayer())){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent e){
        for(Fight f : OitcCore.fights){
            if(f.getPunkte().containsKey(e.getPlayer())){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFoodLevelChangeEvent(FoodLevelChangeEvent e){
        Player p = (Player) e.getEntity();
        for(Fight f : OitcCore.fights){
            if(f.getPunkte().containsKey(p)){
                e.setCancelled(true);
                p.setFoodLevel(20);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        Player p = e.getEntity();
        for(Fight f : OitcCore.fights){
            if(f.getPunkte().containsKey(p) && f.getAktiv()){
                m.setInventory(p);
                p.teleport(f.getArena().getRandomSpawn());
                return;
            } else if(f.getPunkte().containsKey(p) && (!f.getAktiv())){
                p.teleport(f.getArena().getLobby());
                return;
            }
        }
    }

    @EventHandler
    public void onItemHau(PlayerInteractEvent e){
        Player p = e.getPlayer();
        try{
            if(p.getItemInHand().getItemMeta().getDisplayName().toLowerCase().contains("shotgun")){
                e.setCancelled(true);
                Shotgun.onShot(p);
            } else if (p.getItemInHand().getItemMeta().getDisplayName().toLowerCase().contains("sniper")){
                e.setCancelled(true);
                Sniper.onShot(p);
            }
        } catch(NullPointerException exception){}
    }

    @EventHandler
    public void onBowShot(EntityShootBowEvent e){
        if(e.getEntity() instanceof Player) {
            Player p = (Player)e.getEntity();
            for (Fight f : OitcCore.fights) {
                if (f.getPunkte().containsKey((Player) e.getEntity())) {
                    e.getBow().setDurability((short)2);
                }
            }
        }
    }

    //Spieler mit Pfeil getroffen
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Player && e.getEntity() instanceof Player){
            Player p1 = (Player) e.getEntity();
            for(Fight f : OitcCore.fights){
                if(f.getPunkte().containsKey(p1)){
                    if(f.getAktiv()){

                        //Bei einem Bogentreffer
                        if(e.getDamager() instanceof Arrow && e.getEntity() instanceof Player) {

                            final Arrow arrow = (Arrow)e.getDamager();
                            final Player p2 = (Player)arrow.getShooter();
                            arrow.remove();
                            if(!p1.getName().equals(p2.getName())&& f.getPunkte().containsKey(p1) && f.getPunkte().containsKey(p2)){
                                e.setCancelled(true);
                                m.onKill(p1, p2);
                            }
                        }


                        if(e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
                            final Player p2 = (Player) e.getDamager();
                            if(p1.getHealth()-e.getDamage()<= 0 && f.getPunkte().containsKey(p1) && f.getPunkte().containsKey(p2)) {
                                e.setCancelled(true);
                                m.onKill(p1, p2);
                            }
                        }
                        break;
                    }
                    else if(f.getPunkte().containsKey(p1)){
                        e.setCancelled(true);
                        break;
                    }
                }
            }
        }
    }
}
