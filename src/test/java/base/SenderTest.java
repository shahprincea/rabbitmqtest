package base;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;


/**
 * Sender test
 * 
 * @author Prince
 *
 */
public class SenderTest {

	private static final Logger s_logger = Logger.getLogger(SenderTest.class.getName());
	
	public void testBasicPublish() throws IOException {
		Producer sender = new Producer("Producer");
		for(int i = 0; i < 10; i++) {
			HashMap<String, Integer> message = new HashMap<String, Integer>();
			message.put("msgNum", i);
			sender.publishMsg(message);
			s_logger.info("Message number " + i);
		}
	}
}
