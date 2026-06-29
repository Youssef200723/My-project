import java.util.Scanner;

public class Admin extends Employee {
    private String securityClearance;
    private String adminPrivileges;

    Admin() {}

    public Admin(String name, String email, String password, String employeeId, String position, String securityClearance, String adminPrivileges) {
        super(name, email, password, employeeId, position);
        this.securityClearance = securityClearance;
        this.adminPrivileges = adminPrivileges;
    }

    public Admin(String name, String user_ID, String email, String password,
                 String user_Name, String securityClearance,
                 String adminPrivileges, String employeeId, String position) {
        super(name, user_ID, email, password, user_Name, employeeId, position);
        this.securityClearance = securityClearance;
        this.adminPrivileges = adminPrivileges;
    }

    public void setSecurityClearance(String securityClearance) { this.securityClearance = securityClearance; }
    public String getSecurityClearance() { return securityClearance; }
    public void setAdminPrivileges(String adminPrivileges) { this.adminPrivileges = adminPrivileges; }
    public String getAdminPrivileges() { return adminPrivileges; }

    @Override
    public String getRole() { return "Admin"; }

    @Override
    public String login(String input, String password) { return super.login(input, password); }

    @Override
    public void updateProfile(String name, String email, String password, String user_Name) {
        super.updateProfile(name, email, password, user_Name);
    }

    public void createEmployee(String name, String email, String password, String employeeId, String position) {
        if (Bank.getInstance().findUser(email) != null) {
            System.out.println("Error: Email already exists.");
            return;
        }
        if (password.length() < 6) {
            System.out.println("Password must be at least 6 characters.");
            return;
        }
        Users employee = new Employee(name, email, password, employeeId, position);
        Bank.getInstance().addUser(employee);
        System.out.println("Employee created successfully.");
    }

    public void modifySystemSettings(double interestRate, double minimumBalance, int withdrawLimit) {
        SystemSettings.setInterestRate(interestRate);
        SystemSettings.setMinimumBalance(minimumBalance);
        SystemSettings.setWithdrawLimit(withdrawLimit);
        FileManager.saveSystemSettings();
        System.out.println("System settings updated and saved.");
    }

    public String overrideTransactionLimits(String input, double amount) {
        Account acc = Bank.getInstance().findAccount(input);
        if (acc == null) return "Invalid account";
        if (amount <= 0) return "Amount must be positive";
        try {
            acc.adminWithdraw(amount);
            Transaction t = new Transaction(TransactionType.WITHDRAW, amount, acc);
            t.setStatus(TransactionStatus.SUCCESS);
            acc.addTransaction(t);
            Bank.getInstance().addTransaction(t);
            FileManager.updateAllData(Bank.getInstance());
            return "Override withdraw successful. " + t;
        } catch (Exception e) {
            return "Override failed: " + e.getMessage();
        }
    }

    public void freezeAllCustomerAccounts(Customer customer) {
        if (customer == null) { System.out.println("Customer not found"); return; }
        for (Account account : customer.getAccounts()) {
            account.setStatus(AccountStatus.FROZEN);
        }
        FileManager.updateAllData(Bank.getInstance());
        System.out.println("All accounts for " + customer.getName() + " frozen.");
    }

    public void activeAllCustomerAccounts(Customer customer) {
        if (customer == null) { System.out.println("Customer not found"); return; }
        for (Account account : customer.getAccounts()) {
            account.setStatus(AccountStatus.ACTIVE);
        }
        FileManager.updateAllData(Bank.getInstance());
        System.out.println("All accounts for " + customer.getName() + " activated.");
    }

    public boolean freezeAccount(String accountNumber) {
        Account account = Bank.getInstance().findAccount(accountNumber);
        if (account == null) { System.out.println("Account not found"); return false; }
        account.setStatus(AccountStatus.FROZEN);
        FileManager.updateAllData(Bank.getInstance());
        System.out.println("Account frozen: " + accountNumber);
        return true;
    }

    public boolean activateAccount(String accountNumber) {
        Account account = Bank.getInstance().findAccount(accountNumber);
        if (account == null) { System.out.println("Account not found"); return false; }
        account.setStatus(AccountStatus.ACTIVE);
        FileManager.updateAllData(Bank.getInstance());
        System.out.println("Account activated: " + accountNumber);
        return true;
    }

