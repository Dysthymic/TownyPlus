package townyplus.actions;

import townyplus.Data;
import townyplus.base.Core;
import townyplus.util.Time;

public class TaxTimer implements Runnable {

	public void run() {
		if (Data.lastTax+Time.day>Time.now()) return;
		Data.lastTax = Data.lastTax+Time.day;
		//Stops double taxes
		if (Data.lastTax<Time.now()) {
			Data.lastTax = Time.now()+Time.day;
		}
		Tax.taxNow();
	}
	
}
