package base;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;

import org.apache.commons.lang3.SerializationUtils;

import com.rabbitmq.client.MessageProperties;

/**
 * Producer which publish message to Queue/Exchange
 * @author Prince
 *
 */
public class Producer extends AbstractAgent {

	public Producer(String producerName) throws IOException {
		super(producerName);
		s_logger.log(Level.INFO, "Producer {0} Alive", producerName);
		super.setupConnection();
		s_logger.log(Level.INFO, "Connection Setup", producerName);
	}
	
	/**
	 * Publishes this message directly to Queue
	 * It does not use any exchange
	 */
	public void publishMsg(Serializable object) throws IOException {
		channel.basicPublish("", App.getProperty("queuename"), MessageProperties.MINIMAL_PERSISTENT_BASIC, SerializationUtils.serialize(object));
	}
	
	/**
	 * Publishes messages to Exchange using defined rountingKey
	 */
	public void publishMsg(Serializable object, String exchange, String routingKey) throws IOException {
		channel.basicPublish(exchange, routingKey, MessageProperties.MINIMAL_PERSISTENT_BASIC, SerializationUtils.serialize(object));
	}
	
	
}
