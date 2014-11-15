package OneProducerTwoConsumer;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class Recv {

	public static final String QUEUE_NAME = "DiscoveryQueue";
	private static final int RESET = 5;


	public static void main(String[] args) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
	    Connection connection = factory.newConnection();
	    
	    Channel channel = connection.createChannel();

	    channel.queueDeclare(QUEUE_NAME, true, false, false, null);
	    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
	    
	    QueueingConsumer consumer = new QueueingConsumer(channel);
	    channel.basicConsume(QUEUE_NAME, consumer);
        
        int counter = 0;
	    while (true) {
	      counter++;
	      QueueingConsumer.Delivery delivery = consumer.nextDelivery();
	      Envelope envelope = delivery.getEnvelope();
	      String message = new String(delivery.getBody());
	      if(counter < RESET) {
	    	  channel.basicAck(envelope.getDeliveryTag(), false); 
	    	  System.out.println(" [x] Received '" + message + "'" + " seen " + envelope.isRedeliver());
	      }
	      else  {
	    	  System.out.println(" [x] Received but was rejected for fun '" + message + "'" + " seen " + envelope.isRedeliver());
	    	  channel.basicNack(envelope.getDeliveryTag(), false, true);
	    	  counter = 0;
	      }	  
	      
	      
	    }

	}
}
