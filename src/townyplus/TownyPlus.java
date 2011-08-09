package townyplus;

import townyplus.hooks.HPerm;
import ca.xshade.bukkit.towny.object.Resident;
import ca.xshade.bukkit.towny.object.TownBlock;
import ca.xshade.bukkit.towny.object.WorldCoord;
import townyplus.hooks.HGuard;
import townyplus.hooks.HTowny;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.java.JavaPlugin;
import townyplus.listener.TPBlockListener;
import townyplus.listener.TPEntityListener;
import townyplus.listener.TPPlayerListener;

public class TownyPlus extends JavaPlugin {
    static final String[] tc = {"tc"};
    static final String[] nc = {"nc"};
    public static Set<Material> ProtectedBlocks = new HashSet<Material>();
    public static Set<Material> NearTownBlocks = new HashSet<Material>();
    public static Set<DamageCause> AllowDamageTypeInTown= new HashSet<DamageCause>();
    public static WorldCoord worldCoord;

    public void onEnable() {
        //Load Subroutines & Hooks
        MC.load(this);
        if (! HTowny.load()) {
            MC.log("Unable to load towny.");
        }
        HPerm.load(); HGuard.load();
        Tags.load(getDataFolder());
        TPPlayerListener playerListener = new TPPlayerListener();
        TPBlockListener blockListener = new TPBlockListener();
        TPEntityListener entityListener = new TPEntityListener();
        //blockLIstener
        getCommand("tag").setExecutor(new Tags());
        getCommand("tc").setExecutor(this);
        getCommand("nc").setExecutor(this);
        getCommand("live").setExecutor(new CmdList());
        //WorldGuard
        MC.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.High);
        MC.registerEvent(Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Lowest);
        MC.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Highest);
        MC.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Highest);
        MC.registerEvent(Type.BLOCK_PISTON_EXTEND, blockListener, Priority.Highest);
        MC.registerEvent(Type.BLOCK_PISTON_RETRACT, blockListener, Priority.Highest);
        
        ProtectedBlocks.add(Material.CHEST);
        ProtectedBlocks.add(Material.FURNACE);
        ProtectedBlocks.add(Material.BURNING_FURNACE);
        ProtectedBlocks.add(Material.DISPENSER);
        ProtectedBlocks.add(Material.JUKEBOX);
        
        NearTownBlocks.add(Material.LAVA_BUCKET);
        NearTownBlocks.add(Material.WATER_BUCKET);
        NearTownBlocks.add(Material.LAVA);
        NearTownBlocks.add(Material.WATER);
        
        AllowDamageTypeInTown.add(DamageCause.FALL); 
        AllowDamageTypeInTown.add(DamageCause.SUFFOCATION); 
        AllowDamageTypeInTown.add(DamageCause.VOID); 
        AllowDamageTypeInTown.add(DamageCause.DROWNING); 
        //Material
        MC.log(Level.INFO,"Plugin active.");
    }

    public void onDisable() {
        MC.log(Level.INFO,"Plugin disabled.");
    }

    //Chat Commands
    @Override
    public boolean onCommand(CommandSender sender, Command cmnd, String lable, String[] strings) {
        if (! (sender instanceof Player)) return false;
        Player player = (Player)sender;
        //If already in mode, go back to general chat
        if (HTowny.handler.hasPlayerMode(player, lable)) {
            HTowny.handler.removePlayerMode(player);
            player.sendMessage(ChatColor.AQUA+"You are now using general chat.");
        //If already in mode, go back to general chat
        } else if (lable.equals("tc")) {
            HTowny.handler.setPlayerMode(player, tc);
            player.sendMessage(ChatColor.AQUA+"You are now using town chat.");
        //If already in mode, go back to general chat
        } else if (lable.equals("nc")) {
            HTowny.handler.setPlayerMode(player, nc);
            player.sendMessage(ChatColor.AQUA+"You are now using nation chat.");
        }        
        return true;
    }

    public static boolean canUse(Player player, Block block) {
        if (block == null) return true;
        if (player.isOp()) return true;
        if (!(HTowny.hooked)) return false;
        if (! TownyPlus.ProtectedBlocks.contains(block.getType())) return true;
        //Will throw exception if plot is not part of town.
        TownBlock plot = HTowny.getPlot(block.getLocation());
        if (plot == null) return true; //Wilderness
        Resident playerRes = HTowny.getResident(player.getName());
        return HTowny.canBuild(plot, playerRes);
    }
    
}