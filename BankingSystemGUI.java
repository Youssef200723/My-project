import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class BankingSystemGUI extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private Color darkGreen = new Color(24, 69, 49);
    private Color creamBg = new Color(248, 247, 242);
    private Color gold = new Color(191, 150, 64);
    private Account currentSelectedAccount = null;
    private JPanel dashboardPanel = null;
    private Timer transactionTimer = null;

    public BankingSystemGUI() {
        setTitle("AIU Bank Management System");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        mainPanel.add(createLoginScreen(), "LOGIN");
        mainPanel.add(createSignUpScreen(), "SIGNUP");
        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }


    private JPanel createLoginScreen() {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        JPanel left = new JPanel(new GridBagLayout());
        left.setBackground(darkGreen);
        JLabel logo = new JLabel("AIU BANK", SwingConstants.CENTER);
        logo.setFont(new Font("SansSerif", Font.BOLD, 36));
        logo.setForeground(gold);
        left.add(logo);
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(creamBg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField userTxt = new JTextField(20);
        JPasswordField passTxt = new JPasswordField(20);
        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setBackground(darkGreen);
        loginBtn.setForeground(gold);
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        JButton signUpBtn = new JButton("REGISTER NEW ACCOUNT");
        signUpBtn.setBackground(gold);
        signUpBtn.setForeground(darkGreen);
        signUpBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0; right.add(new JLabel("Welcome Back,"), gbc);
        gbc.gridy = 1; right.add(new JLabel("Email or Username:"), gbc);
        gbc.gridy = 2; right.add(userTxt, gbc);
        gbc.gridy = 3; right.add(new JLabel("Password:"), gbc);
        gbc.gridy = 4; right.add(passTxt, gbc);
        gbc.gridy = 5; gbc.gridwidth = 1; right.add(loginBtn, gbc);
        gbc.gridy = 6; right.add(signUpBtn, gbc);
        loginBtn.addActionListener(e -> {
            Users user = Main.bank.findUser(userTxt.getText());
            if (user != null && user.getPassword().equals(new String(passTxt.getPassword()))) {
                showDashboard(user);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        signUpBtn.addActionListener(e -> cardLayout.show(mainPanel, "SIGNUP"));
        panel.add(left);
        panel.add(right);
        return panel;
    }


    private JPanel createSignUpScreen() {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        JPanel left = new JPanel(new GridBagLayout());
        left.setBackground(darkGreen);
        JLabel title = new JLabel("JOIN AIU BANK", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(gold);
        left.add(title);
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(creamBg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JPasswordField passField = new JPasswordField(20);
        JTextField addrField = new JTextField(20);
        JButton registerBtn = new JButton("CONFIRM REGISTRATION");
        registerBtn.setBackground(darkGreen);
        registerBtn.setForeground(gold);
        registerBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        JButton backBtn = new JButton("BACK TO LOGIN");
        backBtn.setBackground(Color.WHITE);
        backBtn.setForeground(darkGreen);
        gbc.gridx = 0; gbc.gridy = 0; right.add(new JLabel("Create New Profile"), gbc);
        gbc.gridy = 1; right.add(new JLabel("Full Name:"), gbc);
        gbc.gridy = 2; right.add(nameField, gbc);
        gbc.gridy = 3; right.add(new JLabel("Email Address:"), gbc);
        gbc.gridy = 4; right.add(emailField, gbc);
        gbc.gridy = 5; right.add(new JLabel("Password (min 6 chars):"), gbc);
        gbc.gridy = 6; right.add(passField, gbc);
        gbc.gridy = 7; right.add(new JLabel("Home Address:"), gbc);
        gbc.gridy = 8; right.add(addrField, gbc);
        gbc.gridy = 9; gbc.gridwidth = 1; right.add(registerBtn, gbc);
        gbc.gridy = 10; right.add(backBtn, gbc);
        registerBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String pass = new String(passField.getPassword()).trim();
            String addr = addrField.getText().trim();
            if (name.isEmpty() || email.isEmpty() || pass.length() < 6) {
                JOptionPane.showMessageDialog(this, "Invalid input. Password must be at least 6 characters.");
                return;
            }
            if (Main.bank.checkEmail(email)) {
                JOptionPane.showMessageDialog(this, "Email is already registered!");
                return;
            }
            Users newUser = new Customer(name, email, pass, addr);
            Main.bank.addUser(newUser);
            JOptionPane.showMessageDialog(this, "Registration Success! Username: " + newUser.getUser_Name());
            nameField.setText("");
            emailField.setText("");
            passField.setText("");
            addrField.setText("");
            cardLayout.show(mainPanel, "LOGIN");
        });
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        panel.add(left);
        panel.add(right);
        return panel;
    }


    private void showDashboard(Users user) {
        if (transactionTimer != null) transactionTimer.stop();
        if (dashboardPanel != null) mainPanel.remove(dashboardPanel);

        dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBackground(creamBg);


        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, gold));
        JLabel userLbl = new JLabel("  Active User: " + user.getName() + " | Role: " + user.getRole() + "  ");
        userLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        userLbl.setForeground(darkGreen);
        header.add(userLbl, BorderLayout.EAST);
        dashboardPanel.add(header, BorderLayout.NORTH);


        if (user instanceof Customer) {

            dashboardPanel.add(createCustomerPanel((Customer) user), BorderLayout.CENTER);
        } else if (user instanceof Admin) {
            dashboardPanel.add(createAdminPanel((Admin) user), BorderLayout.CENTER);
        } else if (user instanceof Employee) {
            dashboardPanel.add(createEmployeePanel((Employee) user), BorderLayout.CENTER);
        }

        mainPanel.add(dashboardPanel, "DASH");
        cardLayout.show(mainPanel, "DASH");
    }


    private JPanel createCustomerPanel(Customer cust) {
        JPanel main = new JPanel(new BorderLayout());
        JPanel sidebar = new JPanel(new GridLayout(10, 1, 0, 5));
        sidebar.setBackground(darkGreen);
        sidebar.setPreferredSize(new Dimension(220, 0));
        JPanel contentCardPanel = new JPanel(new CardLayout());
        CardLayout contentLayout = (CardLayout) contentCardPanel.getLayout();

        String[] menuItems = {"Dashboard", "My Accounts", "Transfer", "Transactions", "Profile", "Logout"};
        for (String item : menuItems) {
            JButton btn = new JButton(item);
            btn.setForeground(gold);
            btn.setBackground(darkGreen);
            btn.setFont(new Font("SansSerif", Font.BOLD, 14));
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.addActionListener(e -> {
                if (item.equals("Logout")) {
                    if (transactionTimer != null) transactionTimer.stop();
                    currentSelectedAccount = null;
                    cardLayout.show(mainPanel, "LOGIN");
                } else if (item.equals("Dashboard") || item.equals("My Accounts") || item.equals("Profile")) {
                    contentLayout.show(contentCardPanel, item.toUpperCase());
                    if (item.equals("Dashboard")) refreshDashboardContent(contentCardPanel, cust);
                } else {
                    if (currentSelectedAccount == null) {
                        JOptionPane.showMessageDialog(this, "Please go to 'My Accounts' and select an account first.");
                    } else {
                        contentLayout.show(contentCardPanel, item.toUpperCase());
                    }
                }
            });
            sidebar.add(btn);
        }
        main.add(sidebar, BorderLayout.WEST);

        contentCardPanel.add(createCustomerHome(cust), "DASHBOARD");
        contentCardPanel.add(createAccountsPanel(cust, contentCardPanel, contentLayout), "MY ACCOUNTS");
        contentCardPanel.add(createTransferPanel(cust), "TRANSFER");
        contentCardPanel.add(createTransactionsPanel(cust), "TRANSACTIONS");
        contentCardPanel.add(createProfilePanel(cust), "PROFILE");
        main.add(contentCardPanel, BorderLayout.CENTER);
        return main;
    }

    private void refreshDashboardContent(JPanel contentCardPanel, Customer cust) {
        Component existing = null;
        for (Component comp : contentCardPanel.getComponents()) {
            if (comp instanceof JPanel && ((JPanel) comp).getClientProperty("dashboard") != null) {
                existing = comp;
                break;
            }
        }
        JPanel newDashboard = createCustomerHome(cust);
        newDashboard.putClientProperty("dashboard", true);
        if (existing != null) contentCardPanel.remove(existing);
        contentCardPanel.add(newDashboard, "DASHBOARD");
        ((CardLayout) contentCardPanel.getLayout()).show(contentCardPanel, "DASHBOARD");
    }

    private JPanel createCustomerHome(Customer cust) {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.setBackground(creamBg);
        JPanel stats = new JPanel(new GridLayout(1, 3, 20, 0));
        stats.setBackground(creamBg);
        double total = 0;
        if (cust.getAccounts() != null) {
            for (Account a : cust.getAccounts()) total += a.getBalance();
        }
        stats.add(createCard("Total Balance", total + " EGP"));
        stats.add(createCard("Active Accounts", String.valueOf(cust.getAccounts() != null ? cust.getAccounts().size() : 0)));
        stats.add(createCard("Status", "Verified"));
        p.add(stats, BorderLayout.NORTH);
        JTextArea welcomeArea = new JTextArea("\n\n   Welcome to AIU Bank Dashboard.\n   To begin, please navigate to 'My Accounts' to select an account.");
        welcomeArea.setFont(new Font("SansSerif", Font.PLAIN, 18));
        welcomeArea.setBackground(creamBg);
        welcomeArea.setEditable(false);
        p.add(new JScrollPane(welcomeArea), BorderLayout.CENTER);
        return p;
    }

    private JPanel createAccountsPanel(Customer cust, JPanel contentCardPanel, CardLayout contentLayout) {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.setBackground(creamBg);
        String[] cols = {"Account Number", "Type", "Balance", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        if (cust.getAccounts() != null) {
            for (Account acc : cust.getAccounts()) {
                model.addRow(new Object[]{acc.getAccountNumber(), (acc instanceof SavingAccount ? "Savings" : "Checking"), acc.getBalance(), acc.getStatus()});
            }
        }
        JTable table = new JTable(model);
        table.setRowHeight(30);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel bot = new JPanel(new FlowLayout());
        bot.setBackground(creamBg);
        JButton btnSelect = new JButton("Select & Manage Account");
        JButton btnOpen = new JButton("Open New Account");
        btnSelect.setBackground(darkGreen);
        btnSelect.setForeground(gold);
        bot.add(btnSelect);
        bot.add(btnOpen);
        p.add(bot, BorderLayout.SOUTH);
        btnSelect.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String num = (String) table.getValueAt(row, 0);
                currentSelectedAccount = null;
                for (Account a : cust.getAccounts()) {
                    if (a.getAccountNumber().equals(num)) {
                        currentSelectedAccount = a;
                        break;
                    }
                }
                if (currentSelectedAccount != null) {
                    JOptionPane.showMessageDialog(this, "Account " + num + " is ready.");
                    showQuickOpDialog(cust, table);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an account from the table.");
            }
        });
        btnOpen.addActionListener(e -> {
            String type = JOptionPane.showInputDialog("Type (checking/saving):");
            String depStr = JOptionPane.showInputDialog("Initial Deposit:");
            if (type != null && depStr != null) {
                try {
                    double dep = Double.parseDouble(depStr);
                    String result = cust.openAccount(type, dep);
                    JOptionPane.showMessageDialog(this, result);
                    while (model.getRowCount() > 0) model.removeRow(0);
                    for (Account acc : cust.getAccounts()) {
                        model.addRow(new Object[]{acc.getAccountNumber(), (acc instanceof SavingAccount ? "Savings" : "Checking"), acc.getBalance(), acc.getStatus()});
                    }
                    contentLayout.show(contentCardPanel, "MY ACCOUNTS");
                    refreshDashboardContent(contentCardPanel, cust);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error creating account: " + ex.getMessage());
                }
            }
        });
        return p;
    }

    private void showQuickOpDialog(Customer cust, JTable table) {
        if (currentSelectedAccount == null) return;
        String[] ops = {"Deposit", "Withdraw", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this, "Account: " + currentSelectedAccount.getAccountNumber() + "\nChoose operation:", "Quick Actions", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, ops, ops[0]);
        if (choice == 0) {
            String amtStr = JOptionPane.showInputDialog("Enter Deposit Amount:");
            if (amtStr != null) {
                try {
                    double amt = Double.parseDouble(amtStr);
                    Transaction t = new Transaction(TransactionType.DEPOSIT, amt, currentSelectedAccount);
                   if (!t.execute()){throw new Exception("Failed operation");}
                    Main.bank.addTransaction(t);
                    for (int i = 0; i < table.getRowCount(); i++) {
                        if (table.getValueAt(i, 0).equals(currentSelectedAccount.getAccountNumber())) {
                            table.setValueAt(currentSelectedAccount.getBalance(), i, 2);
                            break;
                        }
                    }
                    JOptionPane.showMessageDialog(this, "Deposit Successful!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        } else if (choice == 1) {
            String amtStr = JOptionPane.showInputDialog("Enter Withdrawal Amount:");
            if (amtStr != null) {
                try {
                    double amt = Double.parseDouble(amtStr);
                    Transaction t = new Transaction(TransactionType.WITHDRAW, amt, currentSelectedAccount);
                    if (!t.execute()){throw new Exception("Failed operation");}
                    Main.bank.addTransaction(t);
                    for (int i = 0; i < table.getRowCount(); i++) {
                        if (table.getValueAt(i, 0).equals(currentSelectedAccount.getAccountNumber())) {
                            table.setValueAt(currentSelectedAccount.getBalance(), i, 2);
                            break;
                        }
                    }
                    JOptionPane.showMessageDialog(this, "Withdrawal Successful!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
            }
        }
    }

    private JPanel createTransferPanel(Customer cust) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(creamBg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField txtDest = new JTextField(15);
        JTextField txtAmt = new JTextField(15);
        JButton btnExec = new JButton("EXECUTE TRANSFER");
        btnExec.setBackground(darkGreen);
        btnExec.setForeground(gold);
        gbc.gridx = 0; gbc.gridy = 0; p.add(new JLabel("Secure Fund Transfer"), gbc);
        gbc.gridy = 1; p.add(new JLabel("Destination Account Number:"), gbc);
        gbc.gridy = 2; p.add(txtDest, gbc);
        gbc.gridy = 3; p.add(new JLabel("Amount:"), gbc);
        gbc.gridy = 4; p.add(txtAmt, gbc);
        gbc.gridy = 5; p.add(btnExec, gbc);
        btnExec.addActionListener(e -> {
            if (currentSelectedAccount == null) {
                JOptionPane.showMessageDialog(this, "Please select an account first from 'My Accounts'.");
                return;
            }
            String destNum = txtDest.getText().trim();
            Account dest = Main.bank.findAccount(destNum);
            if (dest == null) {
                JOptionPane.showMessageDialog(this, "Destination account not found!");
                return;
            }
            try {
                double amt = Double.parseDouble(txtAmt.getText().trim());
                Transaction t = new Transaction(amt, currentSelectedAccount, dest);
                if (t.execute()) {
                    Main.bank.addTransaction(t);
                    JOptionPane.showMessageDialog(this, "Transfer Successful!");
                    txtDest.setText("");
                    txtAmt.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Transfer Failed. Check balance.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid Amount!");
            }
        });
        return p;
    }

    private JPanel createTransactionsPanel(Customer cust) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(creamBg);
        String[] cols = {"Transaction ID", "Type", "Amount", "Status", "Time"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setRowHeight(30);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        if (transactionTimer != null) transactionTimer.stop();
        transactionTimer = new Timer(1000, e -> {
            if (currentSelectedAccount != null && currentSelectedAccount.getTransactions() != null) {
                model.setRowCount(0);
                for (Transaction t : currentSelectedAccount.getTransactions()) {
                    model.addRow(new Object[]{t.getTransactionId(), t.getType(), t.getAmount(), t.getStatus(), t.getTimestamp()});
                }
            }
        });
        transactionTimer.start();
        return p;
    }

    private JPanel createProfilePanel(Customer cust) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(creamBg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField nameField = new JTextField(cust.getName(), 20);
        JTextField emailField = new JTextField(cust.getEmail(), 20);
        JPasswordField passField = new JPasswordField(20);
        JTextField addrField = new JTextField(cust.getAddress(), 20);
        JButton updateBtn = new JButton("Update Profile");
        updateBtn.setBackground(darkGreen);
        updateBtn.setForeground(gold);
        gbc.gridx = 0; gbc.gridy = 0; p.add(new JLabel("Update Your Information"), gbc);
        gbc.gridy = 1; p.add(new JLabel("Full Name:"), gbc);
        gbc.gridy = 2; p.add(nameField, gbc);
        gbc.gridy = 3; p.add(new JLabel("Email:"), gbc);
        gbc.gridy = 4; p.add(emailField, gbc);
        gbc.gridy = 5; p.add(new JLabel("New Password (min 6 chars):"), gbc);
        gbc.gridy = 6; p.add(passField, gbc);
        gbc.gridy = 7; p.add(new JLabel("Address:"), gbc);
        gbc.gridy = 8; p.add(addrField, gbc);
        gbc.gridy = 9; p.add(updateBtn, gbc);
        updateBtn.addActionListener(e -> {
            String newPass = new String(passField.getPassword()).trim();
            if (newPass.length() > 0 && newPass.length() < 6) {
                JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.");
                return;
            }
            cust.updateProfile(nameField.getText().trim(), emailField.getText().trim(), newPass, cust.getUser_Name());
            if (newPass.length() >= 6) cust.setPassword(newPass);
            JOptionPane.showMessageDialog(this, "Profile updated!");
        });
        return p;
    }

    private JPanel createAdminPanel(Admin admin) {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(creamBg);


        JTextArea reportArea = new JTextArea(admin.generateFullReport());
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        reportArea.setEditable(false);
        JScrollPane reportScroll = new JScrollPane(reportArea);
        reportScroll.setPreferredSize(new Dimension(600, 300));
        panel.add(reportScroll, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        buttonPanel.setBackground(creamBg);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        JButton btnModifySettings = new JButton("Modify System Settings");
        styleButton(btnModifySettings);
        btnModifySettings.addActionListener(e -> {
            JPanel settingsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
            JTextField rateField = new JTextField(String.valueOf(SystemSettings.getInterestRate()));
            JTextField minBalField = new JTextField(String.valueOf(SystemSettings.getMinimumBalance()));
            JTextField limitField = new JTextField(String.valueOf(SystemSettings.getWithdrawLimit()));
            settingsPanel.add(new JLabel("Interest Rate (e.g., 0.03):"));
            settingsPanel.add(rateField);
            settingsPanel.add(new JLabel("Minimum Balance for Savings:"));
            settingsPanel.add(minBalField);
            settingsPanel.add(new JLabel("Monthly Withdrawal Limit:"));
            settingsPanel.add(limitField);
            int result = JOptionPane.showConfirmDialog(this, settingsPanel, "Modify System Settings", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    double rate = Double.parseDouble(rateField.getText());
                    double minBal = Double.parseDouble(minBalField.getText());
                    int limit = Integer.parseInt(limitField.getText());
                    admin.modifySystemSettings(rate, minBal, limit);
                    JOptionPane.showMessageDialog(this, "Settings updated successfully!");
                    reportArea.setText(admin.generateFullReport());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid number format!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        JButton btnFreezeActivate = new JButton("Freeze/Activate Customer Accounts");
        styleButton(btnFreezeActivate);
        btnFreezeActivate.addActionListener(e -> {
            String email = JOptionPane.showInputDialog("Enter customer email or username:");
            if (email != null && !email.trim().isEmpty()) {
                Users u = Main.bank.findUser(email.trim());
                if (u instanceof Customer) {
                    String[] options = {"Freeze All", "Activate All", "Cancel"};
                    int choice = JOptionPane.showOptionDialog(this, "Customer: " + u.getName(), "Freeze/Activate",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    if (choice == 0) admin.freezeAllCustomerAccounts((Customer) u);
                    else if (choice == 1) admin.activeAllCustomerAccounts((Customer) u);
                    reportArea.setText(admin.generateFullReport());
                } else {
                    JOptionPane.showMessageDialog(this, "Customer not found!");
                }
            }
        });


        JButton btnResetPassword = new JButton("Reset User Password");
        styleButton(btnResetPassword);
        btnResetPassword.addActionListener(e -> {
            String email = JOptionPane.showInputDialog("Enter user email or username:");
            if (email != null && !email.trim().isEmpty()) {
                Users u = Main.bank.findUser(email.trim());
                if (u == null) {
                    JOptionPane.showMessageDialog(this, "User not found!");
                    return;
                }
                String newPwd = JOptionPane.showInputDialog("Enter new password (min 6 chars):");
                if (newPwd != null && newPwd.length() >= 6) {
                    admin.resetPassword(u, newPwd);
                    JOptionPane.showMessageDialog(this, "Password reset for " + u.getName());
                    reportArea.setText(admin.generateFullReport());
                } else {
                    JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.");
                }
            }
        });


        JButton btnViewTransactions = new JButton("View All Transactions");
        styleButton(btnViewTransactions);
        btnViewTransactions.addActionListener(e -> {
            JTextArea transArea = new JTextArea();
            transArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            for (Transaction t : Main.bank.getTransactions()) {
                transArea.append(t.toString() + "\n");
            }
            JScrollPane scroll = new JScrollPane(transArea);
            scroll.setPreferredSize(new Dimension(600, 400));
            JOptionPane.showMessageDialog(this, scroll, "All Transactions", JOptionPane.PLAIN_MESSAGE);
        });

        JButton btnCreateEmployee = new JButton("Create Employee");
        styleButton(btnCreateEmployee);
        btnCreateEmployee.addActionListener(e -> showCreateEmployeeDialog(admin, reportArea));


        JButton btnApplyInterest = new JButton("Apply Monthly Interest to All Savings");
        styleButton(btnApplyInterest);
        btnApplyInterest.addActionListener(e -> {
            Main.bank.applyMonthlyInterestToAllSavings();
            JOptionPane.showMessageDialog(this, "Interest applied!");
            reportArea.setText(admin.generateFullReport());
        });


        JButton btnOverrideWithdraw = new JButton("Admin Override Withdrawal");
        styleButton(btnOverrideWithdraw);
        btnOverrideWithdraw.addActionListener(e -> {
            String accNum = JOptionPane.showInputDialog("Enter account number:");
            if (accNum == null || accNum.trim().isEmpty()) return;
            String amtStr = JOptionPane.showInputDialog("Enter amount to withdraw:");
            if (amtStr == null) return;
            try {
                double amt = Double.parseDouble(amtStr);
                String result = admin.overrideTransactionLimits(accNum.trim(), amt);
                JOptionPane.showMessageDialog(this, result);
                reportArea.setText(admin.generateFullReport());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount!");
            }
        });

        JButton btnFullReport = new JButton("Refresh Full Report");
        styleButton(btnFullReport);
        btnFullReport.addActionListener(e -> reportArea.setText(admin.generateFullReport()));

        JButton btnLogout = new JButton("Logout");
        styleButton(btnLogout);
        btnLogout.addActionListener(e -> {
            if (transactionTimer != null) transactionTimer.stop();
            currentSelectedAccount = null;
            cardLayout.show(mainPanel, "LOGIN");
        });

        buttonPanel.add(btnModifySettings);
        buttonPanel.add(btnFreezeActivate);
        buttonPanel.add(btnResetPassword);
        buttonPanel.add(btnViewTransactions);
        buttonPanel.add(btnCreateEmployee);
        buttonPanel.add(btnApplyInterest);
        buttonPanel.add(btnOverrideWithdraw);
        buttonPanel.add(btnFullReport);
        buttonPanel.add(btnLogout);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createEmployeePanel(Employee emp) {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(creamBg);


        JTextArea resultArea = new JTextArea(emp.generateReport());
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultArea.setEditable(false);
        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setPreferredSize(new Dimension(600, 300));
        panel.add(resultScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 15, 15));
        buttonPanel.setBackground(creamBg);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnViewCustomer = new JButton("View Customer Accounts");
        styleButton(btnViewCustomer);
        btnViewCustomer.addActionListener(e -> {
            String email = JOptionPane.showInputDialog("Enter customer email or username:");
            if (email != null && !email.trim().isEmpty()) {
                String result = emp.viewCustomerAccounts(email.trim());
                resultArea.setText(result);
            }
        });

        JButton btnSearchCustomer = new JButton("Search Customer");
        styleButton(btnSearchCustomer);
        btnSearchCustomer.addActionListener(e -> {
            String keyword = JOptionPane.showInputDialog("Enter name or email:");
            if (keyword != null && !keyword.trim().isEmpty()) {
                String result = emp.searchCustomer(keyword.trim());
                resultArea.setText(result);
            }
        });

        JButton btnGenerateReport = new JButton("Generate Report");
        styleButton(btnGenerateReport);
        btnGenerateReport.addActionListener(e -> resultArea.setText(emp.generateReport()));

        JButton btnLogout = new JButton("Logout");
        styleButton(btnLogout);
        btnLogout.addActionListener(e -> {
            if (transactionTimer != null) transactionTimer.stop();
            currentSelectedAccount = null;
            cardLayout.show(mainPanel, "LOGIN");
        });

        buttonPanel.add(btnViewCustomer);
        buttonPanel.add(btnSearchCustomer);
        buttonPanel.add(btnGenerateReport);
        buttonPanel.add(btnLogout);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }


    private void styleButton(JButton btn) {
        btn.setBackground(darkGreen);
        btn.setForeground(gold);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
    }


    private void showCreateEmployeeDialog(Admin admin, JTextArea reportArea) {
        JTextField nameField = new JTextField(15);
        JTextField emailField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JTextField empIdField = new JTextField(15);
        JTextField positionField = new JTextField(15);
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Full Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password (min 6 chars):"));
        panel.add(passField);
        panel.add(new JLabel("Employee ID:"));
        panel.add(empIdField);
        panel.add(new JLabel("Position:"));
        panel.add(positionField);
        int result = JOptionPane.showConfirmDialog(this, panel, "Create New Employee", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String pwd = new String(passField.getPassword()).trim();
            String empId = empIdField.getText().trim();
            String position = positionField.getText().trim();
            if (name.isEmpty() || email.isEmpty() || pwd.length() < 6 || empId.isEmpty() || position.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields required and password >=6 characters.");
                return;
            }
            if (Main.bank.findUser(email) != null) {
                JOptionPane.showMessageDialog(this, "Email already exists!");
                return;
            }
            admin.createEmployee(name, email, pwd, empId, position);
            JOptionPane.showMessageDialog(this, "Employee created!");
            reportArea.setText(admin.generateFullReport());
        }
    }


    private JPanel createCard(String title, String value) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(gold, 2));
        JLabel t = new JLabel("  " + title);
        t.setForeground(Color.GRAY);
        JLabel v = new JLabel("  " + value);
        v.setFont(new Font("SansSerif", Font.BOLD, 22));
        v.setForeground(darkGreen);
        card.add(t);
        card.add(v);
        return card;
    }
}
