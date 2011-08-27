package townyplus;

import java.util.HashMap;
import java.util.Map;
import townyplus.base.YML;

public class Data extends YML {
	static public Map<String,Double> pastDue = new HashMap<String,Double>();
	static public Map<String,String> townTags = new HashMap<String,String>();
	static public long lastTax;
	
	@Override
	public void loadDefault() {
	}
	
}
