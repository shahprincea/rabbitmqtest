package Cases;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import base.App;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;

public class Case14Test {
	
	@Test
	public void multipleConsumer() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		
		ExecutorService executor = Executors.newFixedThreadPool(3);
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					SimpleProducer producer = new SimpleProducer("Producer");
					//producer.produceWithDelay(10,2000);
					
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}
			
		});
		executor.execute(new Runnable() {

			@Override
			public void run() {
				/*try {
					SimpleConsumer producer = new SimpleConsumer("Consumer");
					producer.consumeAtWill(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}  */
			}
			
		});
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					for(int i =0; i < 3; i++) {
						App.restartRabbit(2000);
						TimeUnit.SECONDS.sleep(5);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}
			
		});
		
		executor.shutdown();
		executor.awaitTermination(20, TimeUnit.MINUTES);
		
	}
}
