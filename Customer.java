import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Customer extends Users {
    private String address;
    private ArrayList<Account> accounts = new ArrayList<>();

    Customer() { super(); }

    public Customer(String name, String email, String password, String address) {
        super(name, email, password);
        this.address = address;
    }

    public Customer(String name, String user_ID, String email, String password, String user_Name, String address, ArrayList<Account> accounts) {
        super(name, user_ID, email, password, user_Name);
        this.address = address;
        this.accounts = accounts;
    }

    public void setAddress(String address) { this.address = address; }
    public String getAddress() { return address; }
    public ArrayList<Account> getAccounts() { return accounts; }
    public void setAccounts(ArrayList<Account> accounts) { this.accounts = accounts; }

    public String openAccount(String type, double deposit) {
        Account account;
        if (type.equalsIgnoreCase("checking")) {
            account = new CheckingAccount(0, this);
        } else if (type.equalsIgnoreCase("saving")) {
            if (deposit < SystemSettings.minimumBalance) {
                return "Minimum balance required for saving: " + SystemSettings.minimumBalance;
            }
            account = new SavingAccount(0, this);
        } else {
            return "Invalid account type";
        }

        Transaction initialDeposit = new Transaction(TransactionType.DEPOSIT, deposit, account);
        if (!initialDeposit.execute()) {
            return "Failed to deposit initial amount. Account not created.";
        }
        this.accounts.add(account);
        Bank.getInstance().addAccount(account);
        return "Account opened. Account number: " + account.getAccountNumber();
    }

    @Override
    public void updateProfile(String name, String email, String password, String user_Name) {
        super.updateProfile(name, email, password, user_Name);
        Bank.getInstance().addUser(this);
        FileManager.updateAllData(Bank.getInstance());
    }

    @Override
    public String login(String input, String password) {
        return super.login(input, password);
    }

    @Override
    public void show() { }

    public void show(Scanner scanner) {
        if (accounts.isEmpty()) {
            openFirstAccount(scanner);
        } else {
            mainMenu(scanner);
        }
    }

    private void openFirstAccount(Scanner scanner) {
        System.out.println("Open your first account.\nSaving Account enter: 1\nChecking Account enter: 2");
        int type = scanner.nextInt();
        if (type == 1) {
            System.out.println("Deposit amount > " + SystemSettings.minimumBalance);
            double amount;
            while (true) {
                amount = scanner.nextDouble();
                if (amount < SystemSettings.minimumBalance) {
                    System.out.println("Amount must be at least " + SystemSettings.minimumBalance);
                    continue;
                }
                break;
            }
            System.out.println(openAccount("saving", amount));
        } else if (type == 2) {
            System.out.println(openAccount("checking", 0));
        } else {
            System.out.println("Wrong selection");
            show(scanner);
            return;
        }
        afterAccounts(scanner);
    }

    private void afterAccounts(Scanner scanner) {
        System.out.println("To use account enter: 1\nTo open another account enter: 2");
        int input = scanner.nextInt();
        if (input == 2) {
            System.out.println("Saving enter: 1\nChecking enter: 2");
            int type2 = scanner.nextInt();
            if (type2 == 1) {
                System.out.println("Deposit amount > " + SystemSettings.minimumBalance);
                double amount;
                while (true) {
                    amount = scanner.nextDouble();
                    if (amount < SystemSettings.minimumBalance) {
                        System.out.println("Amount must be at least " + SystemSettings.minimumBalance);
                        continue;
                    }
                    break;
                }
                System.out.println(openAccount("saving", amount));
            } else if (type2 == 2) {
                System.out.println(openAccount("checking", 0));
            } else {
                System.out.println("Wrong selection");
            }
            show(scanner);
        } else if (input == 1) {
            mainMenu(scanner);
        } else {
            System.out.println("Wrong selection");
            show(scanner);
        }
    }

    private void mainMenu(Scanner scanner) {
        System.out.println("Enter your account number:");
        String accountNum;
        while (true) {
            accountNum = scanner.next();
            if (Bank.getInstance().findAccount(accountNum) == null) {
                System.out.println("Account not found. Try again:");
                continue;
            }
            break;
        }
        while (true) {
            System.out.println("To deposit:1\nTo withdraw:2\nTo transfer:3\nLast 10 transactions:4\nUpdate profile:5\nLogout:6");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    performDeposit(accountNum, scanner);
                    break;
                case 2:
                    performWithdraw(accountNum, scanner);
                    break;
                case 3:
                    performTransfer(accountNum, scanner);
                    break;
                case 4:
                    showLastTransactions(accountNum);
                    break;
                case 5:
                    updateProfileMenu(scanner);
                    break;
                case 6:
                    System.out.println("Goodbye");
                    return;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    private void performDeposit(String accountNum, Scanner scanner) {
        try {
            System.out.println("Enter amount:");
            double amount = scanner.nextDouble();
            Account acc = Bank.getInstance().findAccount(accountNum);
            Transaction t = new Transaction(TransactionType.DEPOSIT, amount, acc);
            t.execute();
            if (t.getStatus() == TransactionStatus.SUCCESS) {
                Bank.getInstance().addTransaction(t);
                System.out.println("Deposit successful");
            } else {
                System.out.println("Deposit failed");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void performWithdraw(String accountNum, Scanner scanner) {
        try {
            System.out.println("Enter amount:");
            double amount = scanner.nextDouble();
            Account acc = Bank.getInstance().findAccount(accountNum);
            Transaction t = new Transaction(TransactionType.WITHDRAW, amount, acc);
            t.execute();
            if (t.getStatus() == TransactionStatus.SUCCESS) {
                Bank.getInstance().addTransaction(t);
                System.out.println("Withdrawal successful");
            } else {
                System.out.println("Withdrawal failed");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void performTransfer(String sourceNum, Scanner scanner) {
        try {
            System.out.println("Enter destination account number:");
            String destNum = scanner.next();
            if (destNum.equals(sourceNum)) {
                System.out.println("Error: You cannot transfer money to the same account.");
                return;
            }
            if (Bank.getInstance().findAccount(destNum) == null) {
                System.out.println("Error: Destination account not found.");
                return;
            }
            System.out.println("Enter amount:");
            double amount = scanner.nextDouble();
            Account source = Bank.getInstance().findAccount(sourceNum);
            Account dest = Bank.getInstance().findAccount(destNum);
            Transaction t = new Transaction(amount, source, dest);
            if (t.execute()) {
                Bank.getInstance().addTransaction(t);
                System.out.println("Transfer successful");
            } else {
                System.out.println("Transfer failed");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void showLastTransactions(String accountNum) {
        Account acc = Bank.getInstance().findAccount(accountNum);
        List<Transaction> trans = acc.getTransactions();
        int start = Math.max(0, trans.size() - 10);
        for (int i = trans.size() - 1; i >= start; i--) {
            System.out.println(trans.get(i));
        }
    }

    private void updateProfileMenu(Scanner scanner) {
        System.out.println("Enter new name:");
        String name = scanner.next().trim();
        System.out.println("Enter new email:");
        String email = scanner.next().trim();
        System.out.println("Enter new password:");
        String password = scanner.next().trim();
        System.out.println("Enter new username:");
        String userName = scanner.next();
        this.updateProfile(name, email, password, userName);
        System.out.println("Profile updated");
    }

    @Override
    public String toString() {
        return "Customer: \naddress='" + address +
                "\naccounts=" + accounts +
                "\nname='" + name +
                "\nuser_ID='" + user_ID +
                "\nemail='" + email +
                "\npassword='" + password +
                "\nuser_Name='" + user_Name;
    }

    @Override
    public String getRole() { return "Customer"; }

    public String displayWithAccount() {
        return "Address: " + address +
                "\nName: " + name +
                "\nEmail: " + email +
                "\nUser_Name: " + user_Name +
                "\nAccount: " + accounts.toString();
    }
}