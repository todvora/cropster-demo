package cz.tomasdvorak.roastingfacility.repositories;

import cz.tomasdvorak.roastingfacility.entities.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {
}
