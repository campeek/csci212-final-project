public class book{
	private String author;
	private String title;
	private int serial_number;
	public book(){
		this.author="";
		this.title="";
		this.serial_number=-1;
	}
	public book(String author_in, String title_in, int serial_number_in){
		this.author=author_in;
		this.title=title_in;
		this.serial_number=serial_number_in;
	}
	public book(String whole_line) {
		try {
		String[] temp=whole_line.split(",");
		this.author=temp[0];
		this.title=temp[1];
		this.serial_number=Integer.parseInt(temp[2]);
		}
		catch(Exception e) {
			System.err.println("error converting whole string to book. remember to use the format 'author,title,number'");
		}
	}
	public String get_author(){
		return author;
	}
	public void set_author(String author_in){
		this.author=author_in;
	}
	public String get_title(){
		return title;
	}
	public void set_title(String title_in){
		this.title=title_in;
	}
	public int get_serial_number(){
		return serial_number;
	}
	public void set_serial_number(int serial_number_in){
		this.serial_number=serial_number_in;
	}
	//TODO: make the bits that write to and read from files.
}

