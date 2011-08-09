package townyplus.listener;

import ca.xshade.bukkit.towny.NotRegisteredException;
import ca.xshade.bukkit.towny.object.Resident;
import ca.xshade.bukkit.towny.object.Town;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import townyplus.hooks.HTowny;
import townyplus.hooks.HGuard;
import townyplus.hooks.HPerm;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import townyplus.TownyPlus;

public class TPBlockListener extends BlockListener {

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        if (HPerm.has(event.getPlayer(), "townyplus.piston")) return;
        Player target = event.getPlayer();
        Block block = event.getBlock();
        //Check if not on disallowed list
        if (!(TownyPlus.NearTownBlocks.contains(block.getType()))) return;
        //Check if in town
        if (HTowny.getTown(block.getLocation()) != null) return;
        //Get My Town
        Town myTown = HTowny.getTown(event.getPlayer().getName());
        boolean canDo = true;
        if (! canPlace(event.getPlayer(),myTown,block.getRelative(+12,0,0))) canDo = false;
        if (! canPlace(event.getPlayer(),myTown,block.getRelative(-12,0,0))) canDo = false;
        if (! canPlace(event.getPlayer(),myTown,block.getRelative(0,0,+12))) canDo = false;
        if (! canPlace(event.getPlayer(),myTown,block.getRelative(0,0,-12))) canDo = false;
        if (! canDo) {
            target.sendMessage("You can not place "+block.getType().name()+" this close to town.");
            event.setCancelled(true);
        }
    }

    public boolean canPlace(Player target,Town targetTown, Block block) {
        //Get My Town
        Boolean result = true;
        //If Town exists...
        Town town = HTowny.getTown(block.getLocation());
        if (town != null) {
            if (town != targetTown) result = false;
        }
        if (! HGuard.canBuild(target, block.getLocation())) result = false;
        return result;
    }

    @Override
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        //Up and down always ok
        if ((event.getDirection() == BlockFace.UP) || (event.getDirection() == BlockFace.DOWN)) return;
        //if no blocks moved ok
        if (event.getBlocks().isEmpty()) return;
        //Grab locations, check
        Location piston = event.getBlock().getLocation();
        Location target = event.getBlocks().get(event.getBlocks().size()-1).getRelative(event.getDirection()).getLocation();
        if (! canActivate(piston, target)) event.setCancelled(true);
    }

    @Override
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        //Up and down always ok
        if ((event.getDirection() == BlockFace.UP) || (event.getDirection() == BlockFace.DOWN)) return;
        //Can always retract for non-sticky pistons
        if (event.getBlock().getType() == Material.PISTON_BASE) return;
        Location piston = event.getBlock().getLocation();
        Location target = event.getRetractLocation();
        if (! canActivate(piston, target)) event.setCancelled(true);
    }

    public boolean canActivate(Location piston, Location target) {
        //Grab Target Info
        Town targetTown = HTowny.getTown(target);
        ApplicableRegionSet targetRegions = HGuard.guardAreas(target);
        //If target zone in wild, always ok.
        if ((targetRegions.size() == 0) && (targetTown == null)) return true;
        //Grab Piston info
        Town pistonTown = HTowny.getTown(piston);
        ApplicableRegionSet pistonRegions = HGuard.guardAreas(piston);
        //Worldguard: If more regions in target area, cancel
        if (targetRegions.size() > pistonRegions.size()) return false;
        //Not Ok if going into or out of town
        if (targetTown != pistonTown) return false;
        //OK if same Plot
        if (HTowny.getPlot(piston) == HTowny.getPlot(target)) return true;
        //Or if both plot unowned
        if ((! HTowny.getPlot(target).hasResident()) && (! HTowny.getPlot(piston).hasResident())) return true;
        //If both plots have the same owner, ok.
        try {
            Resident targetResident = HTowny.getPlot(target).getResident();
            Resident pistonResident = HTowny.getPlot(piston).getResident();
            if (targetResident == pistonResident) return true;
        } catch (NotRegisteredException ex) {
        }
        return false;
    }

    
}
