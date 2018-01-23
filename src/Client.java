import java.io.*;
import java.net.*;

public class Client implements Sendable  {
	
	
	
	private DatagramSocket socket = null;
	private FileEvent event = null;
	private String sourceFilePath = "B:/ToSend/q.bmp";
	private String destinationPath = "B:/ToReceive/";
	private String hostName = "localhost";
	public Sender changeTime;
	
	private int serverPort;
	
	public void setServer(String port, String adr) {
		this.serverPort = Integer.valueOf(port);
		this.hostName = adr;
	}
	public Client() {}
	
	public Client(int speed) {
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
		System.out.println("PacketSize = " + Server.packetSize + " waiting " + Server.waiting);
	}

	public void createTestConnection() {
		try {
			
			socket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName(hostName);
			byte[] incomingData = new byte[1024];
			
			FileEvent fileEvent = new FileEvent();
			String fileName = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1, sourceFilePath.length());
			String path = sourceFilePath.substring(0, sourceFilePath.lastIndexOf("/") + 1);
			fileEvent.setDestinationDirectory(destinationPath);
			fileEvent.setFilename(fileName);
			fileEvent.setSourceDirectory(sourceFilePath);
			File file = new File(sourceFilePath);

			if (file.isFile()) {
				try {
					DataInputStream diStream = new DataInputStream(new FileInputStream(file));
					long len = (int) file.length();
					long fileSize = 0;
					
					
					System.out.println("PacketSize = " + Server.packetSize + " waiting " + Server.waiting);
					while(fileSize < len){
						byte[] fileBytes = new byte[Server.packetSize];
						int read = 0;
						int numRead = 0;
						while (read < fileBytes.length && (numRead = diStream.read(fileBytes, read, fileBytes.length - read)) >= 0) {
							read = read + numRead;
						}
						fileEvent.setFileData(fileBytes);
						fileSize += Server.packetSize;

						fileEvent.setFileSize(len);
						fileEvent.setStatus("Success");
						event = fileEvent;

						ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
						ObjectOutputStream os = new ObjectOutputStream(outputStream);
						os.writeObject(event);
						byte[] data = outputStream.toByteArray();
						DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, serverPort);
						socket.send(sendPacket);

						System.out.println("Sent " + fileSize + " / " + len);
						Thread.sleep(Server.waiting);
					}
					
					fileEvent.setStatus("Success");
				} catch (Exception e) {
					e.printStackTrace();
					fileEvent.setStatus("Error");
				}
			} else {
				System.out.println("path specified is not pointing to a file");
				fileEvent.setStatus("Error");
			}


			
			System.out.println("File sent from client");
			DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
			socket.receive(incomingPacket);
			String response = new String(incomingPacket.getData());
			System.out.println("Response from server:" + response);
			Thread.sleep(2000);
			System.exit(0);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public void createConnection(String sendFilepath, String receiveFilePath) {
		
			this.sourceFilePath = sendFilepath;
			this.destinationPath = receiveFilePath;
			
			createTestConnection();
	}

	public FileEvent getFileEvent() {
		FileEvent fileEvent = new FileEvent();
		String fileName = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1, sourceFilePath.length());
		String path = sourceFilePath.substring(0, sourceFilePath.lastIndexOf("/") + 1);
		fileEvent.setDestinationDirectory(destinationPath);
		fileEvent.setFilename(fileName);
		fileEvent.setSourceDirectory(sourceFilePath);
		File file = new File(sourceFilePath);

		if (file.isFile()) {
			try {
				DataInputStream diStream = new DataInputStream(new FileInputStream(file));
				long len = (int) file.length();
				long fileSize = 0;
				
				while(fileSize < len){
					byte[] fileBytes = new byte[Server.packetSize];
					int read = 0;
					int numRead = 0;
					while (read < fileBytes.length && (numRead = diStream.read(fileBytes, read, fileBytes.length - read)) >= 0) {
						read = read + numRead;
					}
					fileEvent.setFileData(fileBytes);
					fileSize += Server.packetSize;
					System.out.println("Sent " + fileSize + " / " + len);
				}
				fileEvent.setFileSize(len);
				
				fileEvent.setStatus("Success");
			} catch (Exception e) {
				e.printStackTrace();
				fileEvent.setStatus("Error");
			}
		} else {
			System.out.println("path specified is not pointing to a file");
			fileEvent.setStatus("Error");
		}
		return fileEvent;
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
	/*public static void main(String[] args) {
		Client client = new Client();
		client.createConnection();
	}*/
	public void setAdr(String source, String destination) {
		this.sourceFilePath = source;
		this.destinationPath = destination;
		
	}
}