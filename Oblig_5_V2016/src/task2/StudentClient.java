package task2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class StudentClient {
	private ArrayList<Student> students = new ArrayList<>();
	private boolean running = true;
	
	public static void main(String[] args){
		new StudentClient();
	}

	public StudentClient() {
		try{
			/*students.add(new Student("Abe", "Lincoln", "Kings", "Washington", "DC", "85551"));
			students.add(new Student("Zacharry", "Bale", "Queens", "Chicago", "Wyoming", "81333"));
			students.add(new Student("Bill", "Clinton", "Princeton", "Detroit", "Maine", "11942"));
			students.add(new Student("Simon", "Bakeman", "Oxford", "Winchester", "Highlands", "37-123"));
			students.add(new Student("Michael", "Myers", "Bunny", "Las Vegas", "California", "69692"));
			students.add(new Student("Franz", "McKnight", "Vegas strt.", "Los Angeles", "Oklahoma", "74885"));
			students.add(new Student("Tina", "Turner", "Broadway", "NYC", "NY", "88226"));
			students.add(new Student("Hanna", "O'hanna", "Manwood", "Miami", "NY", "88302"));
			students.add(new Student("Wilbur", "Mittens", "Elm street", "Liverpool", "Lowlands", "34-987"));
			students.add(new Student("John", "Birch", "Queens", "NYC", "Massachusettes", "88223"));*/
			Socket socket = new Socket("localhost", 8000);
			new Thread(new Listener(socket)).start();
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			/*for(Student s : students){
				out.writeObject(s);
			}*/
			Scanner input = new Scanner(System.in);
			while(running){
				Student s;
				try {
					s = newStudent(input);
					out.writeObject(s);
				} catch (ShutdownException e) {
					running = false;
				}
			}
			input.close();
			socket.close();
		}
		catch(IOException e){}
	}
	
	private class Listener implements Runnable {
		Socket socket;
		
		public Listener(Socket socket){
			this.socket = socket;
		}

		@Override
		public void run() {
			System.out.println("Starting server listener...");
			try {
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				System.out.println("Listener: Started");
				while(running){
					try {
						Student s = (Student)in.readObject();
						students.add(s);
						System.out.println("Listener: Received object from server - " + s.toString());
					} catch (ClassNotFoundException e) {
						System.out.println("Listener: Bad data from server");
					}
				}
			} catch (IOException e) {
				running = false;
				System.out.println("Listener: Failed to establish InputStream");
			}
		}
	}

	public Student newStudent(Scanner input) throws ShutdownException {
		System.out.println("First name (Type \"exit\" or \"close\" to stop client): ");
		String fName = input.nextLine();
		if(fName.toLowerCase().equals("exit") || fName.toLowerCase().equals("close"))
			throw new ShutdownException();
		System.out.print("Last name: ");
		String lName = input.nextLine();
		System.out.print("Street: ");
		String street = input.nextLine();
		System.out.print("City: ");
		String city = input.nextLine();
		System.out.print("State: ");
		String state = input.nextLine();
		System.out.print("Zip: ");
		String zip = input.nextLine();
		return new Student(fName, lName, street, city, state, zip);
	}
	
	@SuppressWarnings("serial")
	private class ShutdownException extends Exception {}
}
