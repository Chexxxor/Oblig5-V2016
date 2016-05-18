package task2;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class StudentClient {
	public static void main(String[] args){
		new StudentClient();
	}

	public StudentClient() {
		Scanner input = new Scanner(System.in);
		try{
			boolean running = true;
			ArrayList<Student> students = new ArrayList<>();
			students.add(new Student("Abe", "Lincoln", "Kings", "Washington", "DC", "85551"));
			students.add(new Student("Zacharry", "Bale", "Queens", "Chicago", "Wyoming", "81333"));
			students.add(new Student("Bill", "Clinton", "Princeton", "Detroit", "Maine", "11942"));
			students.add(new Student("Simon", "Bakeman", "Oxford", "Winchester", "Highlands", "37-123"));
			students.add(new Student("Michael", "Myers", "Bunny", "Las Vegas", "California", "69692"));
			students.add(new Student("Franz", "McKnight", "Vegas strt.", "Los Angeles", "Oklahoma", "74885"));
			students.add(new Student("Tina", "Turner", "Broadway", "NYC", "NY", "88226"));
			students.add(new Student("Hanna", "O'hanna", "Manwood", "Miami", "NY", "88302"));
			students.add(new Student("Wilbur", "Mittens", "Elm street", "Liverpool", "Lowlands", "34-987"));
			students.add(new Student("John", "Birch", "Queens", "NYC", "Massachusettes", "88223"));
			Socket socket = new Socket("localhost", 8000);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			for(Student s : students){
				out.writeObject(s);
			}
			while(running){
				Student s = newStudent(input);
				out.writeObject(s);
			}
			socket.close();
		}
		catch(IOException e){}
		input.close();
	}

	public Student newStudent(Scanner input){
		System.out.print("First name: ");
		String fName = input.nextLine();
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
}
