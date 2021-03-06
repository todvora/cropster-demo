package cz.tomasdvorak.roastingfacility.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class GreenCoffee {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private int stock;

    @ManyToOne
    @JoinColumn(name = "facility_id")
    private Facility facility;


    @OneToMany(mappedBy = "greenCoffee")
    private List<RoastingProcess> roastingProcesses;

    /**
     * JPA constructor
     */
    public GreenCoffee() {
    }

    public GreenCoffee(Facility facility, String greenCoffeeName, int amountInKgs) {
        this.facility = facility;
        this.name = greenCoffeeName;
        this.stock = amountInKgs;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getStock() {
        return stock;
    }

    public Facility getFacility() {
        return facility;
    }
}