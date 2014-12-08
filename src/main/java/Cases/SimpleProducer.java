package Cases;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import base.App;

import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
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
	
	public SimpleProducer (String name, Map<String, Object> queueProps) {
		//Declaring a queue from config.properties
		m_queue = App.getProperty("queuename");
		m_durable = Boolean.parseBoolean(App.getProperty("isdurable"));
		m_exclusive = Boolean.parseBoolean(App.getProperty("isexclusive"));
		m_autoDelete = Boolean.parseBoolean(App.getProperty("autodelete"));
		m_arguments = queueProps;
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
		factory.setAutomaticRecoveryEnabled(true);
		// attempt recovery every 10 seconds
		factory.setNetworkRecoveryInterval(1000);
	    factory.setHost(m_host);
	    m_connection = factory.newConnection();
	    m_channel = m_connection.createChannel();
	    if(m_arguments != null) {
	    	m_channel.queueDelete(m_queue);
	    }
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
		produce(numOfMsg, 0);
	}
	
	/**
	 * Produces msg directly to queue with defined delayed
	 * 
	 * @param numOfMsg
	 * @throws IOException
	 */
	public void produce(long numOfMsg, long delay) throws IOException {
		
		connection();
		for(int i = 1; i <= numOfMsg; i++) {
  	    	try {
	  	    	String message = "msg_"+i;
		  		m_channel.basicPublish("", m_queue, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
		  	    System.out.println(m_name + " [x] Sent '" + message + "'");
	  	    	TimeUnit.MILLISECONDS.sleep(delay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AlreadyClosedException ex) {
		  		System.out.println("oooppsss connection broke");
		  		try {
					TimeUnit.MILLISECONDS.sleep(delay);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  	}
	  	}
  	    m_channel.close();
        m_connection.close();
  	    
	}
	
	/**
	 * 
	 * @param numOfMsg
	 * @param delay
	 * @throws IOException
	 */
	public void produceMultiNode(long numOfMsg, long delay, String host, String queueName) throws IOException {
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();
	    //channel.queueDelete(queueName);
	    channel.queueDeclare(queueName, true, false, false, null);
	    channel.basicQos(100, false);
  		
	    for(int i = 1; i <= numOfMsg; i++) {
  	    	try {
	  	    	String message = "msg_"+i;
		  		channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
		  	    System.out.println(m_name + " [x] Sent '" + message + "'");
	  	    	TimeUnit.MILLISECONDS.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
	  	}
  	    channel.close();
        connection.close();
  	    
	}
	
	public void produceReliably(long numOfMsg, long delay) throws IOException, InterruptedException {
		final SortedSet<Long> unconfirmedSet = Collections.synchronizedSortedSet(new TreeSet());
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(m_host);
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();
	    channel.queueDeclare(m_queue, m_durable, m_exclusive, m_autoDelete, m_arguments);
	    channel.exchangeDeclare(App.getProperty("discoverexchange"), App.getProperty("exchangemode"), true, false, null);
	    channel.queueBind(m_queue, App.getProperty("discoverexchange"), "");
	    channel.confirmSelect();
	    System.out.println(m_name + " [*] Waiting for messages. To exit press CTRL+C");
  		
	    channel.addConfirmListener(new ConfirmListener() {
	        public void handleAck(long seqNo, boolean multiple) {
	            if (multiple) {
	            	long num = seqNo+1;
	                unconfirmedSet.headSet(num).clear();
	                System.out.println("Recevied multiple Ack for " + num);
	            } else {
	                unconfirmedSet.remove(seqNo);
	                System.out.println("Recevied single Ack for " + seqNo);
	            }
	        }
	        public void handleNack(long seqNo, boolean multiple) {
	            // handle the lost messages somehow
	        }
	    });
	    
	    for(int i = 1; i <= numOfMsg; i++) {
  	    	try {
	  	    	String message = "msg_"+i;
	  	    	unconfirmedSet.add(channel.getNextPublishSeqNo());
	  	    	channel.basicPublish(App.getProperty("discoverexchange"), "",true, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
		  	    System.out.println(m_name + " [x] Sent '" + message + "'");
	  	    	TimeUnit.MILLISECONDS.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (AlreadyClosedException ex) {
		  		System.out.println("oooppsss connection broke");
		  		try {
					TimeUnit.MILLISECONDS.sleep(delay);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  	}
	  	}
  	    
  	    //channel.waitForConfirms();
  	    channel.close();
        connection.close();
	}

	/**
	 * Produces msg of size 2k and publishes directly to the queue
	 * 
	 * @param numOfMsg
	 * @throws IOException
	 */
	public void produceMsgPerFile(long numOfMsg, Path path) throws IOException {
		
		connection();
		byte[] msg = Files.readAllBytes(path);
		
  		for(int i = 1; i <= numOfMsg; i++) {
  	    	m_channel.basicPublish("", m_queue, MessageProperties.PERSISTENT_TEXT_PLAIN, msg);
	  	    System.out.println(m_name + " [x] Sent msg_"+i);
	  	}
  	    m_channel.close();
        m_connection.close();
  	    
	}
	
	public static void main(String[] args) throws IOException {
		SimpleProducer producer = new SimpleProducer("Default_Producer");
		producer.produce(Long.MAX_VALUE);
	}
}
