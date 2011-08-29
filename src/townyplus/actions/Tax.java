package townyplus.actions;

import ca.xshade.bukkit.towny.IConomyException;
import ca.xshade.bukkit.towny.NotRegisteredException;
import ca.xshade.bukkit.towny.TownyException;
import ca.xshade.bukkit.towny.object.Resident;
import ca.xshade.bukkit.towny.object.Town;
import ca.xshade.bukkit.towny.object.TownBlock;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import townyplus.Config;
import townyplus.Data;
import townyplus.Main;
import townyplus.base.Core;
import townyplus.hooks.HPerm;
import townyplus.hooks.HTowny;
import townyplus.util.BasicFormatter;
import townyplus.util.TextUtil;
import townyplus.util.Time;

public class Tax implements CommandExecutor {
	private static final Logger logger = Logger.getLogger("townyplus");

	public boolean onCommand(CommandSender cs, Command cmnd, String lable, String[] vars) {
		if (vars.length == 0) return getInfo(cs,vars);
		vars[0] = vars[0].toLowerCase();
		if ("? help".contains(vars[0])) return getHelp(cs);
		if ("info town".contains(vars[0])) return getInfo(cs,vars);
		if (("pay".contains(vars[0])) && (cs instanceof Player)) return payPastDue((Player)cs);
		if ("past pastdue due".contains(vars[0])) {
			if (cs instanceof Player) {
				Resident res = HTowny.getResident((Player)cs);
				if (isTownAdmin(res)) {
					try {
						getPastDue(res.getTown(),(Player)cs);
						return true;
					} catch (NotRegisteredException ex) {
					}
				}
				Core.send(cs,"&4You do not have permission for this command.");
				return true;
			}
			Core.send(cs,"&4You do not have permission for this command.");
			return true;
		}
		if ("collect get".contains(vars[0])) {
			if (cs instanceof Player) {
				Resident res = HTowny.getResident((Player)cs);
				if (isTownAdmin(res)) {
					try {
						collectPastDue(res.getTown(),(Player)cs);
						return true;
					} catch (NotRegisteredException ex) {
					}
				}
				Core.send(cs,"&4You do not have permission for this command.");
				return true;
			}
			Core.send(cs,"&4You do not have permission for this command.");
			return true;
		}
		if (! HPerm.has(cs, "townyplus.tax.admin")) {
			Core.send(cs,"&4You do not have permission for this command.");
			return true;
		}
		if ("now".contains(vars[0])) {
			Core.send(cs,"&2Taking taxes out now for all towns.");
			taxNow();
			return true;
		}
		if ("reload".contains(vars[0])) {
			Core.send(cs,"&2Tax system reloaded.");
			Main.config.save();
			return true;
		}
		if ("townnow".contains(vars[0])) {
			if (vars.length<2) {
				Core.send(cs,"&4Town name must be listed to tax.");
				return true;
			}
			Town town = HTowny.getTown(vars[1]);
			if (town == null) {
				Core.send(cs,"&4Invalid Town");
				return true;
			}
			taxTown(town);
			return true;
		}		
		Core.send(cs,"&4Unknown Command");
		return true;
	}

	public static boolean getHelp(CommandSender cs) {
		Core.send(cs,TextUtil.titleize("Tax Help"));
		Core.send(cs,"&2Taxes and town upkeep are collected each day (24 hours), ");
		Core.send(cs,"&2along with any past due amounts for the town you are in.");
		Core.send(cs,"&a/tax &2Shows your current tax info and town.");
		Core.send(cs,"&a/tax info (town) &2Shows tax info for another town.");
		Core.send(cs,"&a/tax pay &2Pays any back taxes owed. See &a/tax &2for a full list.");
		Core.send(cs,"&7(Mayor/Assistant Commands):");
		Core.send(cs,"&a/town set (taxes/plottax) $ &2Sets taxes per resident or plot.");
		Core.send(cs,"&a/tax due &2Lists everyone in your town who owes taxes.");
		Core.send(cs,"&a/tax collect &2Attempts to collect on all past due amounts.");
		if (HPerm.has(cs, "townyplus.tax.admin")) {
			Core.send(cs,"&7(Admin Only): &a/tax now &2 Withdraws taxes now.");
			Core.send(cs,"&7(Admin Only): &a/tax reload &2 reloads config/tax data.");
		}
		return true;
	}
	
