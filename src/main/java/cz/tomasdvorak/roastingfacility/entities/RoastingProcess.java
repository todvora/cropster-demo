package cz.tomasdvorak.roastingfacility.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
public class RoastingProcess {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Product name is optional
     */
    @Column(nullable = true)
    private String productName;

    private int startWeight;
    private int endWeight;
    private Date startTime;
    private Date endTime;

    @ManyToOne
    @JoinColumn(name = "green_coffee_id")
    private GreenCoffee greenCoffee;

    @ManyToOne
    @JoinColumn(name = "facility_id")
    private Facility facility;


    /**
     * JPA constructor without params
     */
    public RoastingProcess() {
    }

    public RoastingProcess(String productName, int startWeight, int endWeight, Date startTime, Date endTime, GreenCoffee greenCoffee, Facility facility) {
        this.productName = productName;
        this.startWeight = startWeight;
        this.endWeight = endWeight;
        this.startTime = startTime;
        this.endTime = endTime;
        this.greenCoffee = greenCoffee;
        this.facility = facility;
    }

    public Long getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public int getStartWeight() {
        return startWeight;
    }

    public int getEndWeight() {
        return endWeight;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public GreenCoffee getGreenCoffee() {
        return greenCoffee;
    }

    public Facility getFacility() {
        return facility;
    }
}