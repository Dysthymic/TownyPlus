//PermssionsEx Hook v2.0

package townyplus.hooks;

import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class HPerm {
    public static PermissionManager handler = null;

    public static boolean load(JavaPlugin plugin) {
		if(! plugin.getServer().getPluginManager().isPluginEnabled("PermissionsEx")){
			return false;
		}
		handler = PermissionsEx.getPermissionManager();
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
        return handler.getUser(player).getOption(action);
    }
    
    public static void setInfo(Player player, String action, String value) {
        if (handler == null) return;
        handler.getUser(player).setOption(action, value);
    }
    
	public static PermissionUser getUser(Player player) {
        if (handler == null) return null;
		return handler.getUser(player);
	}	
}
