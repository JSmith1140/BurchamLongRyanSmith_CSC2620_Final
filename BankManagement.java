
/*
 * Bank Management Project
 * Author: Jacob Smith
 */
import java.util.Scanner;

abstract class BankAccount {
    /*
     * BankAccount class which has methods to deposit amount, withdraw amount, and
     * get balance
     */
    private String accountNumber;
    private String accountName;
    private String accountType;
    private double balance;

    public BankAccount(String accountNumber, String accountName, String accountType, double balance) { // BankAcount
                                                                                                       // constructor
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.accountType = accountType;
        this.balance = balance;
    }

    public String getAccountNumber() { // getter for accountNumber
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) { // setter for accountNumber
        this.accountNumber = accountNumber;
    }

    public String getAccountName() { // getter for accountName
        return accountName;
    }

    public void setAccountName(String accountName) { // setter for accountName
        this.accountName = accountName;
    }

    public String getAccountType() { // getter for accountType
        return accountType;
    }

    public void setAccountType(String accountType) { // setter for accountType
        this.accountType = accountType;
    }

    public double getBalance() { // getter for balance
        return balance;
    }

    public void setBalance(double balance) { // setter for balance
        this.balance = balance;
    }

    abstract void depositAmount(double amount); // depositAmount abstract method

    abstract void withdrawAmount(double amount); // withdrawAmount abstract method

    abstract void displayAccountDetails(); // displayAccountDetails abstract method
}

// Exception class
class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

// SavingsAccount class
// Extends BankAccount
class SavingsAccount extends BankAccount {
    /*
     * SavingsAccount class
     */
    private double interest;

    public SavingsAccount(String accountNumber, String accountName, double balance, double interest) {
        super(accountNumber, accountName, "Savings", balance);
        this.interest = interest;
    }

    public void addInterest() {
        depositAmount(getBalance() * interest);
    }

    // Overriding the depositAmount superclass method
    @Override
    public void depositAmount(double amount) {
        try {
            if (amount < 0) {
                throw new InsufficientFundsException("Invalid deposit amount."); // Exception message if user gives a
                                                                                 // negative number
            }
            setBalance(getBalance() + amount);
            System.out.println("Amount deposited: $" + amount);
        } catch (InsufficientFundsException e) {
            System.out.println(e.getMessage());
        }
    }

    // Overriding withdrawAmount superclass method
    @Override
    public void withdrawAmount(double amount) {
        try {
            if (amount < 0) {
                throw new InsufficientFundsException("Invalid withdrawal amount."); // Exception message if user gives a
                                                                                    // negative number
            }
            if (getBalance() >= amount) {
                setBalance(getBalance() - amount);
                System.out.println("Amount withdrawn: $" + amount);
            } else {
                throw new InsufficientFundsException("Insufficient balance in Savings Account."); // Exception message
                                                                                                  // if user withdraws
                                                                                                  // more than balance
            }
        } catch (InsufficientFundsException e) {
            System.out.println(e.getMessage());
        }
    }

    // Overriding displayAccountDetails superclass method
    @Override
    public void displayAccountDetails() {
        System.out.println();
        System.out.println("Savings Account Details:");
        System.out.println("Account Number: " + getAccountNumber());
        System.out.println("Account Holder: " + getAccountName());
        System.out.println("Balance: $" + getBalance());
        System.out.println("Interest Rate: " + interest);
    }
}

// CurrentAccount class
// Extends BankAccount
class CurrentAccount extends BankAccount {
    /*
     * CurrentAccount class
     */
    private double overdraftLimit;

    public CurrentAccount(String accountNumber, String accountName, double balance, double overdraftLimit) {
        super(accountNumber, accountName, "Current", balance);
        this.overdraftLimit = overdraftLimit;
    }

    // Ovveriding depositAmount superclass method
    @Override
    public void depositAmount(double amount) {
        try {
            if (amount < 0) {
                throw new InsufficientFundsException("Invalid deposit amount.");
            }
            setBalance(getBalance() + amount);
            System.out.println("Amount deposited: $" + amount);
        } catch (InsufficientFundsException e) {
            System.out.println(e.getMessage());
        }
    }

