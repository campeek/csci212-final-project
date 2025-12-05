// User.java
// This will represent ONE user account (with a name, password, and role).

public class User {                      
    public final String name;           // username
    public final String password;       // password
    public final String role;           // role (user or librarian)

    // Constructor method
    public User(String name, String password, String role) {
        this.name = name;               
        this.password = password;       
        this.role = role;                
    }

    // Method to check if the user is a librarian
    public boolean isLibrarian() {
        return "librarian".equalsIgnoreCase(role); // TRUE if librarian
    }

    // METHOD OVERRIDE: Override Object.toString().
    @Override                               
    public String toString() {
        return "User{name='" + name + "', role='" + role + "'}";
    }
}

