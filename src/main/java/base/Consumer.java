package base;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.apache.commons.lang3.SerializationUtils;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * Consumer which 
 * @author Prince
 *
 */
public class Consumer extends AbstractAgent implements Callable<String>, com.rabbitmq.client.Consumer {

	private static final Logger s_logger = Logger.getLogger(Consumer.class.getName());
	public Consumer(String agentName) throws IOException {
		super(agentName);
		super.setupConnection();
	}

	/**
	 * Called when Consumer is registered
	 */
	@Override
	public void handleConsumeOk(String consumerTag) {
		System.out.println("Consumer " + agentName + " having consumerTag " + consumerTag +" registered");
	}

	@Override
	public void handleCancelOk(String consumerTag) {
		System.out.println("Consumer " + agentName + " having consumerTag " + consumerTag +" de-registered");
	}

	@Override
	public void handleCancel(String consumerTag) throws IOException {
		System.out.println("Consumer " + agentName + " having consumerTag " + consumerTag +" de-registered as queue is deleted");
		
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope env,
			BasicProperties props, byte[] body) throws IOException {
		try {
			Map<String, Integer> map = (HashMap<String, Integer>)SerializationUtils.deserialize(body);
			System.out.println(agentName + " consumed msg_" + map.get("msgNum"));
			//System.out.println(agentName + " sleeping for 2sec");
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleShutdownSignal(String consumerTag,
			ShutdownSignalException sig) {
		
		
	}

	@Override
	public void handleRecoverOk(String consumerTag) {
		
		
	}

	@Override
	public String call() throws Exception {
		try {
			channel.basicConsume(App.getProperty("queuename"), true, this);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}

}
