package townyplus;

import townyplus.actions.Tags;
import townyplus.actions.Live;
import townyplus.hooks.HPerm;
import ca.xshade.bukkit.towny.object.WorldCoord;
import townyplus.hooks.HGuard;
import townyplus.hooks.HTowny;
import java.util.logging.Level;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import townyplus.actions.Tax;
import townyplus.actions.TaxTimer;
import townyplus.actions.TownChat;
import townyplus.base.Core;
import townyplus.listener.TPBlockListener;
import townyplus.listener.TPEntityListener;
import townyplus.listener.TPPlayerListener;
import townyplus.util.Time;

public class Main extends Core {
	public static WorldCoord worldCoord;
	public static Config config = new Config();
	public static Data data = new Data();
	private static int taxTask = 0;

	public void onEnable() {
		instance = this;
		//Load Subroutines & Hooks
		if (! HTowny.load(this)) {
			log("Unable to load towny.");
		}
		HPerm.load(this); 
		HGuard.load(this);
		config.load(this);
		data.load(this);
		
		registerEvent(new TPPlayerListener(), Priority.Highest, 
			Type.PLAYER_INTERACT, Type.PLAYER_COMMAND_PREPROCESS);
		registerEvent(new TPEntityListener(), Priority.Highest, 
			Type.ENTITY_DAMAGE);
		registerEvent(new TPBlockListener(), Priority.Highest, 
			Type.BLOCK_PLACE, Type.BLOCK_PISTON_EXTEND, Type.BLOCK_PISTON_RETRACT);

		setExecutor("tag", new Tags());
		setExecutor("tc nc", new TownChat());
		setExecutor("live", new Live());
		setExecutor("Tax", new Tax());

		Tax.load();
		//Starts Check every minute.
		taxTask = getServer().getScheduler().scheduleSyncRepeatingTask(this, new TaxTimer(), 600, 20*60);
		log(Level.INFO,"Plugin active.");
	}

	public void onDisable() {
		data.save();
		getServer().getScheduler().cancelTask(taxTask);
		log(Level.INFO,"Plugin disabled.");
	}

	//Chat Commands
	@Override
	public boolean onCommand(CommandSender sender, Command cmnd, String lable, String[] strings) {
		sender.sendMessage("&cError");
		return true;
	}


	
}