    // Overriding withdrawAmount superclass method
    @Override
    public void withdrawAmount(double amount) {
        try {
            if (amount < 0) {
                throw new InsufficientFundsException("Invalid withdrawal amount.");
            }
            if (getBalance() - amount >= -overdraftLimit) { // Allow overdraft withdrawals
                setBalance(getBalance() - amount);
                System.out.println("Amount withdrawn: $" + amount);
            } else {
                throw new InsufficientFundsException("Exceeded overdraft limit."); // Exception message if user exceeds
                                                                                   // the overdraft limit
            }
        } catch (InsufficientFundsException e) {
            System.out.println(e.getMessage());
        }
    }

    // Overriding displayAccountDetails superclass method
    @Override
    public void displayAccountDetails() {
        System.out.println();
        System.out.println("Current Account Details:");
        System.out.println("Account Number: " + getAccountNumber());
        System.out.println("Account Holder: " + getAccountName());
        System.out.println("Balance: $" + getBalance());
        System.out.println("Overdraft Limit: $" + overdraftLimit);
    }
}

public class BankManagement {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in); // scanner used for user input
        BankAccount account = null;

        // Choose account type
        System.out.print("Choose account type (Savings or Current): ");
        String accountType = scanner.nextLine();

        // Create the account
        if (accountType.equalsIgnoreCase("Savings")) {
            account = createSavingsAccount(scanner);
        } else if (accountType.equalsIgnoreCase("Current")) {
            account = createCurrentAccount(scanner);
        } else {
            System.out.println("Invalid account type selected");
            return;
        }

        // User Deposits money
        System.out.print("\nEnter deposit amount: $");
        double depositAmount = scanner.nextDouble();
        scanner.nextLine();
        depositMoney(account, depositAmount);

        // User Withdraws money
        System.out.print("\nEnter withdrawal amount: $");
        double withdrawalAmount = scanner.nextDouble();
        scanner.nextLine();
        withdrawMoney(account, withdrawalAmount);
        account.displayAccountDetails(); // Display account details
    }

    // Method to create a Savings Account
    private static BankAccount createSavingsAccount(Scanner scanner) {
        // Ask for the users account details
        System.out.println("\nSavings Account:");
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();

        System.out.print("Enter account holder name: ");
        String accountName = scanner.nextLine();

        double initialBalance;
        while (true) {
            System.out.print("Enter initial balance: $");
            initialBalance = scanner.nextDouble();
            scanner.nextLine();
            if (initialBalance > 0) {
                break;
            } else {
                System.out.println("Initial balance must be positive. Please try again.");
            }
        }

        System.out.print("Enter interest rate (as a decimal): ");
        double interestRate = scanner.nextDouble();
        scanner.nextLine();
        return new SavingsAccount(accountNumber, accountName, initialBalance, interestRate);
    }

    // Method to create a Current Account
    private static BankAccount createCurrentAccount(Scanner scanner) {
        // Asks for the users account details
        System.out.println("\nCurrent Account:");
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();

        System.out.print("Enter account holder name: ");
        String accountName = scanner.nextLine();

        double initialBalance;
        while (true) {
            System.out.print("Enter initial balance: $");
            initialBalance = scanner.nextDouble();
            scanner.nextLine();
            if (initialBalance > 0) {
                break;
            } else {
                System.out.println("Initial balance must be positive. Please try again.");
            }
        }

        System.out.print("Enter overdraft limit: $");
        double overdraftLimit = scanner.nextDouble();
        scanner.nextLine();
        return new CurrentAccount(accountNumber, accountName, initialBalance, overdraftLimit);
    }

    private static void depositMoney(BankAccount account, double amount) {
        account.depositAmount(amount);
    }

    private static void withdrawMoney(BankAccount account, double amount) {
        account.withdrawAmount(amount);
    }
}