import java.util.UUID;

public abstract class Users {
    protected String name;
    protected String user_ID;
    protected String email;
    protected String password;
    protected String user_Name;
    private boolean isLogin;

    Users() {}

    public Users(String name, String email, String password) {
        this.name = name;
        this.user_ID = UUID.randomUUID().toString();
        setEmail(email);
        setPassword(password);
        setUser_Name();
        this.isLogin = true;
    }

    public Users(String name, String user_ID, String email, String password, String user_Name) {
        this.name = name;
        this.user_ID = user_ID;
        this.email = email;
        this.password = password;
        this.user_Name = user_Name;
        this.isLogin = true;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUser_ID() { return user_ID; }
    public String getEmail() { return email; }
    public boolean setEmail(String email) {
        if (email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            this.email = email;
            return true;
        }
        return false;
    }
    public String getPassword() { return password; }
    public String getUser_Name() { return user_Name; }
    public void setUser_Name() {
        String namePart = (this.name.length() >= 3) ? this.name.substring(0, 3) : this.name;
        String ID = user_ID.substring(user_ID.length() - 3);
        this.user_Name = namePart + "@" + ID;
    }
    public boolean setPassword(String password) {
        if (password == null || password.length() < 6) return false;
        this.password = password;
        return true;
    }
    public String login(String input, String password) {
        if (!(this.email.equals(input) || this.user_Name.equals(input))) {
            return "Your email or username is incorrect";
        } else if (!this.password.equals(password)) {
            return "Your password is incorrect";
        }
        return "Hello";
    }
    abstract void show();
    public void updateProfile(String name, String email, String password, String user_Name) {
        this.name = name;
        this.email = email;
        setPassword(password);
        this.user_Name = user_Name;
    }
    public void logout() {}
    public abstract String getRole();
    @Override
    public String toString() {
        return "Users{name='" + name + "', user_ID='" + user_ID + "', email='" + email + "', password='" + password + "', user_Name='" + user_Name + "'}";
    }
}
