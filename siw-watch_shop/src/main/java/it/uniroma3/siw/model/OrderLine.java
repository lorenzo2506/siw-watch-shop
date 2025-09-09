package it.uniroma3.siw.model;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class OrderLine {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	
	private Integer quantity;
	
	private Float unitPrice;
	
	@ManyToOne  // Più sicuro e flessibile
    @JoinColumn(name = "watch_id")
	private Watch watch;
	
	
	public void increaseQuantityByOne() {
		this.quantity+=1;
	}
	
	public void decreaseQuantity() {
		if(this.quantity>0)
			this.quantity-=1;
	}
	
	
	 public Float getTotalPrice() {
	        // 🔥 CONTROLLO NULL SAFETY
	        if (unitPrice == null || quantity == null) {
	            return 0.0f;
	        }
	        return unitPrice * quantity;
	    }     

	
	
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;

	    OrderLine that = (OrderLine) o;

	    // Confronta per Watch (non id, se l'entità è transitoria)
	    return this.watch != null && this.watch.equals(that.watch);
	}

	@Override
	public int hashCode() {
	    return watch != null ? watch.hashCode() : 0;
	}

	
}
