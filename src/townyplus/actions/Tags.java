package townyplus.actions;

import townyplus.hooks.HPerm;
import ca.xshade.bukkit.towny.object.Town;
import townyplus.hooks.HTowny;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.crypto.Data;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionGroup;
import static townyplus.Data.*;
import townyplus.Main;

public class Tags implements CommandExecutor {
	static File folder;
	
	public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] vars) {
		if (vars.length==0) {
			if (!(cs instanceof Player)) return false;
			update((Player)cs);
			cs.sendMessage("Tag updated.");
			return true;
		}
		vars[0] = vars[0].toLowerCase();
		if (("?".equals(vars[0])) || ("help".equals(vars[0]))) {
			cs.sendMessage("--Tag Actions--");
			cs.sendMessage("/tag - Updates your own tag.");
			cs.sendMessage("--For ops and those with townyplus.tags permission:-");
			cs.sendMessage("/tag add (town name) (tag) - Add or update tag.");
			cs.sendMessage("/tag del (town name) - deletes tag.");
			cs.sendMessage("/tag get (town name) - Views current tag.");
			cs.sendMessage("/tag update (player name) - Updates tag for another player.");
			cs.sendMessage("/tag custom (player name) (tag) - Creates custom tag.");
			return true;
		}
		if ("add set".contains(vars[0])) {
			if (vars.length<3) return false;
			if (! HPerm.has(cs, "townyplus.tag")) return false;
			townTags.put(vars[1], vars[2]);
			Main.data.save();
			cs.sendMessage("Tag updated");
			return true;
		}
		if ("del delete remove".contains(vars[0])) {
			if (vars.length<2) return false;
			if (! HPerm.has(cs, "townyplus.tag")) return false;
			townTags.remove(townTags.get(vars[1]));
			Main.data.save();
			((Player)cs).sendMessage("Tag removed");
			return true;
		}		
		if ("get".equals(vars[0])) {
			if (vars.length<2) return false;
			((Player)cs).sendMessage("Town Tag: "+townTags.get(vars[1]));
			return true;
		}
		if (vars.length<2) {
			cs.sendMessage("Invalid command. See /tag ?");
			return true;
		}
		Player player = Main.instance.getServer().getPlayer(vars[1]);
		if (player != null) vars[1] = player.getName();
		if ("update".equals(vars[0])) {
			if (player == null) {
				cs.sendMessage("Cannot update tag for offline player");
				return true;
			}
			if (vars.length<2) return false;
			if (! HPerm.has(cs, "townyplus.tag")) return false;
			update(player);
			cs.sendMessage("Tag updated for "+vars[1]);
			return true;
		}
		if ("custom".equals(vars[0])) {
			if (! HPerm.has(cs, "townyplus.tag")) {
				cs.sendMessage("You don't have permission to modify tags.");
				return true;
			}
			if (vars.length<2) {
				cs.sendMessage("Must include name");
				return true;
			}
			Player target = Main.instance.getServer().getPlayer(vars[1]);
			if (target == null) {
				cs.sendMessage("Player must be online to add/edit custom tags.");
				return true;
			}
			//clear old tag
			if (vars.length<3) {
				//HPerm.handler.getUser(target).setOption("townyplus.tag.custom", "");
				cs.sendMessage("Custom tag removed.");
				update(target);
				return true;
			}
			//HPerm.handler.getUser(target).setOption("townyplus.tag.custom", vars[2]);
			update(target);
			((Player)cs).sendMessage("Custom tag made for "+target.getName());
			return true;
		}		
		cs.sendMessage("Unable to verify command. See /tag ?");
		return true;
	}
	
	public static void update(Player player) {
		if (player == null) return;
		if (HPerm.handler == null) return;
		if (HTowny.handler == null) return;
		//From Town
		Town town = HTowny.getTown(player);
		//String custom = HPerm.getInfo(player,"townyplus.tag.custom");
		String result = "";
		PermissionGroup[] groups = HPerm.handler.getUser(player).getGroups();
		if (groups.length>0) for (PermissionGroup group: groups) {
			result += group.getPrefix();
		}
		player.sendMessage(result);
		if (town != null) {
			if (townTags.containsKey(town.getName())) {
				HPerm.getUser(player).setPrefix(result+townTags.get(town.getName()),null);
			} else {
				HPerm.getUser(player).setPrefix(result+"&f[&7"+town.getName()+"&f]",null);
			}
		} else {
			HPerm.getUser(player).setPrefix(result,null);
		}
	}
}
