package it.uniroma3.siw.model;



import java.util.Objects;
import java.util.Random;

import jakarta.persistence.Column;
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
	
	@Column(unique = true, nullable = false)
	private String code;
	
	@NotNull
	@Min(1800)
	@Max(2025)
	private Integer year;
	
	public Watch() {
		this.code = generateWatchCode();
	}
	
	
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;

	    Watch watch = (Watch) o;

	    return Objects.equals(brand, watch.brand) &&
	           Objects.equals(name, watch.name) &&
	           Objects.equals(year, watch.year);
	}

	@Override
	public int hashCode() {
	    return Objects.hash(brand, name, year);
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
	
	private String generateWatchCode() {
        return "WATCH-" + System.currentTimeMillis() + "-" + 
               String.format("%04d", new Random().nextInt(10000));
    }
	

}
