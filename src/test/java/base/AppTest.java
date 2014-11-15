package base;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

/**
 * Simple test which stop start rabbitMQ and leaves rabbit as it was before
 * @author prince
 *
 */
public class AppTest {

	@Test
	public void testGetProperty() {
		String value = App.getProperty("host");
		assertTrue(!value.isEmpty() && value.length() > 0);
	}

	@Test
	public void testStartStopRabbitMQ() throws InterruptedException, IOException {
		if(App.isRabbitAlive()) {
			App.stopRabbit();
			App.startRabbit();
		} else {
			App.startRabbit();
			App.stopRabbit();
			assertTrue(!App.isRabbitAlive());
		}
	}
}
