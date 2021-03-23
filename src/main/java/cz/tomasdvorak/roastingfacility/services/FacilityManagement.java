package cz.tomasdvorak.roastingfacility.services;

import cz.tomasdvorak.roastingfacility.entities.Facility;
import cz.tomasdvorak.roastingfacility.entities.GreenCoffee;
import cz.tomasdvorak.roastingfacility.entities.Machine;
import cz.tomasdvorak.roastingfacility.entities.RoastingProcess;
import cz.tomasdvorak.roastingfacility.repositories.GreenCoffeeRepository;
import cz.tomasdvorak.roastingfacility.repositories.MachineRepository;
import cz.tomasdvorak.roastingfacility.repositories.RoastingProcessRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FacilityManagement {

    private static final Logger logger = LoggerFactory.getLogger(FacilityManagement.class);

    private final GreenCoffeeRepository greenCoffeeRepository;
    private final MachineRepository machineRepository;
    private final RoastingProcessRepository roastingProcessRepository;
    private final Facility facility;

    public FacilityManagement(Facility facility, GreenCoffeeRepository greenCoffeeRepository, MachineRepository machineRepository, RoastingProcessRepository roastingProcessRepository) {
        this.greenCoffeeRepository = greenCoffeeRepository;
        this.machineRepository = machineRepository;
        this.facility = facility;
        this.roastingProcessRepository = roastingProcessRepository;
    }

    public List<GreenCoffee> getGreenCoffeeStock() {
        return greenCoffeeRepository.getByFacility(facility);
    }

    public List<GreenCoffee> getAvailableGreenCoffee() {
        return greenCoffeeRepository.getAvailableByFacility(facility);
    }

    public GreenCoffee addToStock(String greenCoffeeName, int amountInKgs) {
        if(amountInKgs < 1) {
            throw new IllegalArgumentException("Amount in KGs has to be greater than 0");
        }
        return this.greenCoffeeRepository.save(new GreenCoffee(facility, greenCoffeeName, amountInKgs));
    }

    /**
     * This method updates the weight to the provided value, we could also provide just delta, depending on the use-case
     * @param greenCoffee
     * @param amountInKgs
     * @return
     */
    protected GreenCoffee updateStock(GreenCoffee greenCoffee, int amountInKgs) {
        if(amountInKgs < 0) {
            throw new IllegalArgumentException("Amount in KGs has to be at least 0");
        }
        greenCoffee.setStock(amountInKgs);
        return this.greenCoffeeRepository.saveAndFlush(greenCoffee);
    }

    public Machine addRoastingMachine(String machineName, int capacity) {
        return machineRepository.save(new Machine(facility, machineName, capacity));
    }

    public List<Machine> getAllMachines() {
        return machineRepository.getByFacility(facility);
    }

    public RoastingProcess roast(RoastConfiguration configuration) {


        int newStockWeight = configuration.getGreenCoffee().getStock() - configuration.getStartWeight();
        GreenCoffee stockStatusAfter = updateStock(configuration.getGreenCoffee(), newStockWeight);

        logger.info("Roasting started, we are roasting {} kg of {}. There are {} kg of this coffee remaining on stock", configuration.getStartWeight(), configuration.getGreenCoffee().getName(), stockStatusAfter.getStock());

        //roastingProcessRepository.save(new RoastingProcess());
        return null;
    }
}