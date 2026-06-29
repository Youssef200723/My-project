import java.io.*;
import java.util.*;

public class FileManager {
    private static final String USERS_FILE = "users.txt";
    private static final String ACCOUNTS_FILE = "accounts.txt";
    private static final String TRANSACTIONS_FILE = "transactions.txt";
    private static final String SETTINGS_FILE = "settings.txt";
    private static boolean savingEnabled = true;

    public static void enableSaving() { savingEnabled = true; }
    public static void disableSaving() { savingEnabled = false; }

    public static void updateAllData(Bank bank) {
        if (!savingEnabled) return;
        synchronized (FileManager.class) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE, false))) {
                for (Users u : bank.getUsers()) pw.println(formatUser(u));
            } catch (IOException e) { e.printStackTrace(); }

            try (PrintWriter pw = new PrintWriter(new FileWriter(ACCOUNTS_FILE, false))) {
                for (Account acc : bank.getAccountsList()) pw.println(formatAccount(acc));
            } catch (IOException e) { e.printStackTrace(); }

            try (PrintWriter pw = new PrintWriter(new FileWriter(TRANSACTIONS_FILE, false))) {
                for (Transaction t : bank.getTransactions()) pw.println(formatTransaction(t));
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    public static void saveUsers(Users users) {
        if (!savingEnabled) return;
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE, true))) {
            pw.println(formatUser(users));
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void saveAccounts(Account account) {
        if (!savingEnabled) return;
        try (PrintWriter pw = new PrintWriter(new FileWriter(ACCOUNTS_FILE, true))) {
            pw.println(formatAccount(account));
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void saveTransactions(Transaction transaction) {
        if (!savingEnabled) return;
        try (PrintWriter pw = new PrintWriter(new FileWriter(TRANSACTIONS_FILE, true))) {
            pw.println(formatTransaction(transaction));
        } catch (IOException e) { e.printStackTrace(); }
    }

    // ------------------- إضافات لحفظ إعدادات النظام -------------------
    public static void saveSystemSettings() {
        if (!savingEnabled) return;
        try (PrintWriter pw = new PrintWriter(new FileWriter(SETTINGS_FILE, false))) {
            pw.println(SystemSettings.getInterestRate());
            pw.println(SystemSettings.getMinimumBalance());
            pw.println(SystemSettings.getWithdrawLimit());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadSystemSettings() {
        File file = new File(SETTINGS_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            if (line != null) SystemSettings.setInterestRate(Double.parseDouble(line));
            line = br.readLine();
            if (line != null) SystemSettings.setMinimumBalance(Double.parseDouble(line));
            line = br.readLine();
            if (line != null) SystemSettings.setWithdrawLimit(Integer.parseInt(line));
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
    // ----------------------------------------------------------------

    public static List<Users> loadUsers() {
        List<Users> list = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (!file.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Users u = parseUser(line);
                if (u != null) list.add(u);
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    public static List<Account> loadAccounts(Bank bank) {
        List<Account> list = new ArrayList<>();
        File file = new File(ACCOUNTS_FILE);
        if (!file.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Account acc = parseAccount(line, bank);
                if (acc != null) list.add(acc);
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    public static List<Transaction> loadTransactions(Bank bank) {
        List<Transaction> list = new ArrayList<>();
        File file = new File(TRANSACTIONS_FILE);
        if (!file.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Transaction t = parseTransaction(line, bank);
                if (t != null) list.add(t);
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    private static String formatUser(Users u) {
        String type = u.getRole();
        StringBuilder sb = new StringBuilder();
        sb.append(type).append("|");
        sb.append(u.getName()).append("|");
        sb.append(u.getUser_ID()).append("|");
        sb.append(u.getEmail()).append("|");
        sb.append(u.getPassword()).append("|");
        sb.append(u.getUser_Name()).append("|");
        if (u instanceof Customer) {
            sb.append("|").append(((Customer) u).getAddress());
        } else if (u instanceof Employee && !(u instanceof Admin)) {
            Employee e = (Employee) u;
            sb.append(e.getEmployeeId()).append("|");
            sb.append(e.getPosition());
        } else if (u instanceof Admin) {
            Admin a = (Admin) u;
            sb.append(a.getSecurityClearance()).append("|");
            sb.append(a.getAdminPrivileges()).append("|");
            sb.append(a.getEmployeeId()).append("|");
            sb.append(a.getPosition());
        }
        return sb.toString();
    }

    private static String formatAccount(Account acc) {
        String type = (acc instanceof SavingAccount) ? "SAVING" : "CHECKING";
        StringBuilder sb = new StringBuilder();
        sb.append(type).append("|");
        sb.append(acc.getAccountNumber()).append("|");
        sb.append(acc.getBalance()).append("|");
        sb.append(acc.getStatus()).append("|");
        sb.append(acc.getOwner().getUser_ID());
        if (acc instanceof SavingAccount) {
            SavingAccount sa = (SavingAccount) acc;
            sb.append("|").append(sa.getInterestRate());
            sb.append("|").append(sa.getMinimumBalance());
            sb.append("|").append(sa.getWithdrawIli());
            sb.append("|").append(sa.getWithdraw_this_month());
            sb.append("|").append(sa.getLastResetMonth());
        }
        return sb.toString();
    }

    private static String formatTransaction(Transaction t) {
        StringBuilder sb = new StringBuilder();
        sb.append(t.getTransactionId()).append("|");
        sb.append(t.getTimestamp()).append("|");
        sb.append(t.getStatus()).append("|");
        sb.append(t.getType()).append("|");
        sb.append(t.getAmount()).append("|");
        sb.append(t.getSourceAccount() == null ? "null" : t.getSourceAccount().getAccountNumber()).append("|");
        sb.append(t.getDestinationAccount() == null ? "null" : t.getDestinationAccount().getAccountNumber());
        return sb.toString();
    }

    private static Users parseUser(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 6) return null;
        String type = parts[0];
        String name = parts[1];
        String user_ID = parts[2];
        String email = parts[3];
        String password = parts[4];
        String user_Name = parts[5];
        try {
            switch (type) {
                case "Customer":
                    String address = parts[6];
                    return new Customer(name, user_ID, email, password, user_Name, address, new ArrayList<>());
                case "Employee":
                    String empId = parts[6];
                    String position = parts[7];
                    return new Employee(name, user_ID, email, password, user_Name, empId, position);
                case "Admin":
                    String security = parts[6];
                    String privileges = parts[7];
                    String empIdAdmin = parts[8];
                    String posAdmin = parts[9];
                    return new Admin(name, user_ID, email, password, user_Name, security, privileges, empIdAdmin, posAdmin);
                default: return null;
            }
        } catch (Exception e) { return null; }
    }

    private static Account parseAccount(String line, Bank bank) {
        String[] parts = line.split("\\|");
        if (parts.length < 5) return null;
        String type = parts[0];
        String accNum = parts[1];
        double balance = Double.parseDouble(parts[2]);
        AccountStatus status = AccountStatus.valueOf(parts[3]);
        String ownerId = parts[4];
        Customer owner = (Customer) bank.findUser(ownerId);
        if (owner == null) return null;
        Account acc = null;
        if (type.equals("CHECKING")) {
            acc = new CheckingAccount(balance, owner);
        } else if (type.equals("SAVING") && parts.length >= 10) {
            double interest = Double.parseDouble(parts[5]);
            double minBal = Double.parseDouble(parts[6]);
            int limit = Integer.parseInt(parts[7]);
            int used = Integer.parseInt(parts[8]);
            int lastReset = Integer.parseInt(parts[9]);
            acc = new SavingAccount(balance, owner, interest, minBal, limit, used, lastReset);
        }
        if (acc != null) {
            acc.setStatus(status);
            acc.setAccountNumber(accNum);
        }
        return acc;
    }

    private static Transaction parseTransaction(String line, Bank bank) {
        String[] parts = line.split("\\|");
        if (parts.length < 7) return null;
        try {
            long id = Long.parseLong(parts[0]);
            String time = parts[1];
            TransactionStatus status = TransactionStatus.valueOf(parts[2]);
            TransactionType type = TransactionType.valueOf(parts[3]);
            double amount = Double.parseDouble(parts[4]);
            String sourceNum = parts[5];
            String destNum = parts[6];
            Account source = sourceNum.equals("null") ? null : bank.findAccount(sourceNum);
            Account dest = destNum.equals("null") ? null : bank.findAccount(destNum);
            Transaction t = new Transaction(type, amount, source, dest);
            t.setTransactionId(id);
            t.setStatus(status);
            t.setTimestamp(time);
            return t;
        } catch (Exception e) { return null; }
    }
}
