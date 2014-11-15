package base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

/**
 * Producer test
 * 
 * @author Prince
 *
 */
public class ProducerTest {

	private static final Logger s_logger = Logger.getLogger(ProducerTest.class.getName());
	
	public void testBasicPublish() throws IOException {
		Producer producer = new Producer("P1");
		for(int i = 0; i < 10; i++) {
			HashMap<String, Integer> message = new HashMap<String, Integer>();
			message.put("Message Number", i);
			producer.publishMsg(message);
			s_logger.info("Message number " + i);
		}
		s_logger.info("Next Publish Seq No : " + String.valueOf(producer.channel.getNextPublishSeqNo()));
	}
}
