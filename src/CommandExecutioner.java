import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class CommandExecutioner {
	
	//All possible commands
	private final String sendTestMsg = "sendTestMsg";
	private final String sendMsg = "sendMsg";
	private final String setSpeed = "setSpeed";
	
	boolean reading = true;
	
	private BufferedReader commandReader;
	Client client = new Client();
	
	
	public CommandExecutioner() throws Exception {
		
		Server server = new Server(6000);
		
		commandReader = new BufferedReader(new InputStreamReader(System.in));
		//System.out.println("All posible commands " + allPosibleCommands);
		
		this.readCommands();
		
	}
	public void readCommands() throws IOException, InterruptedException {
		
		System.out.println("Write Server address");
		String command = commandReader.readLine();
		
		String[] commandPart = command.split("\\s+");
		
		client.setServer(commandPart[0], commandPart[1]);
		System.out.println("New serverAdr: " + commandPart[0] + ": " + commandPart[1]);
		//executeCommand(command);
		System.out.println("Type command");
		while(reading) {
			command = commandReader.readLine();
			
			executeCommand(command);
		}
		
	}
	
	public void executeCommand(String command) throws IOException, InterruptedException {
	
		String[] commandPart = command.split("\\s+");
		
		
		switch(commandPart[0]) {
		case sendTestMsg: 
			//client.createTestConnection();
			
			break;
		case sendMsg:
			client.setAdr(commandPart[1],commandPart[2]);
			client.changeTime = new Sender(client);
			Thread messageSender = new Thread(client.changeTime);
			messageSender.start();
			break;
		case setSpeed:
			//client.setSpeed(Integer.valueOf(commandPart[1]));
			
			setSpeed(Integer.valueOf(commandPart[1]));
			break;
		
		default:
			System.out.println("Unknown comand");
		}
		
	}
	public void setSpeed(int speed) {
		float koef = 0;
		if(speed < 64) {
			Server.packetSize = speed * 1024;
			Server.waiting = 1000;
		}
		else {
			koef = 64/(float)speed * 1000;
			Server.packetSize = 63 * 1024;
			Server.waiting = (long)koef;
		}
		System.out.println("Koef " + koef);
		System.out.println("PacketSize = " + Server.packetSize + " waiting " + Server.waiting);
	}
	
	
	
	
}
