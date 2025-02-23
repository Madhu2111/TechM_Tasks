package org.example;

public class Loan {

	private String borrowerName;
    private double loanAmount;
    private double interestRate;
    private int tenure;
    public Loan() {
    	
    }

    // Constructor
    public Loan(String borrowerName, double loanAmount, double interestRate, int tenure) {
        this.borrowerName = borrowerName;
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.tenure = tenure;
    }

    // Getters and Setters
    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public int getTenure() {
        return tenure;
    }

    public void setTenure(int tenure) {
        this.tenure = tenure;
    }

    // Method to calculate EMI
    public double calculateEMI() {
        double monthlyRate = (interestRate / 100) / 12;
        int months = tenure * 12;
        return (loanAmount * monthlyRate * Math.pow(1 + monthlyRate, months)) / (Math.pow(1 + monthlyRate, months) - 1);
    }

    // Display Loan Details
    public void displayLoanDetails() {
        System.out.println("Borrower Name: " + borrowerName);
        System.out.println("Loan Amount: ₹" + loanAmount);
        System.out.println("Interest Rate: " + interestRate + "%");
        System.out.println("Tenure: " + tenure + " years");
        System.out.println("EMI: ₹" + String.format("%.2f", calculateEMI()));
    }
}
