package base;

import static org.junit.Assert.*;

import org.junit.Test;

public class AppTest {

	@Test
	public void testGetProperty() {
		String value = App.getProperty("host");
		assertTrue(!value.isEmpty() && value.length() > 0);
	}

}
