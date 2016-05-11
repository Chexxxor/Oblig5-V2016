package task2;

public class Student implements Comparable<Student>, Cloneable {
	private String firstname;
	private String lastname;
	private String street;
	private String city;
	private String state;
	private String zip;
	
	public static void main(String[] args) throws CloneNotSupportedException{
		Student a = new Student("John", "Birch", "Queens", "NYC", "Massachusettes", "88223");
		Student b = (Student) a.clone();
		b.firstname = "Jomes";
		System.out.println(a.compareTo(b));
	}

	public Student(String firstname, String lastname, String street, String city, String state, String zip) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.street = street;
		this.city = city;
		this.state = state;
		this.zip = zip;
	}

	@Override
	public int compareTo(Student s) {
		if(lastname.equals(s.lastname))
			return firstname.compareTo(s.firstname);
		else
			return lastname.compareTo(s.lastname);
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException{
		Student clone = (Student)super.clone();
		clone.firstname = new String(firstname);
		clone.lastname = new String(lastname);
		clone.street = new String(street);
		clone.city = new String(city);
		clone.state = new String(state);
		clone.zip = new String(zip);
		return clone;
	}
}
