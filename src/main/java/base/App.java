package base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Reads config.properites file and exposes Properties for use.
 * 
 * @author Prince
 *
 */
public class App {
	
	private static final String PROP_FILE = "config.properties";
	private static final Logger s_logger = Logger.getLogger(App.class.getName());
	
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
	
	/**
	 * Will stop RabbitMQ from running using processBuilder
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void stopRabbit() throws IOException, InterruptedException {
		List<String> cmds = new ArrayList<String>();
		cmds.add("/bin/sh");
		cmds.add("-c");
		cmds.add("echo root | sudo -S rabbitmqctl stop");
		
		Process process = new ProcessBuilder(cmds).start();
		process.waitFor();
		s_logger.info("RabbitMQ is stopped");
	}
	
	/**
	 * Will start RabbitMQ from running using processBuilder
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void startRabbit() throws IOException, InterruptedException {
		List<String> cmds = new ArrayList<String>();
		cmds.add("/bin/sh");
		cmds.add("-c");
		cmds.add("echo root | sudo -S rabbitmq-server start");
		
		new ProcessBuilder(cmds).start();
		s_logger.info("Waiting for 2 sec cmd to be completed");
		Thread.sleep(2000);
		s_logger.info("RabbitMQ started");
	}
	
	/**
	 * Restarts RabbitMQ after delaying for arg in seconds
	 * @param inSeconds
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void restartRabbit(long inSeconds) throws IOException, InterruptedException {

		App.stopRabbit();
		TimeUnit.SECONDS.sleep(inSeconds);
		App.startRabbit();
		
	}
	
	public static boolean isRabbitAlive() {
		Socket socket =null;
		boolean reachable = false;
		try {
			socket = new Socket(App.getProperty("host"), Integer.valueOf(App.getProperty("port")));
			reachable = true;
		} catch (Exception e) {
			
		} finally {
			if(socket!=null) 
				try {
				socket.close();
				} catch(IOException ioEx){}
		}
		
		return reachable;
	}

}