	public static boolean getInfo(CommandSender cs,String[] vars) {
		Core.send(cs,TextUtil.titleize("Taxes"));
		if (cs instanceof Player) {
			Player player = (Player)cs;
			Core.send(cs,"&2My Taxes per Day: &a$"+getTax(player)+"  &2Next withdraw in &a"+Time.countDown(Data.lastTax-Time.now()+Time.day));
			if (isPastDue((Player)cs)) {
				Core.send(cs,"  &cPast Due: &a"+pastDueAmounts(player));
			}	
		}
		if (vars.length<2) {
			if (!(cs instanceof Player)) {
				Core.send(cs,"&2You must list a town name for more info.");
				return true;
			}
			Town town = HTowny.getTown((Player)cs);
			if (town == null) {
				Core.send(cs,"&2For information on a town, use &c/tax info (town)");
				return true;
			}
			getTownInfo(cs,town);
			return true;
		}
		Town town = HTowny.getTown(vars[1]);
		if (town == null) {
			Core.send(cs,"&2Invalid town name.");
			return true;
		}
		getTownInfo(cs,town);
		return true;
	}
	
	public static void getTownInfo(CommandSender cs, Town town) {
		if (town == null) return;
		//Get Taxes
		double income = getIncome(town);
		double upkeep = getUpkeep(town);
		double baseUpkeep = getBaseUpkeep(town);
		if (cs instanceof Player) {
			Player player = (Player)cs;
			Resident res = HTowny.getResident(player);
		} else {
			Core.send(cs,"&2Next withdraw in &a"+Time.countDown(Data.lastTax-Time.now()+Time.day));
		}
		Core.send(cs,"&2Info for &a"+town.getName()+"&2:");
		Core.send(cs,"&2  Resident Tax: &a$"+town.getTaxes()+"  &2Plot Tax: &a$"+town.getPlotTax());
		Core.send(cs,"&2  Balance: &a$"+getBalance(town));
		Core.send(cs,"&2  Income:  &a$"+income+
			((countPastDue(town)>0) ? "     &2Players past due: &a"+countPastDue(town) :""));
		Core.send(cs,"&2  Upkeep:  &a$"+upkeep+
			((upkeep != baseUpkeep) ? "&7 Before Limit: "+baseUpkeep : ""));
		Core.send(cs,"&2  Profit:   &a$"+(income-upkeep));
	}	
	
	public static int getTax(Player player) {
		Resident res = HTowny.getResident(player);
		Town town = HTowny.getTown(player);
		if (town == null) return 0;
		if (isTownAdmin(res)) return 0;
		return res.getTownBlocks().size() * town.getPlotTax() + town.getTaxes();
	}

	public static int getTax(Resident res) {
		try {
			Town town = res.getTown();
			if (isTownAdmin(res)) return 0;
			return res.getTownBlocks().size() * town.getPlotTax() + town.getTaxes();
		} catch (NotRegisteredException ex) {
			return 0;
		}
	}
	
	public static double getBalance(Town town) {
		try {
			return town.getHoldingBalance();
		} catch (IConomyException ex) {
			return 0.0;
		}
	}
	
	public static double getUpkeep(Town town) {
		double result = town.getResidents().size() * (Double) Config.town_upkeep.get("per_person") + (Double)Config.town_upkeep.get("per_town");
		if (result < (Double)Config.town_upkeep.get("minimum")) return (Double)Config.town_upkeep.get("minimum");
		if ((Double)Config.town_upkeep.get("maximum") > 0) {
			if (result > (Double)Config.town_upkeep.get("maximum")) return (Double)Config.town_upkeep.get("maximum");
		}
		return result;
	}
	
	public static double getBaseUpkeep(Town town) {
		return town.getResidents().size() * (Double) Config.town_upkeep.get("per_person") + (Double)Config.town_upkeep.get("per_town");
	}	
	
	public static double getIncome(Town town) {
		double result = 0;
		for (Resident res:town.getResidents()) {
			if (isTownAdmin(res)) continue;
			if (isPastDue(town.getName()+" "+res.getName())) continue;
			result += res.getTownBlocks().size() * town.getPlotTax() + town.getTaxes();
		}
		return result;
	}
	
	public static boolean taxNow() {
		//Set Up Logger
		if ((Boolean)Config.town_upkeep.get("pay") == false) {
			logger.log(Level.INFO,"\n\nTaxes not applied on "+Time.toStr(System.currentTimeMillis())+": Taxes turned off.");
			return true;
		}
		Core.instance.getServer().broadcastMessage(ChatColor.DARK_GREEN+"[Broadcast] Taxes taken out.");
		Data.lastTax = System.currentTimeMillis();
		Data.saveAll();
		logger.log(Level.INFO,"\n\nTaxes applied on "+Time.toStr(System.currentTimeMillis()));
		Iterator<Town> iter = HTowny.getUniverse().getTowns().iterator();
		while (iter.hasNext()) {
			taxTown(iter.next());
		}
		return true;
	}
	
