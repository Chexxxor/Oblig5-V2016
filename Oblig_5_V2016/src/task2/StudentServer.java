package task2;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
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
	private ArrayList<StudentThread> clients = new ArrayList<>();
	ServerSocket serverSocket;
	ConnectionListener connection;

	public static void main(String[] args){
		new StudentServer();
	}

	public StudentServer(){
		System.out.println("Server starting...");
		try{
			loadFile();
			serverSocket = new ServerSocket(8000);
			System.out.println("Server started");

			InputListener listener = new InputListener();
			listener.start();
			(connection = new ConnectionListener()).start();
			try {
				listener.join();
			} catch (InterruptedException e) {}
			serverSocket.close();
			saveFile();
		}
		catch(IOException e){
			System.out.println("Server failed to start or close");
		}
	}
	
	private class ConnectionListener extends Thread {
		@Override
		public void run(){
			while(clients.size() <= MAX_CLIENTS && serverRunning){
				Socket socket;
				try {
					socket = serverSocket.accept();
					StudentThread client = new StudentThread(socket, count++);
					client.start();
					clients.add(client);
				} catch (IOException e) {
					System.out.println("Server: Client connection failed");
				}
				
			}
		}
	}
	
	private class InputListener extends Thread {
		@Override
		public void run(){
			Scanner input = new Scanner(System.in);
			while(serverRunning){
				String command = input.nextLine();
				switch(command){
				case "exit":
				case "close":
				case "quit":
					serverRunning = false;
					System.out.println("Shutting down");
				default:
					System.out.println(command);
				}
			}
			input.close();
			if(connection.isAlive()){
				try {
					System.out.println("Waiting for client connections to shut down");
					System.out.println("Forcing shutdown in 5 seconds");
					sleep(5000);
				} catch (InterruptedException e){}
			}
			System.out.println("Disconnecting clients");
			for(StudentThread client : clients){
				if(client.isAlive())
					client.interrupt();
			}
			if(connection.isAlive()){
				System.out.println("Shutting down connection");
				connection.interrupt();
			}
			System.out.println("Shutting down input listener");
		}
	}

	private class StudentThread extends Thread {
		private Socket socket;
		private int number;
		private boolean clientRunning = true;
		private ObjectInputStream in;
		private ObjectOutputStream out;

		public StudentThread(Socket socket, int n){
			this.socket = socket;
			number = n;
			try {
				out = new ObjectOutputStream(socket.getOutputStream());
				for(Student s : students){
					send(s);
				}
			} catch (IOException e) {
				clientRunning = false;
				System.out.println("Client " + number + " - Failed establish OutputStream");
			}
		}
		
		public void send(Student s){
			try {
				out.writeObject(s);
			} catch (IOException e) {
				System.out.println("Client " + number + " - Failed to send object: " + s.toString());
			}
		}

		public void run() {
			try{
				in = new ObjectInputStream(socket.getInputStream());
				while(clientRunning && serverRunning){
					try{
						Student s = (Student)in.readObject();
						//System.out.println("Client " + number + " sent " + s.getName());
						System.out.println("Client " + number + " sent " + s.toString());
						students.add(s);
						if(students.size() % 10 == 0)
							saveFile();
						for(StudentThread t : clients){
							t.send(s);
						}
					}
					catch(IOException e){
						System.out.println("Client " + number + ": Connection failed");
						clientRunning = false;
					} catch (ClassNotFoundException e) {
						System.out.println("Client " + number + ": Invalid data received");
					}
				}
				in.close();
			} catch(IOException e){
				clientRunning = false;
				System.out.println("Client " + number + " - Failed to establish InputStream");
			} finally {
				clients.remove(this);
			}
		}
	}

	public void loadFile(){
		System.out.println("Starting Load...");
		File f = new File("Students.txt");
		try {
			boolean continueReading = true;
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
			int count = 0;
			while(continueReading){
				try {
				Student s = (Student)in.readObject();
				count++;
				students.add(s);
				System.out.println("Loaded student " + s.toString());
				} catch(EOFException e) {
					continueReading = false;
				}
			}
			in.close();
			System.out.println("Load complete - Loaded " + count + " objects:");
		} catch(FileNotFoundException e) {
			System.out.println("Loading failed - File not found");
		} catch(IOException e) {
			System.out.println("Loading failed - Failed to establish InputStream");
		} catch(ClassNotFoundException e){
			System.out.println("Loading failed - Invalid object type received");
		}
	}

	public void saveFile(){
		System.out.println("Starting save...");
		File f = new File("Students.txt");
		try{
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
			int count = 0;
			for(Student s : students){
				count++;
				try{
					out.writeObject(s);
				} catch(NotSerializableException | InvalidClassException e) {
					System.out.println("Failed to save object: " + s.toString() + " - Bad object type");
				} catch(IOException e) {
					System.out.println("Failed to save object: " + s.toString() + " - Bad OutputStream");
				}
			}
			System.out.println("Save complete - Saved " + count + " objects.");
			try{
				out.close();
			} catch(IOException e){
				System.out.println("Error closing file during save");
			}
		} catch(IOException e) {
			System.out.println("Save failed - Failed to establish OutputStream");
		}
	}
}
