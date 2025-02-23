package org.example;

public class VehicleLoan extends Loan {
	private String vehicleModel;
	public VehicleLoan() {
		
	}

    public VehicleLoan(String borrowerName, double loanAmount, double interestRate, int tenure, String vehicleModel) {
        super(borrowerName, loanAmount, interestRate, tenure);
        this.vehicleModel = vehicleModel;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    @Override
    public void displayLoanDetails() {
        super.displayLoanDetails();
        System.out.println("Vehicle Model: " + vehicleModel);
    }
	
}
