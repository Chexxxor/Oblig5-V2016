package task2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class StudentServer {
	private final int MAX_CLIENTS = 10;
	private boolean serverRunning = true;
	private int count = 0;
	private ArrayList<Student> students = new ArrayList<>();

	public static void main(String[] args){
		new StudentServer();
	}

	public StudentServer(){
		try{
			ServerSocket serverSocket = new ServerSocket(8000);
			System.out.println("Server started...");
			
			new Thread(() -> {
				
			});
			
			while(count < MAX_CLIENTS && serverRunning){
				Socket socket = serverSocket.accept();
				
				new Thread(new StudentThread(socket, count++));
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	private class StudentThread implements Runnable {
		private Socket socket;
		private int number;
		private boolean clientRunning = true;
		private ObjectInputStream input;
		
		public StudentThread(Socket s, int n){
			socket = s;
			number = n;
		}
		
		public void run() {
			loadFile();
			try{
				input = new ObjectInputStream(socket.getInputStream());
				while(clientRunning && serverRunning){
					try{
						students.add((Student)input.readObject());
						if(students.size() % 10 == 0)
							saveFile();
					}
					catch(Exception e){
						System.err.println(e);
						clientRunning = false;
					}
				}
				input.close();
			}
			catch(IOException e){
				System.err.println(e);
			}
		}
	}
	
	public void loadFile(){
		File f = new File("Students.txt");
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
			while(in.available() > 0){
				students.add((Student)in.readObject());
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveFile(){
		File f = new File("Students.txt");
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
			for(Student s : students){
				out.writeObject(s);
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
