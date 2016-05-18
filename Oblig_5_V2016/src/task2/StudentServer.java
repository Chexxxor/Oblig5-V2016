package task2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class StudentServer {
	private final int MAX_CLIENTS = 10;
	private boolean serverRunning = true;
	private int count = 0;
	private ArrayList<Student> students = new ArrayList<>();

	public static void main(String[] args){
		new StudentServer();
	}

	public StudentServer(){
		loadFile();
		try{
			ServerSocket serverSocket = new ServerSocket(8000);
			System.out.println("Server started...");

			new Thread(() -> {
				Scanner input = new Scanner(System.in);
				while(serverRunning){
					String command = input.nextLine();
					switch(command){
					case "exit":
					case "close":
					case "quit":
						serverRunning = false;
					default:
						System.out.println(command);
					}
				}
				input.close();
			}).start();

			while(count <= MAX_CLIENTS && serverRunning){
				Socket socket = serverSocket.accept();
				
				new Thread(new StudentThread(socket, count++)).start();
			}
			serverSocket.close();
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
			try{
				input = new ObjectInputStream(socket.getInputStream());
				while(clientRunning && serverRunning){
					try{
						Student s = (Student)input.readObject();
						//System.out.println("Client " + number + " sent " + s.getName());
						System.out.println("Client " + number + " sent " + s);
						students.add(s);
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
		System.out.println("Starting Load");
		File f = new File("Students.txt");
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
			int count = 0;
			while(in.available() > 0){
				count++;
				Student s = (Student)in.readObject();
				students.add(s);
				System.out.println("Loaded student " + s.toString());
			}
			in.close();
			System.out.println("Load complete - loaded " + count + " objects:");
		} catch(FileNotFoundException e) {
			System.out.println("Loading failed - File not found");
		} catch(IOException e) {
			System.out.println("Loading failed - Failed to establish InputStream");
		} catch(ClassNotFoundException e){
			System.out.println("Loading failed - invalid class type received");
		}
	}

	public void saveFile(){
		System.out.println("Starting Save");
		File f = new File("Students.txt");
		try {
			int count = 0;
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
			for(Student s : students){
				count++;
				out.writeObject(s);
			}
			System.out.println("Saved " + count + " objects.");
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
