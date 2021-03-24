package cz.tomasdvorak.roastingfacility.services;

import cz.tomasdvorak.roastingfacility.entities.Facility;
import cz.tomasdvorak.roastingfacility.entities.GreenCoffee;
import cz.tomasdvorak.roastingfacility.entities.Machine;
import cz.tomasdvorak.roastingfacility.entities.RoastingProcess;
import cz.tomasdvorak.roastingfacility.repositories.GreenCoffeeRepository;
import cz.tomasdvorak.roastingfacility.repositories.MachineRepository;
import cz.tomasdvorak.roastingfacility.repositories.RoastingProcessRepository;
import cz.tomasdvorak.roastingfacility.services.roasting.RoastConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FacilityManagementImpl implements FacilityManagement {

    private static final Logger logger = LoggerFactory.getLogger(FacilityManagementImpl.class);

    private final GreenCoffeeRepository greenCoffeeRepository;
    private final MachineRepository machineRepository;
    private final RoastingProcessRepository roastingProcessRepository;
    private final Facility facility;

    public FacilityManagementImpl(Facility facility, GreenCoffeeRepository greenCoffeeRepository, MachineRepository machineRepository, RoastingProcessRepository roastingProcessRepository) {
        this.greenCoffeeRepository = greenCoffeeRepository;
        this.machineRepository = machineRepository;
        this.facility = facility;
        this.roastingProcessRepository = roastingProcessRepository;
    }

    @Override
    public Facility getFacility() {
        return facility;
    }

    @Override
    public List<GreenCoffee> getGreenCoffeeStock() {
        return greenCoffeeRepository.getByFacility(facility);
    }

    @Override
    public List<GreenCoffee> getAvailableGreenCoffee() {
        return greenCoffeeRepository.getAvailableByFacility(facility);
    }

    @Override
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
    @Override
    public GreenCoffee updateStock(GreenCoffee greenCoffee, int amountInKgs) {
        if(amountInKgs < 0) {
            throw new IllegalArgumentException("Amount in KGs has to be at least 0");
        }
        greenCoffee.setStock(amountInKgs);
        return this.greenCoffeeRepository.saveAndFlush(greenCoffee);
    }

    @Override
    public Machine addRoastingMachine(String machineName, int capacity) {
        return machineRepository.save(new Machine(facility, machineName, capacity));
    }

    @Override
    public List<Machine> getAllMachines() {
        return machineRepository.getByFacility(facility);
    }

    @Override
    public RoastingProcess roast(RoastConfiguration configuration) {


        int newStockWeight = configuration.getGreenCoffee().getStock() - configuration.getStartWeight();
        GreenCoffee stockStatusAfter = updateStock(configuration.getGreenCoffee(), newStockWeight);

        logger.info("Roasting started, we are roasting {} kg of {}. There are {} kg of this coffee remaining on stock", configuration.getStartWeight(), configuration.getGreenCoffee().getName(), stockStatusAfter.getStock());

        Date startTime = configuration.getStartDate();
        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.MINUTE, configuration.getDuration());

        // TODO: uh, oh, rounding here, float numbers forced to int database columns. Best solution?
        final int endWeight = (int) Math.floor(configuration.getStartWeight() / configuration.getWeightLossInPercent());

        return roastingProcessRepository.save(new RoastingProcess(configuration.getProductName(), configuration.getStartWeight(), endWeight, startTime, endTime.getTime(), configuration.getGreenCoffee(), facility));
    }
}
