package base;

import java.io.IOException;
import java.io.Serializable;

/**
 * Send is the one who publishes message on the channel
 * @author Prince
 *
 */
public interface Sender {
	
	/**
	 * Given message it will publish to default queue, exchange, rountingKey
	 * @param object
	 * @throws IOException 
	 */
	public void publishMsg(Serializable object) throws IOException;
	
	/**
	 * Given message it will publish to default queue using Exchange and RountingKey
	 * @param object
	 * @throws IOException 
	 */
	public void publishMsg(Serializable object, String exchange, String routingKey) throws IOException;
	
}
