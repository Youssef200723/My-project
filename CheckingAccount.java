public class CheckingAccount extends Account {
    public CheckingAccount(double balance, Customer customer) {
        super(balance, customer);
    }

    @Override
    void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid withdrawal amount");
        } else if (this.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException("Account is not ACTIVE");
        } else if (getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        setBalance(getBalance() - amount);
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

    @Override
    public void deposit(double amount) {
        super.deposit(amount);
    }

    @Override
    public String toString() {
        return "Type: CheckingAccount\n" +
                "AccountNumber: " + getAccountNumber() + "\n" +
                "Balance: " + getBalance() + "\n" +
                "Status: " + getStatus() + "\n" +
                "Owner: " + getOwner().getName() + "\n";
    }
}
