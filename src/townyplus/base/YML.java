//YML Class, v2.0
package townyplus.base;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public abstract class YML {
	private static final Logger logger = Logger.getLogger("minecraft");
	private static List<YML> instances = new ArrayList<YML>();
	private JavaPlugin plugin;
	private String fileName = getClass().getSimpleName()+".yml";
	private Field[] fields = getClass().getDeclaredFields();
	private Configuration config;

	abstract public void loadDefault();
	
	public Object load(JavaPlugin plugin) {
		instances.add(this);
		this.plugin = plugin;
		File file = new File(plugin.getDataFolder(),fileName);
		config = new Configuration(file);
		if (! file.exists()) {
			logger.log(Level.INFO,fileName+" doesn't exist. Creating new file.");
			loadDefault();
			save();
			return this;
		}
		//Get Config
		config.load();
		for (Field field: fields) {
			Object obj = config.getProperty(field.getName());
			if (obj != null) {
				try {
                    field.set(this, obj);
				} catch (Exception ex) {
					logger.log(Level.WARNING,"Error loading '"+field.getName()+"' in "+fileName+" - Default/Empty value used.");
				}
			} else {
				logger.log(Level.WARNING,"'"+field.getName() +"' doesn't exist in "+fileName+" - Default value used.");
			}
		}
		save();
		return this;
	}

	public void save() {
		for (Field field: this.getClass().getDeclaredFields()) {
			try {
				config.setProperty(field.getName(), field.get(this));
			} catch (Exception ex) {
				logger.log(Level.WARNING, "Error saving to "+fileName);
			}
		}
		config.save();
	}
	
	public static void saveAll() {
		for (YML instance:instances) {
			instance.save();
		}
	}
}
