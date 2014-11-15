package WithExchange;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Sender {

	public static void main(String[] args) throws IOException {
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		try {
		    channel.queueDeclare("TaskQ", false, false, false, null);
		    channel.exchangeDeclare("Exchange1", "direct", true);
		    channel.queueBind("Queue1", "Exchange1", "info");
		    String message = "This is a test message";
		    
		    Map<String, Object> headers = new HashMap<String, Object>();
		    
		    AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties().builder();
		    builder.messageId("123456789");
		    builder.headers(headers);
		    
		    channel.basicPublish("Exchange1", "Queue1", builder.build(), message.getBytes());

		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    channel.close();
		    connection.close();
		}

	}

}
