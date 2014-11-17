package Cases;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * Queue should be bounded.
 * RabbitMQ does not allow bounding it however allows you to limit number of msg in queue. It will drop older messages as required. 
 * Here we assigned queue to hold no more then 10 message and try to publish 100 messages
 * @author Prince
 *
 */
public class Case11Test {

	@Test
	public void multipleProducer() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		
		ExecutorService executor = Executors.newFixedThreadPool(3);
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Map<String, Object> queueProps = new HashMap<String, Object>();
					queueProps.put("x-max-length", 100);
					SimpleProducer producer = new SimpleProducer("Producer", queueProps);
					producer.produce(100);
					
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}
			
		});
		
		executor.shutdown();
		executor.awaitTermination(8, TimeUnit.SECONDS);
		
	}
}
