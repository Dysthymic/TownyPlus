package townyplus.listener;

import ca.xshade.bukkit.towny.NotRegisteredException;
import ca.xshade.bukkit.towny.object.Resident;
import ca.xshade.bukkit.towny.object.Town;
import ca.xshade.bukkit.towny.object.TownBlock;
import java.util.Arrays;
import java.util.List;
import townyplus.hooks.HTowny;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import townyplus.Data;
import townyplus.Main;
import townyplus.base.Core;
import townyplus.hooks.HPerm;

public class TPPlayerListener extends PlayerListener {
    public static List<Material> ProtectedBlocks = Arrays.asList(
		Material.CHEST, Material.STORAGE_MINECART,
		Material.FURNACE, Material.BURNING_FURNACE,
		Material.DISPENSER, Material.JUKEBOX);

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		//Towny Chest Portection
        if (! canUse(event.getPlayer(), event.getClickedBlock())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("You can't use " + event.getClickedBlock().getType().name() + " here.");
        }
    }

    public static boolean canUse(Player player, Block block) {
        if (block == null) return true;
        if (player.isOp()) return true;
		if (HPerm.has(player, "townyplus.admin")) return true;
        if (HTowny.handler == null) return false;
        if (! ProtectedBlocks.contains(block.getType())) return true;
        TownBlock plot = HTowny.getPlot(block.getLocation());
        if (plot == null) return true; //Wilderness
        Resident playerRes = HTowny.getResident(player);
        return HTowny.canBuild(plot, playerRes);
    }	

	@Override
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage();
		//On Name Change
		String[] vars = msg.toLowerCase().split(" ");
        //Mayor Guard
        if (! isMayorOnly(vars)) return;
        if (! HTowny.getResident(event.getPlayer()).isMayor()) {
            Core.send(event.getPlayer(),"&cYou do not have permission for this.");
            event.setCancelled(true);
        }
		if (vars.length<4) return;
        if ("setname".equals(vars[1]+vars[2])) {
			Town town = HTowny.getTown(event.getPlayer());
			for (String key:Data.pastDue.keySet()) {
				if (key.startsWith(town.getName()+" ")) {
					String newKey = vars[3] + " "+key.split(" ")[1];
					Data.pastDue.put(newKey, Data.pastDue.get(key));
					Data.pastDue.remove(key);
				}
			}
			Main.data.save();
		} 
    }

	public static boolean isMayorOnly(String[] vars) {
        //non-commands & Non Town Commands: false
        if (vars.length<2) return false;
        if (! "/town /t".contains(vars[0])) return false;
        //Withdraw commands: true
        if ("withdraw".equals(vars[1])) return true; 
        //set Mayor commands: true
        if (vars.length<3) return false;
        if ("setname".equals(vars[1]+vars[2])) return true; 
        if ("mayor".equals(vars[2])) return true;
        return false;
    }
		
	


	


	

}

