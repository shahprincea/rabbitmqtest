package Cases;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * Consumers can be added/removed dynamically 
 * 
 * Here we produce 10 messages and consumer 1 and consumer 2 
 * starts at different point in time to consume all the messages
 * 
 * @author Prince
 *
 */
public class Case1Test {

	@Test
	public void multipleConsumer() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		
		ExecutorService executor = Executors.newFixedThreadPool(3);
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
		
		executor.execute(new Runnable() {

			@Override
			public void run() {
				
				SimpleConsumer consumer_1 = new SimpleConsumer("Consumer_1");
				try {
					consumer_1.consumeAtWill(true);//Try to Consume 10 msg as quickly as possible
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}
			
		});
		
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					TimeUnit.SECONDS.sleep(3);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				SimpleConsumer consumer_2 = new SimpleConsumer("Consumer_2");
				try {
					consumer_2.consumeAtWill(true);//Try to Consume 10 msg as quickly as possible
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}
			
		});
		
		executor.shutdown();
		executor.awaitTermination(20, TimeUnit.SECONDS);
	}
}
