package Cases;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import base.App;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;

public class Case16Test {

	private final static String queue =  "mirroredQueue";
	
	@Test
	public void multipleConsumer() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		
		ExecutorService executor = Executors.newFixedThreadPool(4);
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					SimpleProducer producer = new SimpleProducer("Producer");
					producer.produceMultiNode(1000000, 0, "192.168.177.83", queue);
					
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}
			
		});
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					SimpleConsumer consumer = new SimpleConsumer("Consumer_83");
					consumer.consumeMultiNodeAtWill(true, 0, false, "192.168.177.83", queue);
					
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}
			
		});
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					SimpleConsumer consumer = new SimpleConsumer("Consumer_84");
					consumer.consumeMultiNodeAtWill(true, 0, false, "192.168.177.84", queue);
					
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}
			
		});
		
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					SimpleConsumer consumer = new SimpleConsumer("Consumer_85");
					consumer.consumeMultiNodeAtWill(true, 0, false, "192.168.177.85", queue);
					
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}
			
		});
		
		executor.shutdown();
		executor.awaitTermination(20, TimeUnit.MINUTES);
		
	}
}
