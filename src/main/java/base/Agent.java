package base;

import java.io.IOException;

/**
 * Agent which setups connection with the queue. 
 * An Agent could be producer or consumer.
 * 
 * @author Prince
 *
 */
public interface Agent {
	
	/**
	 * Set up connection with RabbitMQ server using config.properties
	 * It also declares queue
	 * 
	 * @return true if connection was setup else false
	 */
	public boolean setupConnection();
	
	/**
	 * Close channel and connection. 
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException;
	
}
