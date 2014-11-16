package Cases;

import java.io.IOException;
import java.util.Map;

import base.App;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * Simple Consumers which reads msg from the queue
 * @author Prince
 *
 */
public class SimpleConsumer {

	private Channel m_channel = null;
	private Connection m_connection = null;
	private String m_queue = null;
	private boolean m_durable = false;
	private boolean m_exclusive = false;
	private boolean m_autoDelete = false;
	private Map<String, Object> m_arguments = null;
	private String m_host = null;
	private String m_name = null;
	
	
	public SimpleConsumer (String name) {
		//Declaring a queue from config.properties
		m_queue = App.getProperty("queuename");
		m_durable = Boolean.parseBoolean(App.getProperty("isdurable"));
		m_exclusive = Boolean.parseBoolean(App.getProperty("isexclusive"));
		m_autoDelete = Boolean.parseBoolean(App.getProperty("autodelete"));
		m_arguments = null;
		m_host = App.getProperty("host");
		m_name = name;

	}
	
	public static void main(String[] args) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		SimpleConsumer consumer = new SimpleConsumer("Default_Consume");
		consumer.consume(Long.MAX_VALUE);
	}
	
	/**
	 * Set up connection with Rabbit and also declares queue
	 * 
	 * @throws IOException
	 * @throws ShutdownSignalException
	 * @throws ConsumerCancelledException
	 * @throws InterruptedException
	 */
	private void connection() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost(m_host);
	    m_connection = factory.newConnection();
	    m_channel = m_connection.createChannel();
	    
	    m_channel.queueDeclare(m_queue, m_durable, m_exclusive, m_autoDelete, m_arguments);
  		System.out.println(m_name + " [*] Waiting for messages. To exit press CTRL+C");
	}
	
	/**
	 * consumes messages from the queue directly
	 * 
	 * @throws IOException
	 * @throws ShutdownSignalException
	 * @throws ConsumerCancelledException
	 * @throws InterruptedException
	 */
	public void consume(final long numOfMsg) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException  {
		
		connection();
		
		QueueingConsumer consumer = new QueueingConsumer(m_channel);
  	    m_channel.basicConsume(m_queue, false, consumer);
  	    int counter = 0;
  	    while (counter++ < numOfMsg) {
  	      QueueingConsumer.Delivery delivery = consumer.nextDelivery();
  	      String message = new String(delivery.getBody());
  	      System.out.println(m_name +" [x] Received '" + message + "'");
  	      Thread.sleep(2000);
  	      m_channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
  	    }
  	    
  	    m_channel.close();
  	    m_connection.close();
	}
}

