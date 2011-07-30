package townyplus;

import com.sun.corba.se.impl.orb.ParserTable.TestAcceptor1;
import org.bukkit.plugin.PluginDescriptionFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class MC {
    static final Logger logger = Logger.getLogger("minecraft");
    static JavaPlugin jp;
    static public Server server;
    static PluginDescriptionFile pdf;
    
    static void load(JavaPlugin jp) {
        pdf = jp.getDescription();
        MC.jp = jp;
        server = jp.getServer();
        jp.getDataFolder().mkdirs();
        //createDefaultConfiguration("config.yml");
    }
    
    static void save() {

    }
    
//Logs
    static void log(String msg) {
        logger.log(Level.INFO, "["+pdf.getName()+"] "+msg);
    }
    static void log(Level lvl, String msg) {
        logger.log(lvl, "["+pdf.getName()+"] "+msg);
    }
    
//Event Register
    static void registerEvent(Event.Type type, Listener listener, Priority priority) {
        jp.getServer().getPluginManager().registerEvent(type, listener, priority, jp);
    }
    static void registerEvent(Event.Type type, Listener listener) {
        jp.getServer().getPluginManager().registerEvent(type, listener, Priority.Normal, jp);
    }    
    
//Messages
    static void send(CommandSender target, String msg) {
        if (!(target instanceof Player)) msg = ChatColor.stripColor(msg);
        target.sendMessage(msg);
    } 
    
}
