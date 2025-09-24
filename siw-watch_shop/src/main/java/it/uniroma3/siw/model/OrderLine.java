package it.uniroma3.siw.model;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class OrderLine {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    private Integer quantity;
    private Float unitPrice;
    
    private String watchName;
    private String watchBrand; 
    private String watchDescription;
    private Integer watchYear;
    private String watchImagePath;
    
    
    @ManyToOne
    @JoinColumn(name = "watch_id")
    private Watch watch;
    
    public void increaseQuantityByOne() {
        this.quantity += 1;
    }
    
    public void decreaseQuantity() {
        if(this.quantity > 0)
            this.quantity -= 1;
    }
    
    public Float getTotalPrice() {
        if (unitPrice == null || quantity == null) {
            return 0.0f;
        }
        return unitPrice * quantity;
    }
    
    public boolean matchesWatch(Watch watch) {
        if (watch == null) return false;
        
        if (this.watch != null && this.watch.getId().equals(watch.getId())) {
            return true;
        }
        
        return watchName != null && watchName.equals(watch.getName()) &&
               watchBrand != null && watchBrand.equals(watch.getBrand()) &&
               watchYear != null && watchYear.equals(watch.getYear());
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderLine that = (OrderLine) o;

        if (this.watch != null && that.watch != null) {
            return this.watch.equals(that.watch);
        }
        
        return watchName != null && watchName.equals(that.watchName) &&
               watchBrand != null && watchBrand.equals(that.watchBrand) &&
               watchYear != null && watchYear.equals(that.watchYear);
    }

    @Override
    public int hashCode() {
        if (watch != null) {
            return watch.hashCode();
        }
        return Objects.hash(watchName, watchBrand, watchYear);
    }
}