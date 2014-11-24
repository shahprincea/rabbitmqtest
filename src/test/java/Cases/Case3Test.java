package Cases;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * Queue should never take away work from consumer even if consumer takes forever to complete.
 * 
 * Here Producer produces 1 messages and consumer_1 takes forever to complete. But consumer_2 will not steal its work 
 * allowing consumer_1 to complete it 
 * 
 * @author Prince
 *
 */
public class Case3Test {

	@Test
	public void multipleConsumer() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
	
		
		ExecutorService executor = Executors.newFixedThreadPool(3);
		//Produce 10 messages
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					SimpleProducer producer = new SimpleProducer("Producer");
					producer.produce(10000);
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}
			
		});
		//Consume 1 msg and but waits for 1 minute before sending ack
		executor.execute(new Runnable() {

			@Override
			public void run() {
				SimpleConsumer consumer_1 = new SimpleConsumer("Consumer_1");
				try {
					consumer_1.consumeAtWill(true, 1000, false); 
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}
			
		});
		//Consume as many messages as you can 
		executor.execute(new Runnable() {

			@Override
			public void run() {
				SimpleConsumer consumer_2 = new SimpleConsumer("Consumer_2");
				try {
					consumer_2.consumeAtWill(true); 
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}
			
		});
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.MINUTES);
	}
}
