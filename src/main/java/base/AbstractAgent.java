/**
 * 
 */
package base;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Abstract implementation of Agent
 * 
 * @author Prince
 *
 */
public class AbstractAgent implements Agent {

	protected Channel channel = null;
	protected Connection connection = null;
	protected String agentName = null;
	protected final static Logger s_logger = Logger.getLogger(AbstractAgent.class.getName());
	
	public AbstractAgent(String agentName) {
		this.agentName = agentName;
	}
	
	/* (non-Javadoc)
	 * @see base.Agent#setupConnection()
	 */
	public void setupConnection() throws IOException {
		
		//Creating connection factory 
		ConnectionFactory factory = new ConnectionFactory();
		
		//hostName of your rabbitMQ server
		factory.setHost(App.getProperty("host"));
		
		//getting a connection
		connection = factory.newConnection();
		
		//Creating a channel
		channel = connection.createChannel();
		
		//Declaring a queue from config.properties
		String queue = App.getProperty("queuename");
		boolean durable = Boolean.parseBoolean(App.getProperty("isdurable"));
		boolean exclusive = Boolean.parseBoolean(App.getProperty("isexclusive"));
		boolean autoDelete = Boolean.parseBoolean(App.getProperty("autodelete"));
		Map<String, Object> arguments = null;
		
		channel.queueDeclare(queue, durable, exclusive, autoDelete, arguments);
		
		s_logger.log(Level.INFO, "Declaring Queue {0} Durable {1} Exclusive {2} autoDelete {3}",  new Object[] {queue, durable, exclusive, autoDelete});

	}
	
	/* (non-Javadoc)
	 * @see base.Agent#close()
	 */
	public void close() throws IOException {
		if(channel != null) 
			channel.close();
		if(connection != null)
			connection.close();
	}

}
