package cz.tomasdvorak.roastingfacility.services;

import cz.tomasdvorak.roastingfacility.entities.Facility;
import cz.tomasdvorak.roastingfacility.entities.GreenCoffee;
import cz.tomasdvorak.roastingfacility.entities.Machine;
import cz.tomasdvorak.roastingfacility.entities.RoastingProcess;
import cz.tomasdvorak.roastingfacility.services.roasting.IllegalRoastConfiguration;
import cz.tomasdvorak.roastingfacility.services.roasting.RoastConfiguration;

import java.util.List;

public interface FacilityManagement {

    /**
     * Access the underlying entity of the facility, mainly for integration purposes
     */
    Facility getFacility();

    /**
     * Get all available green coffee stock supplies, even if they are already consumed
     */
    List<GreenCoffee> getGreenCoffeeStock();

    /**
     * Get only green coffee supplies that have supply greater than 0, they may be roasted
     */
    List<GreenCoffee> getAvailableGreenCoffee();

    /**
     * Put new green coffee to stock
     * @param greenCoffeeName human readable name of the coffee
     * @param amountInKgs how many kilograms should be added to the stock
     * @return newly created and persisted GreenCoffee entity
     */
    GreenCoffee addToStock(String greenCoffeeName, int amountInKgs);

    /**
     * Change the weight of the green coffee on the stock
     * @param greenCoffee which type of green coffee should be updated
     * @param amountInKgs new weight, how much is actually on stock
     * @return updated and persisted GreenCoffee entity
     */
    GreenCoffee updateStock(GreenCoffee greenCoffee, int amountInKgs);

    /**
     * Add new roasting machine to the current facility
     * @param machineName human readable name
     * @param capacity how many kilograms of green coffee fits in
     * @return newly created and persisted Machine entity
     */
    Machine addRoastingMachine(String machineName, int capacity);

    /**
     * List all available Machines of this facility
     */
    List<Machine> getAllMachines();

    /**
     * Start the roasting process
     * @param configuration details of the roasting process - which coffee, how much, for how long...
     * @return newly created and persisted RoastingProcess entity holding all the additional information about this run
     */
    RoastingProcess roast(RoastConfiguration configuration) throws IllegalRoastConfiguration;

    /**
     * List all available processes persisted for this facility. In real life, we would always need paging, as this
     * List would be HUGE. For demo purposes it should be OK.
     */
    List<RoastingProcess> getRoastingProcesses();
}
