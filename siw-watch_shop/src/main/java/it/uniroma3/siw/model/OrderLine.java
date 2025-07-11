package it.uniroma3.siw.model;

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
	
	public Float calculateTotal() {
		return unitPrice*quantity;
	}
	
}
