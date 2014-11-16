package Cases;

import java.io.IOException;
import java.util.Map;

import base.App;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * Simple producer which pushes msg directly to the queue
 * @author Prince
 *
 */
public class SimpleProducer {

	private Channel m_channel = null;
	private Connection m_connection = null;
	private String m_queue = null;
	private boolean m_durable = false;
	private boolean m_exclusive = false;
	private boolean m_autoDelete = false;
	private Map<String, Object> m_arguments = null;
	private String m_host = null;
	private String m_name = null;
	
	public SimpleProducer (String name) {
		//Declaring a queue from config.properties
		m_queue = App.getProperty("queuename");
		m_durable = Boolean.parseBoolean(App.getProperty("isdurable"));
		m_exclusive = Boolean.parseBoolean(App.getProperty("isexclusive"));
		m_autoDelete = Boolean.parseBoolean(App.getProperty("autodelete"));
		m_arguments = null;
		m_host = App.getProperty("host");
		m_name = name;
	}
	
	/**
	 * Set up connection with Rabbit and also declares queue
	 * 
	 * @throws IOException
	 * @throws ShutdownSignalException
	 * @throws ConsumerCancelledException
	 * @throws InterruptedException
	 */
	private void connection() throws IOException {
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost(m_host);
	    m_connection = factory.newConnection();
	    m_channel = m_connection.createChannel();
	    
	    m_channel.queueDeclare(m_queue, m_durable, m_exclusive, m_autoDelete, m_arguments);
  		System.out.println(m_name + " [*] Waiting for messages. To exit press CTRL+C");
	}
	
	
	/**
	 * Produces msg directly to queue
	 * 
	 * @param numOfMsg
	 * @throws IOException
	 */
	public void produce(long numOfMsg) throws IOException {
		
		connection();
		m_channel.queueDeclare(m_queue, m_durable, m_exclusive, m_autoDelete, m_arguments);
  		
  	    for(int i = 1; i <= numOfMsg; i++) {
  	    	String message = "msg_"+i;
	  		m_channel.basicPublish("", m_queue, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
	  	    System.out.println(m_name + " [x] Sent '" + message + "'");
	  	}
  	    m_channel.close();
        m_connection.close();
  	    
	}
	
	public static void main(String[] args) throws IOException {
		SimpleProducer producer = new SimpleProducer("Default_Producer");
		producer.produce(Long.MAX_VALUE);
	}
}
