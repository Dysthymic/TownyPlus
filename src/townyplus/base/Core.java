//Bukkit Core addon v2.0
package townyplus.base;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Core extends JavaPlugin {
	private static final Logger logger = Logger.getLogger("minecraft");
	public static Core instance;

//For subclass only
	@Override
	abstract public void onEnable();
	@Override
	abstract public void onDisable();

//Logs
	public static void log(String msg) {
		logger.log(Level.INFO, "["+instance.getDescription().getName()+"] "+msg);
	}
	
	public static void log(Level lvl, String msg) {
		logger.log(lvl, "["+instance.getDescription().getName()+"] "+msg);
	}
	
//Event/Action Handlers
	public static void registerEvent(Listener listener, Event.Type... types) {
		for (Event.Type type:types) {
			instance.getServer().getPluginManager().registerEvent(type, listener, Event.Priority.Normal, instance);
		}
	}	

	public static void registerEvent(Listener listener, Event.Priority priority, Event.Type... types) {
		for (Event.Type type:types) {
			instance.getServer().getPluginManager().registerEvent(type, listener, priority, instance);
		}
	}

	public static void setExecutor(String names, CommandExecutor executor) {
		for (String name:names.split(" ")) {
			instance.getCommand(name).setExecutor(executor);
		}	
	}
	
//Message Handler
	public static void send(CommandSender target, String msg) {
		//if no codes, send
		if (! msg.contains("&")) {
			target.sendMessage(msg);
			return;
		}
		//Replace &'s with Color, 0-9, A-F
		for (ChatColor color: ChatColor.values()) {
			msg = msg.replace("&"+Integer.toHexString(color.getCode()),""+color);
		}	
		//if Not Player, strip color
		if (!(target instanceof Player)) {
			msg = ChatColor.stripColor(msg);
		}	
		target.sendMessage(msg);
	} 
	

	

	
}
