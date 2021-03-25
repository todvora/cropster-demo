package cz.tomasdvorak.roastingfacility.repositories;

import cz.tomasdvorak.roastingfacility.entities.Facility;
import cz.tomasdvorak.roastingfacility.entities.GreenCoffee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GreenCoffeeRepository extends JpaRepository<GreenCoffee, Long> {

    List<GreenCoffee> findByFacility(@Param("facility") final Facility facility);

    @Query("select c from GreenCoffee c where c.facility = :facility AND stock > 0")
    List<GreenCoffee> getAvailableByFacility(Facility facility);
}
