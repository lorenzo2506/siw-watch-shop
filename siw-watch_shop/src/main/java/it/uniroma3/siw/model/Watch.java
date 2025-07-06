package it.uniroma3.siw.model;



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
public class Watch {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@NotBlank
	private String name;
	
	
	private Float price;
	
	@NotBlank
	private String brand;
	
	private String url_image;
	
	private String description;
	
	private Integer stock;
	
	@NotNull
	@Min(1800)
	@Max(2025)
	private Integer year;
	
	@Override
	public boolean equals(Object o) {
		
		if(o==this)
			return true;
		if(o==null || o.getClass()!=this.getClass())
			return false;
		
		Watch watch = (Watch) o;
		return this.getName().equals(watch.getName()) && this.getBrand().equals(watch.getBrand()) && this.getYear()==watch.getYear();
	}
	
	
	@Override
	public int hashCode() {
		return this.getName().hashCode() + this.getBrand().hashCode() + this.getYear();
	}
	
	
	public boolean isAvailable() {
		return this.stock > 0;
	}
	
	public void incrementStock() {
		this.setStock(this.getStock()+1);
	}
	
	public void decrementStock() {
		if(this.isAvailable())
			this.setStock(this.getStock()-1);
	}
	

}
