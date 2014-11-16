package base;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.apache.commons.lang3.SerializationUtils;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

public class QueueConsumer extends AbstractAgent implements Runnable, Consumer {

	private static final Logger s_logger = Logger.getLogger(QueueConsumer.class.getName());
	public QueueConsumer(String agentName) throws IOException {
		super(agentName);
		super.setupConnection();
	}

	/**
	 * Called when Consumer is registered
	 */
	@Override
	public void handleConsumeOk(String consumerTag) {
		s_logger.info("Consumer " + agentName + " having consumerTag " + consumerTag +" registered");
	}

	@Override
	public void handleCancelOk(String consumerTag) {
		s_logger.info("Consumer " + agentName + " having consumerTag " + consumerTag +" de-registered");
	}

	@Override
	public void handleCancel(String consumerTag) throws IOException {
		s_logger.info("Consumer " + agentName + " having consumerTag " + consumerTag +" de-registered as queue is deleted");
		
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope env,
			BasicProperties props, byte[] body) throws IOException {
		Map<String, Integer> map = (HashMap<String, Integer>)SerializationUtils.deserialize(body);
		s_logger.info(agentName + " consumed msg_" + map.get("msgNum"));
	}

	@Override
	public void handleShutdownSignal(String consumerTag,
			ShutdownSignalException sig) {
		
		
	}

	@Override
	public void handleRecoverOk(String consumerTag) {
		
		
	}

	@Override
	public void run() {
		try {
			channel.basicConsume(App.getProperty("queuename"), true, this);
			s_logger.info(agentName + " sleeping for 2sec");
			TimeUnit.SECONDS.sleep(2);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
