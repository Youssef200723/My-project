import java.util.Scanner;

public class Employee extends Users {
    private String employeeId;
    private String position;

    Employee() {}

    public Employee(String name, String email, String password, String employeeId, String position) {
        super(name, email, password);
        this.employeeId = employeeId;
        this.position = position;
    }

    public Employee(String name, String user_ID, String email, String password, String user_Name, String employeeId, String position) {
        super(name, user_ID, email, password, user_Name);
        this.employeeId = employeeId;
        this.position = position;
    }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String viewCustomerAccounts(String input) {
        Users user = Bank.getInstance().findUser(input);
        if (user == null) return "Invalid customer";
        if (!(user instanceof Customer)) return "Not a customer";
        if (((Customer) user).getAccounts().isEmpty()) return "Customer has no accounts";
        StringBuilder sb = new StringBuilder();
        for (Account acc : ((Customer) user).getAccounts()) {
            sb.append("Balance: ").append(acc.getBalance())
                    .append(", Number: ").append(acc.getAccountNumber())
                    .append(", Status: ").append(acc.getStatus()).append("\n\n");
        }
        return sb.toString();
    }

    public String searchCustomer(String nameOrEmail) {
        Users user = Bank.getInstance().findUser(nameOrEmail);
        if (user == null || !(user instanceof Customer)) return "Customer not found";
        return "Customer found: " + ((Customer) user).displayWithAccount();
    }

    public String generateReport() {
        StringBuilder report = new StringBuilder("--- Employee Report ---\n");
        report.append("Total customers: ").append(Bank.getInstance().getTotalCustomers()).append("\n");
        report.append("Total accounts: ").append(Bank.getInstance().getTotalAccounts()).append("\n");
        return report.toString();
    }

    @Override
    public void show() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Employee Menu ---");
            System.out.println("1. View customer accounts");
            System.out.println("2. Search customer");
            System.out.println("3. Generate report");
            System.out.println("4. Logout");
            System.out.print("Choose: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    System.out.print("Enter customer email or username: ");
                    String input = scanner.nextLine();
                    System.out.println(viewCustomerAccounts(input));
                    break;
                case 2:
                    System.out.print("Enter name or email: ");
                    String keyword = scanner.nextLine();
                    System.out.println(searchCustomer(keyword));
                    break;
                case 3:
                    System.out.println(generateReport());
                    break;
                case 4:
                    System.out.println("Logged out.");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    @Override
    public String login(String input, String password) { return super.login(input, password); }

    @Override
    public String getRole() { return "Employee"; }

    @Override
    public String toString() {
        return "Employee: \nemployeeId: " + employeeId +
                "\nposition: " + position +
                "\nname: " + name +
                "\nuser_ID: " + user_ID +
                "\nemail: " + email +
                "\nuser_Name: " + user_Name;
    }
}
