package townyplus.listener;

import ca.xshade.bukkit.towny.object.Town;
import java.util.Arrays;
import java.util.List;
import townyplus.hooks.HTowny;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;
import townyplus.Main;

public class TPEntityListener extends EntityListener {
    public static List<DamageCause> AllowDamageTypeInTown = Arrays.asList(
        DamageCause.FALL, DamageCause.SUFFOCATION,
        DamageCause.VOID, DamageCause.DROWNING);

    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player target = (Player) event.getEntity();
        Town town = HTowny.getTown(event.getEntity().getLocation());
        if (town == null) return;
        if (town.isPVP()) return;
        if (AllowDamageTypeInTown.contains(event.getCause())) return;
        event.setCancelled(true);
        return;
    }
    
}
