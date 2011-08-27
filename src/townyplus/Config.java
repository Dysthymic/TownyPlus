package townyplus;

import java.util.HashMap;
import java.util.Map;
import townyplus.base.YML;

public class Config extends YML {
	public static Map<String,Object> town_upkeep = new HashMap<String,Object>();

	@Override
	public void loadDefault() {
		town_upkeep.put("pay", true);
		town_upkeep.put("per_person",40.0);
		town_upkeep.put("per_town",0.0);
		town_upkeep.put("minimum", 250.0);
		town_upkeep.put("maximum", 1000.0);
		town_upkeep.put("plot_loss_refund",250.0);
	}
	
			
	
}
