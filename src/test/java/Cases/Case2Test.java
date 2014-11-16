package Cases;

import java.io.IOException;

import org.junit.Test;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;

public class Case2Test {

	@Test
	public void multipleConsumer() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		SimpleProducer producer = new SimpleProducer("Producer");
		SimpleConsumer consumer_1 = new SimpleConsumer("Consumer_1");
		producer.produce(10);
		consumer_1.consume(5, false);
		SimpleConsumer consumer_2 = new SimpleConsumer("Consumer_2");
		consumer_2.consume(10, true);
	}
}
