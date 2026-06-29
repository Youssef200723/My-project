import java.util.ArrayList;
import java.util.List;

public abstract class Account {
    private String accountNumber;
    private double balance;
    private AccountStatus status;
    private Customer owner;
    private List<Transaction> transactions;
    private static int checkingAccount = 100000;
    private static int savingAccount = 200000;

    public Account(double balance, Customer customer) {
        this.balance = balance;
        setAccountNumber1();
        this.status = AccountStatus.ACTIVE;
        this.owner = customer;
        this.transactions = new ArrayList<>();
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid deposit amount");
        }
        if (status == AccountStatus.FROZEN) {
            throw new IllegalArgumentException("Account is frozen");
        }
        this.balance += amount;
    }

    abstract void withdraw(double amount);
    public abstract void adminWithdraw(double amount);

    public double getBalance() { return balance; }
    public AccountStatus getStatus() { return status; }

    public void setAccountNumber1() {
        if (this instanceof SavingAccount) {
            this.accountNumber = "SAV" + (++savingAccount);
        } else {
            this.accountNumber = "CHK" + (++checkingAccount);
        }
    }

    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public Customer getOwner() { return owner; }
    public String getName() { return owner.getName(); }
    public String getAccountNumber() { return accountNumber; }
    public void setStatus(AccountStatus status) { this.status = status; }
    public List<Transaction> getTransactions() { return transactions; }
    public void addTransaction(Transaction transaction) { this.transactions.add(transaction); }
    public void setBalance(double balance) { this.balance = balance; }
    public static void setCheckingCounter(int value) { checkingAccount = value; }
    public static void setSavingCounter(int value) { savingAccount = value; }

    @Override
    public String toString() {
        return "AccountNumber: " + accountNumber + "\n" +
                "Balance: " + balance + "\n" +
                "Status: " + status + "\n" +
                "Owner: " + getOwner().getName() + "\n";
    }
}
