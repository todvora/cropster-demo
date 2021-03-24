package cz.tomasdvorak.roastingfacility.services.demo;

import cz.tomasdvorak.roastingfacility.entities.Facility;
import cz.tomasdvorak.roastingfacility.entities.GreenCoffee;
import cz.tomasdvorak.roastingfacility.entities.Machine;
import cz.tomasdvorak.roastingfacility.entities.RoastingProcess;
import cz.tomasdvorak.roastingfacility.repositories.FacilityRepository;
import cz.tomasdvorak.roastingfacility.services.FacilityManagement;
import cz.tomasdvorak.roastingfacility.services.FacilityService;
import cz.tomasdvorak.roastingfacility.services.roasting.RoastConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.*;

@ActiveProfiles("demo")
@SpringBootTest
/**
 * This would be probably better an integration test, accessing only exposed REST (or other) endpoints. But the endpoints
 * are beyond the scope of the challenge, so we'll access internal beans directly.
 */
public class RoastingDemo {

    private static final Logger logger = LoggerFactory.getLogger(RoastingDemo.class);

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private FacilityService facilityService;

    private List<String> coffeeNames;
    private Random random;



    @BeforeEach
    public void initTest() throws IOException {
        this.coffeeNames = readCoffeeNames(); // read some
        random = new Random(1); // set the seed to create random but repeatable values for the demo purposes
    }


    public void createDemoFacilities() {

        // create two roasting facilities, each having 3 roasting machines with capacities between 15kg and 90kg (INT values only)
        Facility facility1 = facilityRepository.save(new Facility("demo facility #1"));
        Facility facility2 = facilityRepository.save(new Facility("demo facility #2"));

        FacilityManagement facilityManagement1 = facilityService.getForFacility(facility1);
        FacilityManagement facilityManagement2 = facilityService.getForFacility(facility2);

        initializeMachines(facilityManagement1, 3, 15, 90);
        initializeMachines(facilityManagement2, 3, 15, 90);

        initializeGreenCoffee(facilityManagement1, 5, 500, 10_000);
        initializeGreenCoffee(facilityManagement2, 5, 500, 10_000);


    }

    private void initializeGreenCoffee(FacilityManagement facilityManagement, int beanTypesCount, int stockMin, int stockMax) {

        ArrayList<String> coffeeNames = new ArrayList<>(this.coffeeNames);
        Collections.shuffle(coffeeNames, random);
        coffeeNames.stream()
                .limit(beanTypesCount)
                .forEach(coffeeName -> {
                    GreenCoffee greenCoffee = facilityManagement.addToStock(coffeeName, randomInclusive(stockMin, stockMax));
                    logger.info("Facility “{}: added {}kgs of '{}' to the stock", facilityManagement.getFacility().getName(), greenCoffee.getStock(),  greenCoffee.getName());
                });

    }

    private void initializeMachines(FacilityManagement facility, int countOfMachines, int minCapacity, int maxCapacity) {
        for (int i =1; i <= countOfMachines; i++) {
            String machineName = String.format("Roasting machine #%d", i);
            int capacity = randomInclusive(minCapacity, maxCapacity);
            Machine machine = facility.addRoastingMachine(machineName, capacity);
            logger.info("Facility “{}: created roasting machine {} with capacity {}kgs", facility.getFacility().getName(), machineName, capacity);
        }
    }

    private int randomInclusive(int minCapacity, int maxCapacity) {
        return random.ints(minCapacity, maxCapacity + 1) // Caution, we want random numbers inclusive both lower and upper bound!
                .findFirst()
                .getAsInt();
    }

    /**
     * Fixed list of first 80 coffee names coming from https://hub.cropster.com/store, removed two strange :-)
     */
    private List<String> readCoffeeNames() throws IOException {
        String fileContent = new String(getClass().getClassLoader().getResourceAsStream("coffee_names.txt").readAllBytes());
        return Collections.unmodifiableList(Arrays.asList(fileContent.split("\n")));
    }


    @Test
    public void runDemoRoasting() {

        createDemoFacilities();

        List<Facility> availableFacilities = new ArrayList<>(facilityRepository.findAll());
        Collections.shuffle(availableFacilities);

        availableFacilities
                .forEach(this::roastEverything);
    }

    private void roastEverything(Facility facility) {

        FacilityManagement management = facilityService.getForFacility(facility);
        List<GreenCoffee> stock = management.getAvailableGreenCoffee();
        int totallyAvailableGreenCoffee = stock.stream().mapToInt(GreenCoffee::getStock).sum();



        List<Machine> machines = management.getAllMachines();

        Machine machine = pickMachine(machines);
        roastOnMachine(management, machine, stock);
    }

    private void roastOnMachine(FacilityManagement management, Machine machine, List<GreenCoffee> stock) {
        GreenCoffee greenCoffee = pickCoffee(stock);
        int capacity = machine.getCapacity();

        int startWeight = randomInclusive((int) Math.ceil(0.65 * capacity), capacity);
        int roastDuration = randomInclusive(5, 15);
        int weightLossInPercent = randomInclusive(8, 15);
        Date startDate = new Date();

        RoastConfiguration configuration = new RoastConfiguration("some roast", machine, greenCoffee, startWeight, startDate, roastDuration, weightLossInPercent);
        RoastingProcess roastingProcess = management.roast(configuration);

        logger.info("Facility “{}: roasted {}kgs of '{}' on {}", management.getFacility().getName(), startWeight, greenCoffee.getName(), machine.getName());
    }

    private GreenCoffee pickCoffee(List<GreenCoffee> stock) {
        return stock.stream().findFirst().get(); // TODO!
    }

    private Machine pickMachine(List<Machine> machines) {
        return machines.stream().findFirst().get(); // TODO!
    }
}
