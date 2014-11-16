package base;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;


/**
 * Producer test
 * 
 * @author Prince
 *
 */
public class ProducerTest {

	private static final Logger s_logger = Logger.getLogger(ProducerTest.class.getName());
	
	public void testBasicPublish() throws IOException {
		QueueProducer producer = new QueueProducer("P1");
		for(int i = 0; i < 10; i++) {
			HashMap<String, Integer> message = new HashMap<String, Integer>();
			message.put("msgNum", i);
			producer.publishMsg(message);
			s_logger.info("Message number " + i);
		}
	}
}
