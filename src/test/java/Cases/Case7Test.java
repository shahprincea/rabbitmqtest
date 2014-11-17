package Cases;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;

public class Case7Test {

	@Test
	public void multipleProducer() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		
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
					producer.produceSmallMsg(10, path);
					
					file = new File(classLoader.getResource("file/20kb.txt").getFile());
					path = Paths.get(file.toURI());
					producer.produceSmallMsg(10, path);
					
					file = new File(classLoader.getResource("file/200kb.txt").getFile());
					path = Paths.get(file.toURI());
					producer.produceSmallMsg(10, path);
					
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}
			
		});
		
		executor.shutdown();
		executor.awaitTermination(8, TimeUnit.SECONDS);
		
	}
}
