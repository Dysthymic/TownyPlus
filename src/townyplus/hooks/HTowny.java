//Towny Hook v2.0
package townyplus.hooks;

import ca.xshade.bukkit.towny.NotRegisteredException;
import ca.xshade.bukkit.towny.Towny;
import ca.xshade.bukkit.towny.object.Coord;
import ca.xshade.bukkit.towny.object.Resident;
import ca.xshade.bukkit.towny.object.Town;
import ca.xshade.bukkit.towny.object.TownBlock;
import ca.xshade.bukkit.towny.object.TownyUniverse;
import ca.xshade.bukkit.towny.object.WorldCoord;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class HTowny {
	public static Towny handler = null;
	
    public static boolean load(JavaPlugin plugin) {
		if(! plugin.getServer().getPluginManager().isPluginEnabled("Towny")){
			return false;
		}
		handler = (Towny) plugin.getServer().getPluginManager().getPlugin("Towny");
		return true;
	}
   
	public static TownyUniverse getUniverse() {
		if (handler == null) return null;
		return handler.getTownyUniverse();
	}
	
	public static TownBlock getPlot(Location location) {
		if (handler == null) return null;
		try {
			return new WorldCoord(handler.getTownyUniverse().getWorld(location.getWorld().getName()), Coord.parseCoord(location)).getTownBlock();
		} catch (NotRegisteredException ex) {
			return null;
		}
	}

	public static Town getTown(Location location) {
		if (handler == null) return null;
		try {
			return new WorldCoord(handler.getTownyUniverse().getWorld(location.getWorld().getName()), Coord.parseCoord(location)).getTownBlock().getTown();
		} catch (Exception ex) {
			return null;
		}
	}

	public static Town getTown(String name) {
		if (handler == null) return null;
		try {
			return handler.getTownyUniverse().getTown(name);
		} catch (Exception ex) {
			return null;
		}
	}

	public static Town getTown(Player player) {
		if (handler == null) return null;
		try {
			return handler.getTownyUniverse().getResident(player.getName()).getTown();
		} catch (Exception ex) {
			return null;
		}
	}
	
	public static Resident getResident(Player player) {
		if (handler == null) return null;
		try {
			return handler.getTownyUniverse().getResident(player.getName());
		} catch (Exception ex) {
			return null;
		}
	}
	
	public static Resident getResident(String playerName) {
		if (handler == null) return null;
		try {
			return handler.getTownyUniverse().getResident(playerName);
		} catch (Exception ex) {
			return null;
		}
	}	
	
	public static boolean canBuild(TownBlock plot, Resident res) {
		if (handler == null) return false;
		try {
		  //If Plot is in wild
			if (plot == null) return true;
		  //If Player not part of that town
			if (! plot.getTown().equals(res.getTown())) return false;
		  //If Plot Unowned
			if (! plot.hasResident()) return true;
		  //IF you the plot owner, mayor, or owners friend 
			if (plot.getResident().equals(res)) return true;					  
			if (plot.getTown().getMayor().equals(res)) return true;			   
			if (plot.getResident().hasFriend(res)) return true;			  
		  //Otherwise..
			return false;
		} catch (NotRegisteredException ex) {
		  //All Other Conditions
			return false;
		}
	}
	
}
