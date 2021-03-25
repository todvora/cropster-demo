package cz.tomasdvorak.roastingfacility.services.roasting;

import cz.tomasdvorak.roastingfacility.entities.GreenCoffee;
import cz.tomasdvorak.roastingfacility.entities.Machine;

import java.util.Date;

public class RoastConfiguration {
    private final String productName;
    private final Machine machine;
    private final GreenCoffee greenCoffee;
    private final int startWeight;
    private final Date startDate;
    private final int duration;
    private final int weightLossInPercent;

    public RoastConfiguration(String productName, Machine machine, GreenCoffee greenCoffee, int startWeight, Date startDate, int duration, int weightLossInPercent) {
        this.productName = productName;
        this.machine = machine;
        this.greenCoffee = greenCoffee;
        this.startWeight = startWeight;
        this.startDate = startDate;
        this.duration = duration;
        this.weightLossInPercent = weightLossInPercent;
    }

    public String getProductName() {
        return productName;
    }

    public Machine getMachine() {
        return machine;
    }

    public GreenCoffee getGreenCoffee() {
        return greenCoffee;
    }

    public int getStartWeight() {
        return startWeight;
    }

    public int getDuration() {
        return duration;
    }

    public int getWeightLossInPercent() {
        return weightLossInPercent;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void validate() throws IllegalRoastConfiguration {
        int availableMachineCapacity = machine.getCapacity();
        if(availableMachineCapacity < startWeight) {
            throw new IllegalRoastConfiguration(String.format("Machine overloaded, available capacity %d kg, required capacity %d kg", availableMachineCapacity, startWeight));
        }

        // TODO: add more validations, here we could check timestamps and durations, ranges of weight loss... anything sensible
    }
}
