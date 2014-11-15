package base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads config.properites file and exposes Properties for use.
 * 
 * @author Prince
 *
 */
public class App {
	
	private static final String PROP_FILE = "config.properties";
	
	/**
	 * Reads config.properties file and retrieve its value
	 * 
	 * @param key
	 * @return
	 * @throws IOException 
	 */
	public static String getProperty(String key) {
		InputStream input = null;
		Properties prop = null;
		try {
			input = new FileInputStream(PROP_FILE);
			prop = new Properties();
			prop.load(input);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
			if(input != null) {
				try {
					input.close();
				} catch(IOException ioEx) {
					ioEx.printStackTrace();
				}
			}
		}
		
		return prop.getProperty(key);
		
	}
	

}
