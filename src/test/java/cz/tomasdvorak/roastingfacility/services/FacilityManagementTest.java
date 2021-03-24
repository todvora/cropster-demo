package cz.tomasdvorak.roastingfacility.services;

import cz.tomasdvorak.roastingfacility.entities.Facility;
import cz.tomasdvorak.roastingfacility.entities.GreenCoffee;
import cz.tomasdvorak.roastingfacility.entities.Machine;
import cz.tomasdvorak.roastingfacility.repositories.FacilityRepository;
import cz.tomasdvorak.roastingfacility.services.roasting.IllegalRoastingConfiguration;
import cz.tomasdvorak.roastingfacility.services.roasting.RoastConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class FacilityManagementTest {

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private FacilityService facilityService;

    private FacilityManagementImpl facilityManagement;

    @BeforeEach
    public void createDemoFacility() {
        Facility facility = facilityRepository.save(new Facility("junit facility"));
        facilityManagement = facilityService.getForFacility(facility);
    }

    @Test
    public void addToStock() {
        List<GreenCoffee> stock = facilityManagement.getGreenCoffeeStock();
        assertEquals(stock.size(), 0);
        GreenCoffee greenCoffee1 = facilityManagement.addToStock("Bildimoo Nensebo #1", 600);
        GreenCoffee greenCoffee2 = facilityManagement.addToStock("Dhilgee Uraga Raro #1", 400);
        List<GreenCoffee> currentStock = facilityManagement.getGreenCoffeeStock();

        // we should have two types of green coffee
        assertEquals(currentStock.size(), 2);

        // with totally 1000kg weight
        assertEquals(1000, currentStock.stream().mapToInt(GreenCoffee::getStock).sum());
    }
    @Test
    public void updateStock() {
        GreenCoffee greenCoffee = facilityManagement.addToStock("Bildimoo Nensebo #1", 600);
        Assertions.assertEquals(600, facilityManagement.getGreenCoffeeStock().stream().mapToInt(GreenCoffee::getStock).sum());
        facilityManagement.updateStock(greenCoffee, 550);
        Assertions.assertEquals(550, facilityManagement.getGreenCoffeeStock().stream().mapToInt(GreenCoffee::getStock).sum());
    }

    @Test
    public void testIllegalAmount() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> facilityManagement.addToStock("Bildimoo Nensebo #1", -5));
    }

    @Test
    public void addMachine() {
        facilityManagement.addRoastingMachine("junit machine #1", 16);
        facilityManagement.addRoastingMachine("junit machine #2", 60);
        facilityManagement.addRoastingMachine("junit machine #3", 90);

        List<Machine> allMachines = facilityManagement.getAllMachines();
        Assertions.assertEquals(3, allMachines.size());
        Assertions.assertEquals(166, allMachines.stream().mapToInt(Machine::getCapacity).sum());
    }

    @Test
    public void addMachineNonuniqueName() {
        facilityManagement.addRoastingMachine("junit machine #1", 16);
        // now use the very same machine name, should throw an exception since the name needs to be unique
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> facilityManagement.addRoastingMachine("junit machine #1", 60));
    }

    @Test
    public void roast() throws IllegalRoastingConfiguration {

        facilityManagement.addRoastingMachine("junit machine #1", 60);
        facilityManagement.addToStock("Bildimoo Nensebo #1", 600);

        Machine roastMachine = facilityManagement.getAllMachines().stream().findAny().orElseThrow(() -> new RuntimeException("No roasting machine available"));
        GreenCoffee greenCoffee = facilityManagement.getAvailableGreenCoffee().stream().findAny().orElseThrow(() -> new RuntimeException("No green coffee available"));
        RoastConfiguration configuration = new RoastConfiguration(roastMachine, greenCoffee, 50, 10);

        configuration.validate();

        facilityManagement.roast(configuration);
    }

}