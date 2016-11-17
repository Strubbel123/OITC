package me.strubbel.oitc.weapons;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Sniper {
    public static void onShot(Player p) {
        Location loc = p.getLocation();
        double pitch = ((loc.getPitch() + 90) * Math.PI) / 180;
        double yaw = ((loc.getYaw() + 90) * Math.PI) / 180;

        double x = Math.sin(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch) * Math.sin(yaw);
        double z = Math.cos(pitch);

        Vector direction = new Vector(x, z, y);
        double speed = 5;

        Arrow arrow = p.launchProjectile(Arrow.class);
        arrow.setGravity(false);
        arrow.setVelocity(new Vector(direction.getX(), direction.getY(), direction.getZ()).multiply(speed));
        arrow.setShooter(p);
    }
}
