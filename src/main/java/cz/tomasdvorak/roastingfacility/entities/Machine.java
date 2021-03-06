package cz.tomasdvorak.roastingfacility.entities;

import javax.persistence.*;

@Table(uniqueConstraints=@UniqueConstraint(columnNames={"name", "facility_id"}))
@Entity
public class Machine {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    /**
     *  The capacity determines how many kg of coffee one maximally can roast per roasting process
     */
    private int capacity;

    @ManyToOne
    @JoinColumn(name = "facility_id")
    private Facility facility;


    /**
     * JPA constructor
     */
    public Machine() {
    }

    public Machine(Facility facility, String name, int capacity) {
        this.facility = facility;
        this.name = name;
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }
}