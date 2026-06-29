import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private static long nextId = 1;
    private long transactionId;
    private String timestamp;
    private TransactionStatus status;
    private TransactionType type;
    private double amount;
    private Account sourceAccount;
    private Account destinationAccount;

    public Transaction(TransactionType type, double amount, Account sourceAccount, Account destinationAccount) {
        this.transactionId = getNextIdAndIncrement();
        this.type = type;
        this.amount = amount;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.status = TransactionStatus.PENDING;
    }

    public Transaction(TransactionType type, double amount, Account account) {
        if (type == TransactionType.TRANSFER) {
            throw new IllegalArgumentException("Use transfer constructor for TRANSFER type");
        }
        this.transactionId = getNextIdAndIncrement();
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.status = TransactionStatus.PENDING;
        if (type == TransactionType.DEPOSIT) {
            this.destinationAccount = account;
            this.sourceAccount = null;
        } else if (type == TransactionType.WITHDRAW) {
            this.sourceAccount = account;
            this.destinationAccount = null;
        }
    }

    public Transaction(double amount, Account sourceAccount, Account destinationAccount) {
        this.transactionId = getNextIdAndIncrement();
        this.type = TransactionType.TRANSFER;
        this.amount = amount;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.status = TransactionStatus.PENDING;
    }

    private static synchronized long getNextIdAndIncrement() { return nextId++; }

    public static void setNextId(long id) { nextId = id; }

    public boolean execute() {
        try {
            switch (type) {
                case DEPOSIT:
                    if (destinationAccount == null) return false;
                    destinationAccount.deposit(amount);
                    destinationAccount.addTransaction(this);
                    status = TransactionStatus.SUCCESS;
                    return true;
                case WITHDRAW:
                    if (sourceAccount == null) return false;
                    sourceAccount.withdraw(amount);
                    sourceAccount.addTransaction(this);
                    status = TransactionStatus.SUCCESS;
                    return true;
                case TRANSFER:
                    if (sourceAccount == null || destinationAccount == null) return false;
                    sourceAccount.withdraw(amount);
                    destinationAccount.deposit(amount);
                    sourceAccount.addTransaction(this);
                    destinationAccount.addTransaction(this);
                    status = TransactionStatus.SUCCESS;
                    return true;
                default:
                    return false;
            }
        } catch (IllegalArgumentException e) {
            status = TransactionStatus.FAILED;
            return false;
        }
    }

    public long getTransactionId() { return transactionId; }
    public String getTimestamp() { return timestamp; }
    public TransactionStatus getStatus() { return status; }
    public TransactionType getType() { return type; }
    public double getAmount() { return amount; }
    public Account getSourceAccount() { return sourceAccount; }
    public Account getDestinationAccount() { return destinationAccount; }
    public void setTransactionId(long transactionId) { this.transactionId = transactionId; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    public void setType(TransactionType type) { this.type = type; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setSourceAccount(Account sourceAccount) { this.sourceAccount = sourceAccount; }
    public void setDestinationAccount(Account destinationAccount) { this.destinationAccount = destinationAccount; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Transactions: ");
        sb.append("id=").append(transactionId);
        sb.append(", time=").append(timestamp);
        sb.append(", status=").append(status);
        sb.append(", type=").append(type);
        sb.append(", amount=").append(amount);
        if (sourceAccount != null) sb.append(", from=").append(sourceAccount.getAccountNumber());
        if (destinationAccount != null) sb.append(", to=").append(destinationAccount.getAccountNumber());
        return sb.toString();
    }
}