	public static void taxTown(Town town) {
		String date = Time.getDate(System.currentTimeMillis());
		logger.log(Level.INFO,date+" Town: "+town.getName());
		logger.log(Level.INFO,"  Balance: "+getBalance(town));
		logger.log(Level.INFO,"  Taxes Extimate: $"+getIncome(town));
		//Taxes
		residents:
		for (Resident res:town.getResidents()) {
			//Upkeep Per Residents, non-town Admin
			if (isTownAdmin(res)) continue residents;
			String id = town.getName()+" "+res.getName();
			int tax = getTax(res);
			try {
				double due = getPastDue(id) + getTax(res);
				double balance = res.getHoldingBalance();
				if (due == 0) continue residents;
				//If person doesn't have enough
				if (due > balance) {
					//Pay what you can, past due amount for rest
					setPastDue(id, due-balance);
					logger.log(Level.INFO,"    ! "+res.getName()+" is past due $"+(due-balance));
					Main.log("");
					due = balance;
				} else {
					setPastDue(id, 0.0);
				}
				logger.log(Level.INFO,"    "+res.getName()+" paid $"+(due));
				res.pay(due);
				town.collect(due);
			} catch (IConomyException ex) {
				Core.log(Level.SEVERE,"Error when taxing "+res.getName()+" from town "+town.getName());
				logger.log(Level.SEVERE,"Error with taxing "+res.getName()+" from town "+town.getName(),ex);
			}
		}
		Main.data.save();
		//Upkeep
		try {
			double balance = town.getHoldingBalance();
			double upkeep = getUpkeep(town);
			//Pay if can
			if (town.canPayFromHoldings(upkeep)) {
				town.pay(upkeep);
				logger.log(Level.INFO,"  Upkeep $"+upkeep+" Paid.");
				logger.log(Level.INFO,"  New Balanace: $"+getBalance(town));
				return;
			}
			Core.instance.getServer().broadcastMessage(ChatColor.RED+"[Broadcast] "+town.getName()+" is falling apart.");
			double refund = (Double)Config.town_upkeep.get("plot_loss_refund");
			//Take away unowned plots first
			logger.log(Level.WARNING,"  ! Unable to pay upkeep $"+upkeep+"");
			logger.log(Level.INFO,"  Refund per block at $"+refund);
			logger.log(Level.WARNING,"  ! Removing unowned blocks first...");
			
			Iterator<TownBlock> iterTB = town.getTownBlocks().iterator();
			while (iterTB.hasNext()) {
				TownBlock block = iterTB.next();
				if (block.hasResident()) continue;
				try {
					if (block == town.getHomeBlock()) continue;
				} catch (TownyException ex) {
					logger.log(Level.SEVERE, "  Error removing unowned blocks", ex);
				}
				HTowny.handler.getTownyUniverse().removeTownBlock(block);
				town.collect(refund);
				logger.log(Level.WARNING,"    Lost block "+block.getX()+","+block.getZ());
				if (town.canPayFromHoldings(upkeep)) {
					town.pay(upkeep);
					logger.log(Level.INFO,"  Upkeep $"+upkeep+" Paid.");
					logger.log(Level.INFO,"  New Balanace: $"+getBalance(town));
					return;
				}
			}
			//Take away owned blocks
			logger.log(Level.WARNING,"  ! Unowned blocks not enough. Removing owned blocks..");
			Iterator<TownBlock> iterTB2 = town.getTownBlocks().iterator();
			while (iterTB2.hasNext()) {
				TownBlock block = iterTB2.next();
				try {
					if (block == town.getHomeBlock()) continue;
				} catch (TownyException ex) {
					logger.log(Level.SEVERE, "  Error removing owned blocks", ex);
				}
				HTowny.handler.getTownyUniverse().removeTownBlock(block);
				town.collect(refund);
				logger.log(Level.WARNING,"  Lost block "+block.getX()+","+block.getZ());
				if (town.canPayFromHoldings(upkeep)) {
					town.pay(upkeep);
					logger.log(Level.INFO,"  Upkeep $"+upkeep+" Paid.");
					logger.log(Level.INFO,"  New Balanace: $"+getBalance(town));
					return;
				}
			}
			//Break Apart Town
			Core.instance.getServer().broadcastMessage(ChatColor.RED+"[Broadcast] "+town.getName()+" has fallen into ruin.");	
			HTowny.handler.getTownyUniverse().removeTown(town);
			removePastDue(town);
			logger.log(Level.WARNING,"  ! Town Fell into Ruin");
		} catch (IConomyException ex) {
			Core.log(Level.SEVERE,"Error with upkeep for "+town.getName());
			logger.log(Level.SEVERE,"Error with upkeep for "+town.getName(),ex);
		}
	}

	
	public static boolean isPastDue(String id) {
		return Data.pastDue.containsKey(id);
	}
	
