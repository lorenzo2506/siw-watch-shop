package it.uniroma3.siw.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	
	@OneToOne
	private Watch watch;
	
	
	public void increaseQuantity() {
		this.setQuantity( this.getQuantity()+1);
	}
	
	public Float calculateTotal() {
		return unitPrice*quantity;
	}
	
}
