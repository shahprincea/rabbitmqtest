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
	
	/**
	 * Set up connection with Rabbit and also declares queue
	 * 
	 * Deprecated as each consumer should create its own channel and connection 
	 * 
	 * @throws IOException
	 * @throws ShutdownSignalException
	 * @throws ConsumerCancelledException
	 * @throws InterruptedException
	 * @deprecated 
	 */
	private void connection() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost(m_host);
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();
	    
	    channel.queueDeclare(m_queue, m_durable, m_exclusive, m_autoDelete, m_arguments);
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
	public void consume(final long numOfMsg, boolean sendAck, long waittime) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException  {
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setAutomaticRecoveryEnabled(true);
		// attempt recovery every 10 seconds
		factory.setNetworkRecoveryInterval(10000);
	    factory.setHost(m_host);
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();
	    
	    channel.queueDeclare(m_queue, m_durable, m_exclusive, m_autoDelete, m_arguments);
  		System.out.println(m_name + " [*] Waiting for messages. To exit press CTRL+C");
		
		QueueingConsumer consumer = new QueueingConsumer(channel);
  	    channel.basicConsume(m_queue, false, consumer);
  	    int counter = 0;
  	    while (counter++ < numOfMsg) {
  	      QueueingConsumer.Delivery delivery = consumer.nextDelivery();
  	      String message = new String(delivery.getBody());
  	      System.out.println(m_name +" [x] Received '" + message + "'");
  	      Thread.sleep(waittime);  	      
  	      if(sendAck)
  	    	  channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
  	      else 
  	    	  channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
  	    }
  	    
  	    channel.close();
  	    connection.close();
	}
	
	/**
	 * Consume as many messages as you can. Each msg takes waittime time to do the task
	 * @param sendAck
	 * @param waittime
	 * @throws IOException
	 * @throws ShutdownSignalException
	 * @throws ConsumerCancelledException
	 * @throws InterruptedException
	 */
	public void consumeAtWill(boolean sendAck, long waittime) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException  {
		consumeAtWill(sendAck, waittime, false);
	}
	
	/**
	 * Consume as many messages as you can. Each msg takes waittime time to do the task
	 * @param sendAck
	 * @param waittime
	 * @throws IOException
	 * @throws ShutdownSignalException
	 * @throws ConsumerCancelledException
	 * @throws InterruptedException
	 */
	public void consumeAtWill(boolean sendAck, long waittime, boolean showDone) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException  {
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setAutomaticRecoveryEnabled(true);
		// attempt recovery every 10 seconds
		factory.setNetworkRecoveryInterval(10000);
	    factory.setHost(m_host);
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();
		
		QueueingConsumer consumer = new QueueingConsumer(channel);
  	    channel.basicConsume(m_queue, false, consumer);
  	    while (true) {
  	      QueueingConsumer.Delivery delivery = consumer.nextDelivery();
  	      String message = new String(delivery.getBody());
  	      System.out.println(m_name +" [x] Received '" + message + "'");
  	      Thread.sleep(waittime); 
  	      if(showDone) 
  	    	  System.out.println(m_name + " done working on '" + message + "'");
  	      if(sendAck)
  	    	  channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
  	      else 
  	    	  channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
  	    }
	}
	
	/**
	 * Consume 1 msg and dies after waiting for pre define time in mill Sec
	 * 
	 * NOTE: we are closing connection (simulating breaking of connection)
	 * 
	 * @param waittime
	 * @throws IOException
	 * @throws ShutdownSignalException
	 * @throws ConsumerCancelledException
	 * @throws InterruptedException
	 */
	public void consumeAndDie(long waittime) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException  {
		
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost(m_host);
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();
		
		QueueingConsumer consumer = new QueueingConsumer(channel);
  	    channel.basicConsume(m_queue, false, consumer);
  	    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
  	    String message = new String(delivery.getBody());
  	    System.out.println(m_name +" [x] Received '" + message + "'");
  	    Thread.sleep(waittime);
  	    System.out.println(m_name +" [#] is dead");
  	    channel.close();
	    connection.close();
  	    Thread.currentThread().stop();
  	    
  	    
	}
	
	/**
	 * overloadded methoded with no waittime for individual task
	 * 
	 * @param sendAck
	 * @throws IOException
	 * @throws ShutdownSignalException
	 * @throws ConsumerCancelledException
	 * @throws InterruptedException
	 */
	public void consumeAtWill(boolean sendAck) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException  {
		consumeAtWill(sendAck, 1);
	}
	
	/**
	 * Consumes messages and default time each msg takes is 2 sec
	 * 
	 * @param numOfMsg
	 * @param sendAck
	 * @throws ShutdownSignalException
	 * @throws ConsumerCancelledException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void consume(final long numOfMsg, boolean sendAck) throws ShutdownSignalException, ConsumerCancelledException, IOException, InterruptedException {
		consume(numOfMsg, sendAck, 1);
	}
	public static void main(String[] args) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		SimpleConsumer consumer = new SimpleConsumer("Default_Consume");
		consumer.consume(Long.MAX_VALUE, true);
	}
}

