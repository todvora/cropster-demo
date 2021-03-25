package cz.tomasdvorak.roastingfacility.services;

import cz.tomasdvorak.roastingfacility.entities.Facility;
import cz.tomasdvorak.roastingfacility.entities.GreenCoffee;
import cz.tomasdvorak.roastingfacility.entities.Machine;
import cz.tomasdvorak.roastingfacility.entities.RoastingProcess;
import cz.tomasdvorak.roastingfacility.repositories.GreenCoffeeRepository;
import cz.tomasdvorak.roastingfacility.repositories.MachineRepository;
import cz.tomasdvorak.roastingfacility.repositories.RoastingProcessRepository;
import cz.tomasdvorak.roastingfacility.services.roasting.IllegalRoastConfiguration;
import cz.tomasdvorak.roastingfacility.services.roasting.RoastConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
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
        return greenCoffeeRepository.findByFacility(facility);
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
        return machineRepository.findByFacility(facility);
    }

    /*
     * TODO: this method should be probably transactional, if the persisting of the RoastingProcess fails, it should also rollback
     * the GreenCoffee stock update, if that makes sense in real world? As this is not explicitly mentioned in the challenge description,
     * I'll just keep the @Transactional annotation here and the comment. In real app, it would need some additional configuration,
     * thinking about the scope of the transaction, and proper tests.
     */
    @Transactional
    @Override
    public RoastingProcess roast(RoastConfiguration configuration) throws IllegalRoastConfiguration {

        // run some basic sanity checks of the configuration
        configuration.validate();

        // update the green coffee stock to the post-roast weight (should this happen before or after roasting? What makes sense in real world?)
        int newStockWeight = configuration.getGreenCoffee().getStock() - configuration.getStartWeight();
        GreenCoffee stockStatusAfter = updateStock(configuration.getGreenCoffee(), newStockWeight);

        logger.info("Roasting started, we are roasting {} kg of {}. There are {} kg of this coffee remaining on stock", configuration.getStartWeight(), configuration.getGreenCoffee().getName(), stockStatusAfter.getStock());

        Date startTime = configuration.getStartDate();
        Date endTime = getEndTime(startTime, configuration.getDuration());

        // TODO: uh, oh, rounding here, float numbers forced to int database columns. Best solution? How is the rounding solved in real world? How exact are the
        // scales of machines and roasting facilities?
        final int endWeight = (int) (configuration.getStartWeight() - configuration.getStartWeight() * (configuration.getWeightLossInPercent() / 100.0));
        RoastingProcess processEntry = new RoastingProcess(configuration.getProductName(), configuration.getStartWeight(), endWeight, startTime, endTime, configuration.getGreenCoffee(), facility);
        return roastingProcessRepository.save(processEntry);
    }

    private Date getEndTime(Date startDate, int durationInMinutes) {
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(startDate);
        endCal.add(Calendar.MINUTE, durationInMinutes);
        return endCal.getTime();
    }

    public List<RoastingProcess> getRoastingProcesses() {
        return roastingProcessRepository.findByFacility(facility);
    }
}
