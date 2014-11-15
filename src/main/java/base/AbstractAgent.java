/**
 * 
 */
package base;

import java.io.IOException;

/**
 * Abstract implementaion of Agent
 * 
 * @author Prince
 *
 */
public class AbstractAgent implements Agent {

	/* (non-Javadoc)
	 * @see base.Agent#setupConnection()
	 */
	public boolean setupConnection() {
		return false;
	}

	/* (non-Javadoc)
	 * @see base.Agent#close()
	 */
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

}
