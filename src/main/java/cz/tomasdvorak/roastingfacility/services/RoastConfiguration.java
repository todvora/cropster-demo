package cz.tomasdvorak.roastingfacility.services;

import cz.tomasdvorak.roastingfacility.entities.GreenCoffee;
import cz.tomasdvorak.roastingfacility.entities.Machine;

public class RoastConfiguration {
    private final Machine machine;
    private final GreenCoffee greenCoffee;
    private final int startWeight;
    private final int duration;

    public RoastConfiguration(Machine machine, GreenCoffee greenCoffee, int startWeight, int duration) {
        this.machine = machine;
        this.greenCoffee = greenCoffee;
        this.startWeight = startWeight;
        this.duration = duration;
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

    public void validate() {
        int availableMachineCapacity = machine.getCapacity();
        double capacityRatio = 100.0 / availableMachineCapacity * startWeight;
        if(capacityRatio < RoastingProperties.MIN_START_WEIGHT_IN_PERCENT || capacityRatio > RoastingProperties.MAX_START_WEIGHT_IN_PERCENT) {
            throw new IllegalArgumentException(String.format("Machine overloaded, available capacity %d kg, required capacity %d kg", availableMachineCapacity, startWeight));
        }
    }
}
