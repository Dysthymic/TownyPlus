package townyplus.listener;

import ca.xshade.bukkit.towny.object.Town;
import hooks.HTowny;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import townyplus.TownyPlus;

/**
 *
 * @author Pathwalker
 */
public class TPEntityListener extends EntityListener {

    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player target = (Player) event.getEntity();
        Town town = HTowny.getTown(event.getEntity().getLocation());
        if (town == null) return;
        if (town.isPVP()) return;
        if (TownyPlus.AllowDamageTypeInTown.contains(event.getCause())) return;
        event.setCancelled(true);
        return;
    }
    
}
