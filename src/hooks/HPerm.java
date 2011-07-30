//*** Hook Designed by spathwalker. Plugins (c) their respective owners.

package hooks;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import townyplus.MC;

public class HPerm {
    public static PermissionHandler handler;
    public static boolean hooked = false;
    
    public static boolean load() {
        Plugin permissionsPlugin = MC.server.getPluginManager().getPlugin("Permissions");
        if (permissionsPlugin == null) return false;
        handler = ((Permissions) permissionsPlugin).getHandler();
        hooked = true;
        return true;
    }
    
    public static boolean has(CommandSender cs, String action) {
        //Console and Ops always aproved
        if (!(cs instanceof Player)) return true;
        if (((Player)cs).isOp()) return true;
        //no handler or no actions denied
        if ((handler == null) || (action.isEmpty())) return false;
        //Single Check
        if (! action.contains(" ")) return handler.has((Player)cs, action);
        //Multi Check
        String[] split = action.split(" ");
        for (int i=0;i<split.length;i++) {
            if (handler.has((Player)cs, split[i])) return true;
        }
        return false;
    }
    
    public static boolean hasRank(CommandSender cs, String rankName, String[] ranks, String action) {
        //Console and Ops always aproved
        if (!(cs instanceof Player)) return true;
        if (((Player)cs).isOp()) return true;
        //no handler or no actions denied
        if ((handler == null) || (action.isEmpty())) return false;
        //Check for individual permission
        if (handler.has((Player)cs, rankName+"."+action)) return true;
        //Create List
        List<String> rankList = Arrays.asList(ranks);
        if (! rankList.contains(action)) return false;
        //Get Player Rank
        String rank = getInfo((Player)cs, rankName);
        if (! rankList.contains(rank)) return false;
        //If Player rank not high enough.
        if (rankList.lastIndexOf(rank) >= rankList.lastIndexOf(action)) return true;
        return false;
    }
    
    public static String getInfo(Player player, String action) {
        if (handler == null) return "";
        return handler.getInfoString(player.getWorld().getName(), player.getName(), action, false);
    }
    
    public static boolean setInfo(Player player, String action, String value) {
        if (handler == null) return false;
        handler.addUserInfo("world", player.getName(), action, value);
        return true;
    }
    
}
