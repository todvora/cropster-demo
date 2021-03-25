package cz.tomasdvorak.roastingfacility.repositories;

import cz.tomasdvorak.roastingfacility.entities.Facility;
import cz.tomasdvorak.roastingfacility.entities.RoastingProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoastingProcessRepository extends JpaRepository<RoastingProcess, Long> {
    List<RoastingProcess> findByFacility(Facility facility);
}