	public static boolean isPastDue(Player player) {
		for (String key: Data.pastDue.keySet()) {
			if (key.endsWith(" "+player.getName())) return true;
		}
		return false;
	}
	
	public static int countPastDue(Town town) {
		int result = 0;
		for (String key: Data.pastDue.keySet()) {
			if (key.startsWith(town.getName()+" ")) {
				result++;
			}
		}	
		return result;
	}
	
	public static void getPastDue(Town town, Player who) {
		Core.send(who,"&2Players Past Due for "+town.getName()+":");
		for (String key: Data.pastDue.keySet()) {
			if (key.startsWith(town.getName()+" ")) {
				Core.send(who,"  &2"+key.split(" ")[1]+" &a$"+Data.pastDue.get(key));
			}
		}	
	}	
	
	public static void collectPastDue(Town town, Player who) {
		Core.send(who,"&2Collecting Past Due for "+town.getName()+":");
		Iterator<String> keyIter = Data.pastDue.keySet().iterator();
		while (keyIter.hasNext()) {
			String key = keyIter.next();
			if (key.startsWith(town.getName()+" ")) {
				Resident res = HTowny.getResident(key.split(" ")[1]);
				Double amount = Data.pastDue.get(key);
				try {
					if (res.canPayFromHoldings(amount)) {
						res.pay(amount);
						town.collect(amount);
						Core.send(who,"  &2"+res.getName()+" paid $"+amount);
						Data.pastDue.remove(key);
					} else {
						Core.send(who,"  &a"+res.getName()+" is unable to pay $"+amount);
					}
				} catch (IConomyException ex) {
					logger.log(Level.SEVERE,"Town "+town.getName()+" attempting to collect from "+res.getName()+" error:",ex);
				}
			}
		}
		Main.data.save();
	}		
	
	public static void removePastDue(Town town) {
		Iterator<String> iter = Data.pastDue.keySet().iterator();
		while (iter.hasNext()) {
			String entry = iter.next();
			if (entry.startsWith(town.getName()+" ")) {
				Data.pastDue.remove(entry);
			}
		}
		Main.data.save();
	}
	
	public static boolean payPastDue(Player player) {
		Iterator<Entry<String, Double>> iter = Data.pastDue.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Double> entry = iter.next();
			if (entry.getKey().endsWith(" "+player.getName())) {
				try {
					if (HTowny.getResident(player).canPayFromHoldings(entry.getValue())) {
						logger.log(Level.INFO, player.getName()+" paid past due amt $"+entry.getValue()+" to "+entry.getKey().split(" ")[0]);
						Core.send(player, "&2Paid past due amt $"+entry.getValue()+" to "+entry.getKey().split(" ")[0]);
						HTowny.getResident(player).collect(entry.getValue());
						HTowny.getTown(entry.getKey().split(" ")[1]).pay(entry.getValue());
						Data.pastDue.remove(entry.getKey());
					} else {
						Core.send(player, "&2Unable to pay past due amt $"+entry.getValue()+" to "+entry.getKey().split(" ")[0]);
					}
				} catch (IConomyException ex) {
				}
			}
		}
		Main.data.save();
		return true;
	}	
	
	public static String pastDueAmounts(Player player) {
		String result = "";
		for (Entry<String,Double> entry: Data.pastDue.entrySet()) {
			if (entry.getKey().endsWith(" "+player.getName())) {
				result += entry.getKey().split(" ")[0] + ":$"+entry.getValue()+" ";
			}
		}
		return result;
		
	}
	
	public static boolean isTownAdmin(Resident res) {
		if (res == null) return false;
		if (res.isMayor()) return true;
		try {
			if (res.getTown().getAssistants().contains(res)) return true;
		} catch (NotRegisteredException ex) {
		}
		return false;
	}	
	
	public static double getPastDue(String id) {
		if (! Data.pastDue.containsKey(id)) return 0;
		return Data.pastDue.get(id);
	}

	public static void setPastDue(String id, double amount) {
		if (amount <= 0) {
			Data.pastDue.remove(id);
			return;
		}
		Data.pastDue.put(id, amount);
	}
	
	public static void load() {
		logger.setUseParentHandlers(false);
		if (logger.getHandlers().length != 0) return;
		try {
			FileHandler handler = new FileHandler(Core.instance.getDataFolder()+ File.separator+"taxes.log", true);
			handler.setFormatter(new BasicFormatter());
			logger.addHandler(handler);
		} catch (IOException ex) {
			Core.log(Level.SEVERE, "Unable to log taxes.");
		} catch (SecurityException ex) {
			Core.log(Level.SEVERE, "Unable to log taxes.");
		}
		
	}

}
