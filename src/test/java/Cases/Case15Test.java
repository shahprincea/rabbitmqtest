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
					//Get file from resources folder
					ClassLoader classLoader = getClass().getClassLoader();
					File file = new File(classLoader.getResource("file/2kb.txt").getFile());
					Path path = Paths.get(file.toURI());
					producer.produceMsgPerFile(10, path);
					
					App.restartRabbit(2);
					
					producer.produceMsgPerFile(10, path);
					
					SimpleConsumer consumer_1 = new SimpleConsumer("Consumer_1");
					
					consumer_1.consumeAtWill(true, 10);//Try to Consume 10 msg as quickly as possible
					
					App.restartRabbit(2);
					
					consumer_1.consumeAtWill(true, 10);//Try to Consume 10 msg as quickly as possible
					
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}
			
		});
				
		executor.shutdown();
		executor.awaitTermination(20, TimeUnit.SECONDS);
		
	}
}
