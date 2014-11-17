package Cases;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * If consumer dies after dequeueing that msg should be re-delivered to other consumer.
 * 
 * Here we produce 2 messages and we have two consumer
 * Consume_1 consumes 1 messages however it never sends ack and dies (thread is stopped) that message is picked up by other consumer.
 * 
 * @author Prince
 *
 */
public class Case2Test {

	@Test
	public void multipleConsumer() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		
		ExecutorService executor = Executors.newFixedThreadPool(3);
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					SimpleProducer producer = new SimpleProducer("Producer");
					producer.produce(2);
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}
			
		});
		//Consume one message and die immediately
		executor.execute(new Runnable() {

			@Override
			public void run() {
				SimpleConsumer consumer_1 = new SimpleConsumer("Consumer_1");
				try {
					consumer_1.consumeAndDie(1); 
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}
			
		});
		//Consume messages as will
		executor.execute(new Runnable() {

			@Override
			public void run() {
				SimpleConsumer consumer_2 = new SimpleConsumer("Consumer_2");
				try {
					consumer_2.consumeAtWill(true);//
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}
			
		});
		executor.shutdown();
		executor.awaitTermination(100, TimeUnit.SECONDS);
	}
}
