package cz.tomasdvorak.roastingfacility.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Facility {

    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @OneToMany(mappedBy = "facility")
    private List<GreenCoffee> greenCoffeeStock = new ArrayList<>();

    @OneToMany(mappedBy = "facility")
    private List<Machine> machines = new ArrayList<>();


    @OneToMany(mappedBy = "facility")
    private List<RoastingProcess> processes = new ArrayList<>();


    public Facility() {
    }

    public Facility(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}