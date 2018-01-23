import java.io.IOException;
import java.util.Random;

public class Sender implements Runnable {
	
	private Sendable agentSender;
	private int message;
	Random rand = new Random();

	
	public Sender(Sendable agentSender/*,int message*/) {
		
		this.message = message;
		this.agentSender = agentSender;
		
	}
	
	@Override
	public void run() {
		//Thread.sleep(rand.nextInt(80) + 30);
		agentSender.createTestConnection();
		
	}

}
