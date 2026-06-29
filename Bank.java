import java.util.ArrayList;

public class Bank {
    private String bankName;
    private ArrayList<Users> users;
    private ArrayList<Account> accounts;
    private ArrayList<Transaction> transactions;
    private static int totalCustomer;
    private static int totalAdmins;
    private static int totalEmployees;
    private static int totalAccount;
    private static int totalTransaction;
    private static int total_Saving_Account;
    private static int total_Checking_Account;

    private static Bank instance;

    private Bank(String bankName) {
        this.bankName = bankName;
        this.users = new ArrayList<>();
        this.accounts = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }

    public static Bank getInstance(String bankName) {
        if (instance == null) {
            instance = new Bank(bankName);
        }
        return instance;
    }

    public static Bank getInstance() { return instance; }

    public void addUser(Users users) {
        this.users.add(users);
        if (users instanceof Customer) {
            totalCustomer++;
        } else if (users instanceof Employee && !(users instanceof Admin)) {
            totalEmployees++;
        } else if (users instanceof Admin) {
            totalAdmins++;
        }
        FileManager.updateAllData(this);
    }

    public void addUserWithoutSave(Users users) {
        this.users.add(users);
        if (users instanceof Customer) totalCustomer++;
        else if (users instanceof Employee && !(users instanceof Admin)) totalEmployees++;
        else if (users instanceof Admin) totalAdmins++;
    }

    public void addAccount(Account account) {
        accounts.add(account);
        if (account instanceof SavingAccount) total_Saving_Account++;
        else total_Checking_Account++;
        totalAccount++;
        FileManager.updateAllData(this);
    }

    public void addAccountWithoutSave(Account account) {
        accounts.add(account);
        if (account instanceof SavingAccount) total_Saving_Account++;
        else total_Checking_Account++;
        totalAccount++;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        totalTransaction++;
        FileManager.updateAllData(this);
    }

    public void addTransactionWithoutSave(Transaction transaction) {
        transactions.add(transaction);
        totalTransaction++;
    }

    public void removeUser(String input) {
        for (int i = 0; i < users.size(); i++) {
            Users user = users.get(i);
            if (input.equalsIgnoreCase(user.getEmail()) || input.equalsIgnoreCase(user.getUser_Name())) {
                if (user instanceof Customer) totalCustomer--;
                else if (user instanceof Employee && !(user instanceof Admin)) totalEmployees--;
                else if (user instanceof Admin) totalAdmins--;
                users.remove(i);
                FileManager.updateAllData(this);
                break;
            }
        }
    }

    public void removeTransaction(long input) {
        for (int i = 0; i < transactions.size(); i++) {
            if (input == transactions.get(i).getTransactionId()) {
                transactions.remove(i);
                totalTransaction--;
                FileManager.updateAllData(this);
                break;
            }
        }
    }

    public Users findUser(String input) {
        for (Users user : users) {
            if (input.equalsIgnoreCase(user.getEmail()) || input.equalsIgnoreCase(user.getUser_Name()) || input.equalsIgnoreCase(user.getUser_ID())) {
                return user;
            }
        }
        return null;
    }

    public Account findAccount(String accountNumber) {
        for (Account account : accounts) {
            if (account.getAccountNumber().equalsIgnoreCase(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    public void setTransactions(ArrayList<Transaction> transactions) { this.transactions = transactions; }

    int getTotalAccounts() { return accounts.size(); }
    public int getTotalCustomers() { return totalCustomer; }
    public int getTotalEmployees() { return totalEmployees; }
    public int getTotalAdmins() { return totalAdmins; }

    double getTotalBalance() {
        double totalBalance = 0;
        for (Account account : accounts) {
            totalBalance += account.getBalance();
        }
        return totalBalance;
    }

    public String getBankName() { return bankName; }
    public ArrayList<Transaction> getTransactions() { return transactions; }
    public ArrayList<Users> getUsers() { return users; }
    public ArrayList<Account> getAccountsList() { return accounts; }

    String displayAllCustomer() {
        StringBuilder sb = new StringBuilder();
        if (totalCustomer == 0) return "No customers";
        for (Users user : users) {
            if (user instanceof Customer) {
                sb.append(((Customer) user)).append("\n");
            }
        }
        return sb.toString();
    }

    String displayAllCustomerWithAccounts() {
        return displayAllCustomer();
    }

    public String displayAllAccounts() {
        StringBuilder sb = new StringBuilder();
        if (accounts.isEmpty()) return "No accounts";
        for (Account account : accounts) {
            sb.append(account).append("\n\n");
        }
        return sb.toString();
    }

    public void report() {
        System.out.println("Bank Report:\n");
        System.out.println("Total accounts: " + getTotalAccounts());
        System.out.println("Total balance: " + getTotalBalance());
        System.out.println("Total Admins: " + totalAdmins);
        System.out.println("Total Employees: " + totalEmployees);
        System.out.println("Total Customers: " + totalCustomer);
    }

    public String generateBankReport() {
        return "Bank Report:\n" +
                "Total accounts: " + getTotalAccounts() + "\n" +
                "Total balance: " + getTotalBalance() + "\n" +
                "Total Admins: " + totalAdmins + "\n" +
                "Total Employees: " + totalEmployees + "\n" +
                "Total Customers: " + totalCustomer;
    }

    public boolean checkEmail(String email) { return findUser(email) != null; }
    public boolean checkAccount(String accountNumber) { return findAccount(accountNumber) != null; }
    public ArrayList<Account> customerAccounts(Customer customer) { return customer.getAccounts(); }
    public void displayCustomerAccounts(Customer customer) {
        ArrayList<Account> accounts = customer.getAccounts();
        if (accounts.isEmpty()) {
            System.out.println("No account for user: " + customer.getName());
            return;
        }
        System.out.println("Accounts:\n");
        for (Account account : accounts) {
            System.out.println(account);
            System.out.println("-------------------------");
        }
    }

    public void applyMonthlyInterestToAllSavings() {
        for (Account acc : accounts) {
            if (acc instanceof SavingAccount) {
                ((SavingAccount) acc).applyMonthlyInterest();
            }
        }
        FileManager.updateAllData(this);
    }
}
