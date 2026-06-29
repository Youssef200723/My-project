import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static Bank bank = Bank.getInstance("Bank");

    public static void main(String[] args) throws IllegalArgumentException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadAllData();

        if (bank.findUser("yoy22@gmail.com") == null) {
            Users admin = new Admin("Youssef", "yoy22@gmail.com", "123456", "AD1", "CEO", "High", "All");
            bank.addUser(admin);
        }

        SwingUtilities.invokeLater(() -> {
            new BankingSystemGUI().setVisible(true);
        });
    }

    private static void loadAllData() {
        FileManager.disableSaving();


        FileManager.loadSystemSettings();

        List<Users> users = FileManager.loadUsers();
        for (Users u : users) {
            bank.addUserWithoutSave(u);
        }

        List<Account> accounts = FileManager.loadAccounts(bank);
        for (Account a : accounts) {
            bank.addAccountWithoutSave(a);
            Customer owner = a.getOwner();
            if (owner != null) {
                owner.getAccounts().add(a);
            }
        }

        List<Transaction> transactions = FileManager.loadTransactions(bank);
        for (Transaction t : transactions) {
            bank.addTransactionWithoutSave(t);
        }


        int maxChecking = 0, maxSaving = 0;
        for (Account a : accounts) {
            String num = a.getAccountNumber();
            if (num.startsWith("CHK")) {
                try {
                    int val = Integer.parseInt(num.substring(3));
                    if (val > maxChecking) maxChecking = val;
                } catch (NumberFormatException ignored) {}
            } else if (num.startsWith("SAV")) {
                try {
                    int val = Integer.parseInt(num.substring(3));
                    if (val > maxSaving) maxSaving = val;
                } catch (NumberFormatException ignored) {}
            }
        }
        Account.setCheckingCounter(maxChecking);
        Account.setSavingCounter(maxSaving);


        long maxId = 0;
        for (Transaction t : transactions) {
            if (t.getTransactionId() > maxId) maxId = t.getTransactionId();
        }
        Transaction.setNextId(maxId + 1);

        FileManager.enableSaving();
    }
}