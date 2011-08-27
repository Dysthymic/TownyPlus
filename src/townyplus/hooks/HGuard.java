//WordGuard Hook v2.0
package townyplus.hooks;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class HGuard {
    public static WorldGuardPlugin handler = null;

    public static boolean load(JavaPlugin plugin) {
		if(! plugin.getServer().getPluginManager().isPluginEnabled("WorldGuard")){
			return false;
		}
        handler = (WorldGuardPlugin) plugin.getServer().getPluginManager().getPlugin("WorldGuard");
		return true;
    }    
    
    public static boolean canBuild(Player player, Location loc) {
        if (handler == null) return false;
        return handler.canBuild(player, loc);
    }

    public static ApplicableRegionSet guardAreas(Location loc) {
        if (handler == null) return null;
        Vector v = new Vector(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
        return handler.getRegionManager(loc.getWorld()).getApplicableRegions(v);
    }

    public static boolean isGuarded(Location loc) {
        if (handler == null) return false;
        Vector v = new Vector(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
        return (handler.getRegionManager(loc.getWorld()).getApplicableRegions(v).size() == 1);
    }
    
}
