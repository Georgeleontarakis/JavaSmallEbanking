package entities.Orders;

import storage.Storable;

import java.time.LocalDate;

public abstract class StandingOrder implements Storable {

    protected String orderId;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected int frequencyInMonths;
    protected boolean active = true;
    protected int failedAttempts = 0;
    protected int executions = 0;


    public StandingOrder(String orderId, LocalDate startDate, LocalDate endDate){

        this.orderId = orderId;
        this.startDate = startDate;
        this.endDate = endDate;

    }

    public StandingOrder(){}

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setStarDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setFrequencyInMonths(int frequencyInMonths) {
        this.frequencyInMonths = frequencyInMonths;
    }

    public String getOrderId() {
        return orderId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public int getExecutions() {
        return executions;
    }
    
    public void incrementExecutions() {
        this.executions++;
    }    

    public int getFrequencyInMonths() {
        return frequencyInMonths;
    }

    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public void incrementFailures() {
        this.failedAttempts++;
    }
    
    public int getFailures() {
        return this.failedAttempts;
    }

}
