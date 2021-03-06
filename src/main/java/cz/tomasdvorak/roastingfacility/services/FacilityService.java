package cz.tomasdvorak.roastingfacility.services;

import cz.tomasdvorak.roastingfacility.entities.Facility;
import cz.tomasdvorak.roastingfacility.repositories.GreenCoffeeRepository;
import cz.tomasdvorak.roastingfacility.repositories.MachineRepository;
import cz.tomasdvorak.roastingfacility.repositories.RoastingProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Facility service is mainly a wrapper for repositories and services based around one facility.
 */
@Service
public class FacilityService {

    @Autowired
    private GreenCoffeeRepository greenCoffeeRepository;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private RoastingProcessRepository roastingProcessRepository;

    /**
     * We always want to operate on one facility, let's bind it here so we don't need to provide the facility
     * param for each operation in each service
     */
    public FacilityManagement getForFacility(Facility facility) {
        return new FacilityManagementImpl(facility, greenCoffeeRepository, machineRepository, roastingProcessRepository);
    }
}
