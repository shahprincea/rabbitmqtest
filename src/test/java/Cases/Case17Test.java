package Cases;

import java.io.IOException;

import org.junit.Test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;

public class Case17Test {
	@Test
	public void multipleConsumer() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
	
		int msgCount = 0;
		ConnectionFactory factory = new ConnectionFactory();
		factory.setAutomaticRecoveryEnabled(true);
		// attempt recovery every 10 seconds
		factory.setNetworkRecoveryInterval(1000);
	    factory.setHost("localhost");
	    Channel channel = factory.newConnection().createChannel();
	    msgCount = channel.queueDeclarePassive("DiscoveryQueue").getMessageCount();
	
	    System.out.println("Messages in queue : "+ msgCount);
	}
		
}

