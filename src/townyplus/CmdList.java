package townyplus;

import ca.xshade.bukkit.towny.object.Resident;
import ca.xshade.bukkit.towny.object.Town;
import ca.xshade.bukkit.util.ChatTools;
import hooks.HTowny;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdList implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmnd, String lable, String[] strings) {
        Player player = (Player)sender;
        String playerString = "";
        if (strings.length == 0) {
            player.sendMessage(ChatTools.formatTitle(MC.server.getOnlinePlayers().length +" Players Online"));
            //Non Towned
            Player[] online = HTowny.universe.getOnlinePlayers();
            for (Integer i=0;i<online.length;i++) {
                if (! HTowny.getResident(online[i].getName()).hasTown()) {
                    if (i > 0) playerString += ", ";
                    playerString += online[i].getName();
                }
            }

            if (! playerString.equals("")) player.sendMessage(ChatColor.AQUA+"Wilderness: "+ChatColor.GRAY+playerString);
            //Per Town
            Iterator<Town> towns = HTowny.universe.getTowns().iterator();
            while (towns.hasNext()) {
                Town town = towns.next();
                playerString = getOnlineResidents(town);
                if (! playerString.equals("")) {
                    player.sendMessage(ChatColor.AQUA+town.getName()+": "+ChatColor.GRAY+playerString);
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
        Iterator<Player> players = HTowny.universe.getOnlinePlayers(town).iterator();
        while (players.hasNext()) {
            if (! playerString.equals("")) playerString += ", ";
            Player player = players.next();
            Resident res = HTowny.getResident(player.getName());
            if (res.isMayor()) playerString += "**";
            if (town.getAssistants().contains(res)) playerString += "*";
            playerString += player.getName();
        }
        return playerString;
    }
}
