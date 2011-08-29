package townyplus.actions;

import ca.xshade.bukkit.towny.object.Resident;
import ca.xshade.bukkit.towny.object.Town;
import ca.xshade.bukkit.util.ChatTools;
import townyplus.hooks.HTowny;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import townyplus.Main;

public class Live implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmnd, String lable, String[] strings) {
        Player player = (Player)sender;
        String result = "";
        if (strings.length == 0) {
            player.sendMessage(ChatTools.formatTitle(Main.instance.getServer().getOnlinePlayers().length +" Players Online"));
            //Non Towned
			Player[] onlinePlayers = Main.instance.getServer().getOnlinePlayers();
			for (Player target:onlinePlayers) {
                if (! HTowny.getResident(target).hasTown()) {
                    result += target.getName()+", ";
                }
			}
            if (! result.isEmpty()) {
				result = result.substring(0, result.length()-2);
				player.sendMessage(ChatColor.AQUA+"Wilderness: "+ChatColor.GRAY+result);
			}
            //Per Town
            Iterator<Town> towns = HTowny.handler.getTownyUniverse().getTowns().iterator();
            while (towns.hasNext()) {
                Town town = towns.next();
                result = getOnlineResidents(town);
                if (! result.equals("")) {
                    player.sendMessage(ChatColor.AQUA+town.getName()+": "+ChatColor.GRAY+result);
                }
            }
        } else {
            Town town = HTowny.getTown(strings[0]);
            player.sendMessage(ChatTools.formatTitle("Players Online - "+town.getName()));
            player.sendMessage(ChatColor.GRAY+getOnlineResidents(town));
        }
        return true;
    }

    public String getOnlineResidents(Town town) {
        String playerString = "";
        Iterator<Player> players = HTowny.handler.getTownyUniverse().getOnlinePlayers(town).iterator();
        while (players.hasNext()) {
            if (! playerString.equals("")) playerString += ", ";
            Player player = players.next();
            Resident res = HTowny.getResident(player);
            if (res.isMayor()) playerString += "**";
            if (town.getAssistants().contains(res)) playerString += "*";
            playerString += player.getName();
        }
        return playerString;
    }
}
