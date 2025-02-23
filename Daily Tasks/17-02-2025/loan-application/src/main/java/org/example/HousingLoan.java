package org.example;

public class HousingLoan extends Loan {
	private String propertyAddress;
	public HousingLoan() {
		
	}

	public HousingLoan(String borrowerName, double loanAmount, double interestRate, int tenure,String propertyAddress) {
		super(borrowerName, loanAmount, interestRate, tenure);
		this.propertyAddress=propertyAddress;
		// TODO Auto-generated constructor stub
	}
	public String getPropertyAddress() {
        return propertyAddress;
    }

    public void setPropertyAddress(String propertyAddress) {
        this.propertyAddress = propertyAddress;
    }

    @Override
    public void displayLoanDetails() {
        super.displayLoanDetails();
        System.out.println("Property Address: " + propertyAddress);
    }

}
