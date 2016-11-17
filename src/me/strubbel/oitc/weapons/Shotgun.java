package me.strubbel.oitc.weapons;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Shotgun{

    public static void onShot(Player  p){
        Location loc = p.getLocation();
        double pitch = ((loc.getPitch() + 90) * Math.PI) / 180;
        double yaw  = ((loc.getYaw() + 90)  * Math.PI) / 180;

        double x = Math.sin(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch) * Math.sin(yaw);
        double z = Math.cos(pitch);

        Vector direction = new Vector(x, z, y);
        double speed = 1.2;
        double spray = 3D;

        for(int i = 0; i < 10; i++) {
            Arrow arrow = p.launchProjectile(Arrow.class);
            arrow.setVelocity(new Vector(direction.getX() + (Math.random() - 0.5) / spray, direction.getY() + (Math.random() - 0.5) / spray, direction.getZ() + (Math.random() - 0.5) / spray).multiply(speed));
            arrow.setShooter(p);
        }
    }
}

