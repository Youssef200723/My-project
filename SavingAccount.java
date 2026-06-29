import java.time.LocalDate;

public class SavingAccount extends Account {
    private double interestRate;
    private double minimumBalance;
    private int withdrawLimit;
    private int withdrawThisMonth;
    private int lastResetMonth;

    public SavingAccount(double balance, Customer customer) {
        super(balance, customer);
        this.interestRate = SystemSettings.getInterestRate();
        this.minimumBalance = SystemSettings.getMinimumBalance();
        this.withdrawLimit = SystemSettings.getWithdrawLimit();
        this.withdrawThisMonth = 0;
        this.lastResetMonth = LocalDate.now().getMonthValue();
    }

    public SavingAccount(double balance, Customer customer, double interestRate, double minimumBalance, int withdrawLimit, int withdrawThisMonth, int lastResetMonth) {
        super(balance, customer);
        this.interestRate = interestRate;
        this.minimumBalance = minimumBalance;
        this.withdrawLimit = withdrawLimit;
        this.withdrawThisMonth = withdrawThisMonth;
        this.lastResetMonth = lastResetMonth;
    }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }
    public double getMinimumBalance() { return minimumBalance; }
    public void setMinimumBalance(double minimumBalance) { this.minimumBalance = minimumBalance; }
    public int getWithdrawIli() { return withdrawLimit; }
    public void setWithdrawIli(int withdrawLimit) { this.withdrawLimit = withdrawLimit; }
    public int getWithdraw_this_month() { return withdrawThisMonth; }
    public void setWithdraw_this_month(int withdrawThisMonth) { this.withdrawThisMonth = withdrawThisMonth; }
    public int getLastResetMonth() { return lastResetMonth; }
    public void setLastResetMonth(int lastResetMonth) { this.lastResetMonth = lastResetMonth; }

    private void checkAndResetMonthlyCounter() {
        int currentMonth = LocalDate.now().getMonthValue();
        if (currentMonth != lastResetMonth) {
            withdrawThisMonth = 0;
            lastResetMonth = currentMonth;
        }
    }

    @Override
    public void deposit(double amount) { super.deposit(amount); }

    @Override
    void withdraw(double amount) {
        checkAndResetMonthlyCounter();
        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid withdrawal amount");
        }
        if (getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        if (getBalance() - amount < this.minimumBalance) {
            throw new IllegalArgumentException("Minimum balance would be violated");
        }
        if (withdrawThisMonth >= withdrawLimit) {
            throw new IllegalArgumentException("Monthly withdrawal limit reached");
        }
        if (getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException("Account is not ACTIVE");
        }
        setBalance(getBalance() - amount);
        withdrawThisMonth++;
    }

    public void adminWithdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid withdrawal amount");
        } else if (getStatus() != AccountStatus.ACTIVE && getStatus() != AccountStatus.FROZEN) {
            throw new IllegalArgumentException("Account is not eligible for admin override");
        }
        if (getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        setBalance(getBalance() - amount);
    }

    double calculateInterest() {
        return getBalance() * SystemSettings.getInterestRate();
    }

    public void applyMonthlyInterest() {
        double monthlyInterest = getBalance() * (SystemSettings.getInterestRate() / 12);
        setBalance(getBalance() + monthlyInterest);
        System.out.println("Interest added: " + monthlyInterest + " to account " + getAccountNumber());
    }

    @Override
    public void addTransaction(Transaction transaction) { super.addTransaction(transaction); }

    @Override
    public String toString() {
        return "Type: SavingAccount\n" +
                "AccountNumber: " + getAccountNumber() + "\n" +
                "Balance: " + getBalance() + "\n" +
                "Status: " + getStatus() + "\n" +
                "Owner: " + getOwner().getName() + "\n";
    }
}
