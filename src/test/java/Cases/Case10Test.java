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

/**
 * Queue should block producers in case of Disk full (or reaching some threshold).
 * 
 * Here we have disk_free_limit to very amount (10.5GB) and we will quickly see alarm. 
 * At this point we should block producer. Once you consume msg producer will be 
 * allowed to publish more messages
 * 
 * @author Prince
 *
 */
public class Case10Test {

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
					File file = new File(classLoader.getResource("file/200kb.txt").getFile());
					Path path = Paths.get(file.toURI());
					producer.produceMsgPerFile(10000000, path);
					
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}
			
		});
		
		executor.shutdown();
		executor.awaitTermination(500, TimeUnit.SECONDS);
		
	}
}
