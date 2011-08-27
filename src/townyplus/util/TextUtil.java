//Text Utils designed by spathwalker.
//v1.0.0
package townyplus.util;

import org.bukkit.ChatColor;

public class TextUtil {

    public static String ToString(String[] a, String separator) {
        return ToString(a, separator, 0);
    }

    public static String ToString(String[] a, String seperator, int start) {
        StringBuffer result = new StringBuffer();
        if (a.length > start) {
            result.append(a[start]);
            for (int i=start+1; i<a.length; i++) {
                if (!a[i - 1].equals("")) result.append(seperator);
                result.append(a[i]);
            }
        }
        return result.toString();
    }

    
    public static String ToString(Object[] a, String seperator, int start) {
        StringBuffer result = new StringBuffer();
        if (a.length > start) {
            result.append(a[start]);
            for (int i=start+1; i<a.length; i++) {
                if (!a[i - 1].equals("")) result.append(seperator);
                result.append(a[i]);
            }
        }
        return result.toString();
    }    

    public static String cut(String a, int size) {
        if (a.length() <= size) return a;
        return a.substring(1, size);
    }

	public static String titleize(String title) {
		String line = ".oOo.__________________________________________________.oOo.";
		int pivot = line.length() / 2;
		String center = ".[ " + ChatColor.YELLOW + title + ChatColor.GOLD + " ].";
		String out = ChatColor.GOLD + line.substring(0, pivot - center.length() / 2);
		out += center + line.substring(pivot + center.length() / 2);
		return out;
	}

}
