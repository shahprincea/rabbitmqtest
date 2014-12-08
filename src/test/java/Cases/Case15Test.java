package Cases;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import base.App;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * 
 * @author Prince
 *
 */
public class Case15Test {

	@Test
	public void singleProducer() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		
		ExecutorService executor = Executors.newFixedThreadPool(3);
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					SimpleProducer producer = new SimpleProducer("Producer");
					producer.produceReliably(10000, 0);
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}
			
		});
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					SimpleConsumer producer = new SimpleConsumer("Consumer");
					producer.consumeAtWill(true, 50);
					
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}
			
		});
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					for(int i =0; i < 3; i++) {
						App.restartRabbit(2);
						TimeUnit.SECONDS.sleep(1);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}
			
		});
				
		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.MINUTES);
		
	}
}
