package cz.tomasdvorak.roastingfacility.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class RoastingProcess {

    @Id
    @GeneratedValue
    private Long id;
    private String productName;
    private int startWeight;
    private int endWeight;
    private Date startTime;
    private Date endTime;
    private Long greenCoffee;

}