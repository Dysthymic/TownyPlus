package townyplus;

import hooks.HPerm;
import ca.xshade.bukkit.towny.NotRegisteredException;
import ca.xshade.bukkit.towny.object.Town;
import com.nijiko.permissions.Entry;
import com.nijiko.permissions.Group;
import com.nijiko.permissions.User;
import hooks.HTowny;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Properties;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Tags implements CommandExecutor {
    static File folder;
    static Properties townTags = new Properties();
    
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] vars) {
        if (vars.length==0) {
            if (!(cs instanceof Player)) return false;
            update((Player)cs);
            cs.sendMessage("Tag updated.");
            return true;
        }
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
        if ("add".equals(vars[0])) {
            if (vars.length<3) return false;
            if (! HPerm.has(cs, "townyplus.tag")) return false;
            townTags.setProperty(vars[1], vars[2]);
            save();
            ((Player)cs).sendMessage("Tag updated");
            return true;
        }
        if ("del".equals(vars[0])) {
            if (vars.length<2) return false;
            if (! HPerm.has(cs, "townyplus.tag")) return false;
            townTags.remove(townTags.get(vars[1]));
            save();
            ((Player)cs).sendMessage("Tag removed");
            return true;
        }        
        if ("get".equals(vars[0])) {
            if (vars.length<2) return false;
            ((Player)cs).sendMessage("Town Tag: "+townTags.getProperty(vars[1]));
            return true;
        }
        Player player = MC.server.getPlayer(vars[1]);
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
            if (vars.length<3) return false;
            if (! HPerm.has(cs, "townyplus.tag")) return false;
            HPerm.handler.addUserInfo("world", MC.server.getPlayer(vars[1]).getName(), "prefix", vars[2]);
            HPerm.handler.addUserInfo("world", MC.server.getPlayer(vars[1]).getName(), "customprefix", vars[2]);
            ((Player)cs).sendMessage("Custom tag made for "+vars[1]);
            return true;
        }        
        return false;
    }
    
    public static boolean load(File folder) {
        Tags.folder = folder;
        folder.mkdirs();
        File tagFile = new File(folder.getAbsolutePath()+File.separator+"towntags.dat");
        if (! tagFile.exists()) return true;
        FileInputStream stream;
        try {
            stream = new FileInputStream(tagFile);
            townTags.load(stream);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
    
    public static void save() {
        if (folder == null) return;
        File tagFile = new File(folder.getAbsolutePath()+File.separator+"towntags.dat");
        try {
            folder.mkdirs();
            tagFile.createNewFile();
            FileOutputStream stream = new FileOutputStream(tagFile);
            townTags.store(stream, "Town Tags");
            stream.close();
        } catch (IOException ex) {
            return;
        }
    }

    public static void update(Player player) {
        if (player == null) return;
        if (HPerm.handler == null) return;
        if (! HTowny.hooked) return;
        String townTag = "";
        //From Town
        Town town = HTowny.getTown(player.getName());
        if (town != null) {
            MC.log("**"+town.getName());
            if (townTags.containsKey(town.getName())) {
                townTag = townTags.getProperty(town.getName());
                MC.log("**"+townTag);
            }
        }
        String prefix = "";
        User user = HPerm.handler.getUserObject("world", player.getName());
        LinkedHashSet<Entry> parents = user.getParents();
        if (parents != null) {
            Iterator<Entry> iter = parents.iterator();
            while (iter.hasNext()) {
                Entry entry = iter.next();
                if (entry.getPrefix() != null) {
                    if ((! townTags.contains(entry.getName())) && (! entry.getName().equals(player.getName()))) {
                        prefix += entry.getPrefix();
                    }
                }
            }
        }
        HPerm.setInfo(player,"prefix", prefix+townTag);
    }
}
