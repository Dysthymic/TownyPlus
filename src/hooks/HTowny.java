//*** Hook Designed by spathwalker. Plugins (c) their respective owners.

package hooks;

import ca.xshade.bukkit.towny.NotRegisteredException;
import ca.xshade.bukkit.towny.Towny;
import ca.xshade.bukkit.towny.object.Coord;
import ca.xshade.bukkit.towny.object.Resident;
import ca.xshade.bukkit.towny.object.Town;
import ca.xshade.bukkit.towny.object.TownBlock;
import ca.xshade.bukkit.towny.object.TownyUniverse;
import ca.xshade.bukkit.towny.object.WorldCoord;
import org.bukkit.Location;
import townyplus.MC;

public class HTowny {
    public static Towny handler = null;
    public static boolean hooked = false;
    public static TownyUniverse universe;
    
    public static boolean load() {
        handler = (Towny) MC.server.getPluginManager().getPlugin("Towny");
        if (handler == null) return false;
        universe = handler.getTownyUniverse();
        hooked = true;
        return true;
    }
   
    public static TownyUniverse getUniverse() {
        if (handler == null) return null;
        return handler.getTownyUniverse();
    }
    
    public static TownBlock getPlot(Location location) {
        try {
            return new WorldCoord(universe.getWorld(location.getWorld().getName()), Coord.parseCoord(location)).getTownBlock();
        } catch (NotRegisteredException ex) {
            return null;
        }
    }

    public static Town getTown(Location location) {
        try {
            return new WorldCoord(universe.getWorld(location.getWorld().getName()), Coord.parseCoord(location)).getTownBlock().getTown();
        } catch (Exception ex) {
            return null;
        }
    }

    public static Town getTown(String name) {
        try {
            return universe.getResident(name).getTown();
        } catch (Exception ex) {
            return null;
        }
    }
    
    public static Resident getResident(String name) {
        try {
            return universe.getResident(name);
        } catch (Exception ex) {
            return null;
        }
    }
    
    public static boolean canBuild(TownBlock plot, Resident res) {
        try {
          //If Plot is in wild
            if (plot == null) return true;
          //If Player not part of that town
            if (plot.getTown() != res.getTown()) return false;
          //If Plot Unowned
            if (! plot.hasResident()) return true;
          //IF you the plot owner, mayor, or owners friend 
            if (plot.getResident() == res) return true;                      
            if (plot.getTown().getMayor() == res) return true;               
            if (plot.getResident().hasFriend(res)) return true;              
          //Otherwise..
            return false;
        } catch (NotRegisteredException ex) {
          //All Other Conditions
            return false;
        }
    }
    
}
