//MC
//v1.3.0

package townyplus;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class MC {
	public static final Logger logger = Logger.getLogger("minecraft");
	public static JavaPlugin plugin;
	public static String pluginName;
	public static File pluginFolder;
	public static ChatColor ccText = ChatColor.GOLD;
	public static ChatColor ccLit = ChatColor.YELLOW;
	public static ChatColor ccWarn = ChatColor.RED;
	public static ChatColor ccDim = ChatColor.GRAY;
	
	public static void load(JavaPlugin plugin) {
		//pdf = jp.getDescription();
		MC.plugin = plugin;
		pluginName = plugin.getDescription().getName();
		pluginFolder = plugin.getDataFolder();
		plugin.getDataFolder().mkdirs();
	}
	
	static void save() {
	}

//Logs
	public static void log(String msg) {
		logger.log(Level.INFO, "["+plugin.getDescription().getName()+"] "+msg);
	}
	public static void log(Level lvl, String msg) {
		logger.log(lvl, "["+plugin.getDescription().getName()+"] "+msg);
	}
	
//Event/Action Handlers
	public static void registerEvent(Event.Type type, Listener listener, Priority priority) {
		plugin.getServer().getPluginManager().registerEvent(type, listener, priority, plugin);
	}
	public static void registerEvent(Event.Type type, Listener listener) {
		plugin.getServer().getPluginManager().registerEvent(type, listener, Priority.Normal, plugin);
	}	
//Fast Subroutines
	public static Plugin getPlugin(String name) {
		return plugin.getServer().getPluginManager().getPlugin(name);
	}
	public static Server getServer() {
		return plugin.getServer();
	}
	
	
//Message Handler
	public static void send(CommandSender target, String msg) {
		if (target instanceof Player) {
			//Text,Dim,Lit,Warning
			if (msg.contains("~")) {
				msg = msg.replace("~t", ""+ccText);
				msg = msg.replace("~d", ""+ccDim);
				msg = msg.replace("~l", ""+ccLit);
				msg = msg.replace("~d", ""+ccDim);
				msg = msg.replace("~w", ""+ccWarn);
			}
			//Color Code replacements
			if (msg.contains("&")) {
				for (int i=0;i<16;i++) {
					String targetCode = "&"+ Integer.toHexString(i);
					msg = msg.replace(targetCode, ""+ChatColor.getByCode(i));
				}
			}
			target.sendMessage(ccText+msg);
			return;
		}
		if (msg.contains("~")) {
			msg = msg.replace("~t", "");
			msg = msg.replace("~d", "");
			msg = msg.replace("~l", "");
			msg = msg.replace("~w", "");
		}
		target.sendMessage(ChatColor.stripColor(msg));
	} 
	
	public static String lit(String input) {
		return ccLit+input+ccText;
	}
	public static String warn(String input) {
		return ccWarn+input+ccText;
	}
	public static String dim(String input) {
		return ccDim+input+ccText;
	}
	
}
