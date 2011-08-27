package townyplus.actions;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import townyplus.hooks.HTowny;

public class TownChat implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmnd, String lable, String[] strings) {
        if (! (sender instanceof Player)) return false;
        Player player = (Player)sender;
        //If already in mode, go back to general chat
        if (HTowny.handler.hasPlayerMode(player, lable)) {
            HTowny.handler.removePlayerMode(player);
            player.sendMessage(ChatColor.AQUA+"You are now using general chat.");
        //If already in mode, go back to general chat
        } else if (lable.equals("tc")) {
            HTowny.handler.setPlayerMode(player, "tc".split(" "));
            player.sendMessage(ChatColor.AQUA+"You are now using town chat.");
        //If already in mode, go back to general chat
        } else if (lable.equals("nc")) {
            HTowny.handler.setPlayerMode(player, "nc".split(" "));
            player.sendMessage(ChatColor.AQUA+"You are now using nation chat.");
        }        
		return true;
	}
	
}
