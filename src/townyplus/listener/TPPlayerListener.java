package townyplus.listener;

import ca.xshade.bukkit.towny.NotRegisteredException;
import townyplus.hooks.HTowny;
import org.bukkit.ChatColor;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import townyplus.TownyPlus;

public class TPPlayerListener extends PlayerListener {

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!TownyPlus.canUse(event.getPlayer(), event.getClickedBlock())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("You can't use " + event.getClickedBlock().getType().name() + " here.");
        }
    }
    
    @Override
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage();
        //Mayor Guard
        if (! isMayorOnly(msg.toLowerCase().split(" "))) return;
        if (! HTowny.getResident(event.getPlayer().getName()).isMayor()) {
            event.getPlayer().sendMessage(ChatColor.RED+"You do not have permission for this.");
            event.setCancelled(true);
        }
    }
    
    public boolean isMayorOnly(String[] vars) {
        //non-commands & Non Town Commands: false
        if (vars.length<2) return false;
        if (! "/town /t".contains(vars[0])) return false;
        //Withdraw commands: true
        if ("withdraw".equals(vars[1])) return true; 
        //set Mayor commands: true
        if (vars.length<3) return false;
        if ("mayor".equals(vars[2])) return true;
        return false;
    }

}

