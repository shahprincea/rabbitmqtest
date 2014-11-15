package OneProducerTwoConsumer;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Send {

	public static final String QUEUE_NAME = "TaskQ";
	
	public static void main(String[] arg) throws InterruptedException {
			Connection conn = null;
			Channel ch = null;
			try {
				ConnectionFactory factory = new ConnectionFactory();
				factory.setHost("localhost");
				conn = factory.newConnection();
				ch = conn.createChannel();
				
				ch.queueDeclare(QUEUE_NAME, false, false, false, null);
				for(int i = 0; i < 20 ; i++) {
					String msq = i+" Msg";
					ch.basicPublish("", QUEUE_NAME, null, msq.getBytes());
					System.out.println("[x] sent '" + msq + "'");
					Thread.sleep(1000);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				
				try {
					ch.close();
					conn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				
			}
	}
}
