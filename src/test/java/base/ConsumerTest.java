package base;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import org.junit.Test;

/**
 * Simple consumer test
 * @author Prince
 *
 */
public class ConsumerTest {

	private static final Logger s_logger = Logger.getLogger(ConsumerTest.class.getName());
	@Test
	public void test() throws IOException {
		
		QueueConsumer consumer = new QueueConsumer("Consumer");
		new Thread(consumer).start();
		
		QueueConsumer consumer2 = new QueueConsumer("Consumer_2");
		new Thread(consumer2).start();
		
		QueueProducer producer = new QueueProducer("Producer");
		for(int i = 1; i <= 10; i++) {
			HashMap<String, Integer> message = new HashMap<String, Integer>();
			message.put("msgNum", i);
			producer.publishMsg(message);
			s_logger.info("Message number " + i);
		}
		
	}

}
