public class SystemSettings {
    public static double interestRate = 0.02;
    public static double minimumBalance = 100.0;
    public static int withdrawLimit = 3;

    public static double getInterestRate() { return interestRate; }
    public static void setInterestRate(double rate) { interestRate = rate; }

    public static double getMinimumBalance() { return minimumBalance; }
    public static void setMinimumBalance(double minBal) { minimumBalance = minBal; }

    public static int getWithdrawLimit() { return withdrawLimit; }
    public static void setWithdrawLimit(int limit) { withdrawLimit = limit; }
}
