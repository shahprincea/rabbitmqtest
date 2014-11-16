package base;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 * Simple Consumer test
 * @author Prince
 *
 */
public class ConsumerTest {

	@Test
	public void test() throws IOException, InterruptedException, ExecutionException {
		
		
		FutureTask<String> consumerFuture_1 = new FutureTask<String>(new Consumer("Consumer_1"));
		FutureTask<String> consumerFuture_2 = new FutureTask<String>(new Consumer("Consumer_2"));
		
		ExecutorService executor = Executors.newFixedThreadPool(2);
		executor.execute(consumerFuture_1);
		executor.execute(consumerFuture_2);
		
		
		
		Producer producer = new Producer("Producer");
		int numMsg = 10;
		for(int i = 1; i <= numMsg; i++) {
			HashMap<String, Integer> message = new HashMap<String, Integer>();
			message.put("msgNum", i);
			producer.publishMsg(message);
			System.out.println("Published Message number " + i);
		}
		
		executor.awaitTermination(8, TimeUnit.SECONDS);
	    
		
		
	}

}