    public void resetPassword(Users user, String newPassword) {
        if (user.setPassword(newPassword)) {
            FileManager.updateAllData(Bank.getInstance());
            System.out.println("Password reset for " + user.getName());
        } else {
            System.out.println("Password must be at least 6 characters.");
        }
    }

    public void viewAllTransactions() {
        var transactions = Bank.getInstance().getTransactions();
        if (transactions.isEmpty()) {
            System.out.println("No transactions.");
            return;
        }
        for (Transaction t : transactions) {
            System.out.println(t);
        }
    }

    public String generateFullReport() {
        StringBuilder sb = new StringBuilder("===== ADMIN SYSTEM REPORT =====\n");
        sb.append(Bank.getInstance().generateBankReport());
        sb.append("\n--- All Users ---\n");
        for (Users u : Bank.getInstance().getUsers()) {
            sb.append(u.getRole()).append(": ").append(u.getName())
                    .append(" (").append(u.getEmail()).append(")\n");
        }
        return sb.toString();
    }

    @Override
    public void show() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Create employee");
            System.out.println("2. Modify system settings");
            System.out.println("3. Freeze/activate customer accounts");
            System.out.println("4. Reset user password");
            System.out.println("5. View all transactions");
            System.out.println("6. Generate full report");
            System.out.println("7. Apply monthly interest to all savings accounts");
            System.out.println("8. Override transaction limit (withdraw)");
            System.out.println("9. Logout");
            System.out.print("Choose: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    System.out.print("Name: "); String name = scanner.nextLine();
                    System.out.print("Email: "); String email = scanner.nextLine();
                    System.out.print("Password: "); String pwd = scanner.nextLine();
                    System.out.print("Employee ID: "); String empId = scanner.nextLine();
                    System.out.print("Position: "); String pos = scanner.nextLine();
                    createEmployee(name, email, pwd, empId, pos);
                    break;
                case 2:
                    System.out.print("Interest rate (e.g., 0.03): "); double rate = scanner.nextDouble();
                    System.out.print("Minimum balance for savings: "); double minBal = scanner.nextDouble();
                    System.out.print("Monthly withdrawal limit: "); int limit = scanner.nextInt();
                    modifySystemSettings(rate, minBal, limit);
                    break;
                case 3:
                    System.out.print("Customer email/username: "); String cust = scanner.nextLine();
                    Users u = Bank.getInstance().findUser(cust);
                    if (u instanceof Customer) {
                        System.out.print("Freeze (f) or Activate (a): ");
                        String act = scanner.nextLine();
                        if (act.equalsIgnoreCase("f")) freezeAllCustomerAccounts((Customer) u);
                        else activeAllCustomerAccounts((Customer) u);
                    } else System.out.println("Customer not found.");
                    break;
                case 4:
                    System.out.print("User email/username: "); String userTarget = scanner.nextLine();
                    Users target = Bank.getInstance().findUser(userTarget);
                    if (target == null) System.out.println("User not found.");
                    else {
                        System.out.print("New password: "); String newPwd = scanner.nextLine();
                        resetPassword(target, newPwd);
                    }
                    break;
                case 5:
                    viewAllTransactions();
                    break;
                case 6:
                    System.out.println(generateFullReport());
                    break;
                case 7:
                    Bank.getInstance().applyMonthlyInterestToAllSavings();
                    break;
                case 8:
                    System.out.print("Account number: "); String accNum = scanner.nextLine();
                    System.out.print("Amount to withdraw: "); double amt = scanner.nextDouble();
                    System.out.println(overrideTransactionLimits(accNum, amt));
                    break;
                case 9:
                    System.out.println("Logged out.");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    @Override
    public String viewCustomerAccounts(String input) {
        return super.viewCustomerAccounts(input);
    }

    @Override
    public String toString() {
        return "Admin: \nsecurityClearance: " + securityClearance +
                "\nadminPrivileges: " + adminPrivileges +
                "\nname: " + name +
                "\nuser_ID: " + user_ID +
                "\nemail: " + email +
                "\nuser_Name: " + user_Name;
    }
}
