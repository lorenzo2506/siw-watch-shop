package it.uniroma3.siw.model;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
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
	
	@NotNull
	@Min(0)
	private Float price;
	
	@NotBlank
	private String brand;
	
	private String description;
	
	private Integer stock;
	
	// Campo per l'immagine
    private String imagePath;
    
    @Column(name = "availability")
    private boolean availability = true;
	
    
    @OneToMany(mappedBy="watch", cascade=CascadeType.ALL)
    private List<Review> reviews;
	
	@NotNull
	@Min(1800)
	@Max(2025)
	private Integer year;
	
	
	private Integer ratingCount;
	
	private Float averageRating;
	
	
	
	public Watch() {
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
