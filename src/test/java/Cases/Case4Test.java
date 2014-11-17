package Cases;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import base.App;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * Messages should survive restart of RabbitMQ
 * 
 * Producer produces 10 msg and restarts RabbitMQ. Consumer should be able to read those messages. 
 * @author Prince
 *
 */
public class Case4Test {

	@Test
	public void multipleConsumer() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		
		ExecutorService executor = Executors.newFixedThreadPool(3);
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					SimpleProducer producer = new SimpleProducer("Producer");
					producer.produce(2000);
					
					System.out.println("Shutting down RabbitMQ");
					App.restartRabbit(1);
					System.out.println("RabbitMQ is up");
					
					
					SimpleConsumer consumer_1 = new SimpleConsumer("Consumer");
					consumer_1.consumeAtWill(true);
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}
			
		});
		
		executor.shutdown();
		executor.awaitTermination(8, TimeUnit.SECONDS);
		
	}
}
