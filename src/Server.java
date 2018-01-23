import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server implements IListenable {
	private DatagramSocket socket = null;
	private FileEvent fileEvent = null;
	private boolean flag = false;
	public FileOutputStream fileOutputStream = null;
	public File dstFile = null;
	public Listener messageListener;
	public Thread listener; 
	int serverPort;
	
	public static long waiting = 1000;
	public static int packetSize = 63 * 1024;
	
	public Server(int port) {
		messageListener = new Listener(this);
		listener = new Thread(messageListener);
		listener.start();
		serverPort = port;
	}

	public void createAndListen() {
		try {
			socket = new DatagramSocket(serverPort);

			byte[] incomingData = new byte[1024 * 1000 * 50];

			while (true) {
				DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
				socket.receive(incomingPacket);
				byte[] data = incomingPacket.getData();
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				ObjectInputStream is = new ObjectInputStream(in);

				fileEvent = (FileEvent) is.readObject();
				if (fileEvent.getStatus().equalsIgnoreCase("Error")) {
					System.out.println("Some issue happened while packing the data @ client side");
					System.exit(0);
				}
				//for(int i=0;i<fileEvent.getFileSize();i+=2048){
					createAndWriteFile(); // writing the file to hard disk
				//}
				if(dstFile.length() >= fileEvent.getFileSize()){
					InetAddress IPAddress = incomingPacket.getAddress();
					int port = incomingPacket.getPort();
					String reply = "Thank you for the message";
					byte[] replyBytea = reply.getBytes();
					DatagramPacket replyPacket =
					new DatagramPacket(replyBytea, replyBytea.length, IPAddress, port);
					socket.send(replyPacket);
				}
				Thread.sleep(Server.waiting - 10);

			}

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void createAndWriteFile() {
	String outputFile = fileEvent.getDestinationDirectory() + fileEvent.getFilename();

	if (!new File(fileEvent.getDestinationDirectory()).exists()) {
		new File(fileEvent.getDestinationDirectory()).mkdirs();
	}
	
	if(!flag){
		dstFile = new File(outputFile);
	}
	try {
		if(!flag){
			fileOutputStream = new FileOutputStream(dstFile);
			flag = true;
		}

		fileOutputStream.write(fileEvent.getFileData());

		if(dstFile.length() >= fileEvent.getFileSize()){
			fileOutputStream.flush();
			fileOutputStream.close();
		}
		System.out.println("Output file : " + outputFile + " is successfully saved ");

	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}

}

	
}