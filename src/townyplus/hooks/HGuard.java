//*** Hook Designed by spathwalker. Plugins (c) their respective owners.

package townyplus.hooks;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import townyplus.MC;

public class HGuard {
    public static WorldGuardPlugin handler;
    public static boolean hooked = false;

    public static boolean load() {
        handler = (WorldGuardPlugin) MC.getServer().getPluginManager().getPlugin("WorldGuard");
        if (handler == null) return false;
        hooked = true;
        return true;
    }    
    
    public static boolean canBuild(Player player, Location loc) {
        return handler.canBuild(player, loc);
    }

    public static ApplicableRegionSet guardAreas(Location loc) {
        Vector v = new Vector(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
        return handler.getRegionManager(loc.getWorld()).getApplicableRegions(v);
    }
}
