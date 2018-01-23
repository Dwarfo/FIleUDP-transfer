
public class Listener implements Runnable{
	
	private IListenable agentListener;
	//private ServerSocket serverSocket;
	
	public Listener(IListenable agent) {
		
		this.agentListener = agent;
		
	}

	@Override
	public void run() {
		try {
			agentListener.createAndListen();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
}
