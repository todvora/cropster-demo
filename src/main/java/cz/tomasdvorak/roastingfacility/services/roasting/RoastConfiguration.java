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

    public void validate() throws IllegalRoastingConfiguration {
        int availableMachineCapacity = machine.getCapacity();
        double capacityRatio = 100.0 / availableMachineCapacity * startWeight;
        if(capacityRatio < RoastingProperties.MIN_START_WEIGHT_IN_PERCENT || capacityRatio > RoastingProperties.MAX_START_WEIGHT_IN_PERCENT) {
            throw new IllegalArgumentException(String.format("Machine overloaded, available capacity %d kg, required capacity %d kg", availableMachineCapacity, startWeight));
        }
    }
}
