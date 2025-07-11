package it.uniroma3.siw.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.annotations.Fetch;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name="orders")
public class Order {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	
	// Usa @Fetch o query personalizzate
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name="order_id")
	private List<OrderLine> orderLines;
	
	@OneToMany(cascade=CascadeType.ALL)
	private List <Watch> watches;
	
	private LocalDateTime creationTime;
	
	private Float totalPrice;
	
	private String code;
	
    @Enumerated(EnumType.STRING)
	private OrderStatus status;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    
 // Assicurati che la sessione sia aperta o usa @Transactional
    @Transactional
    public Float calculateTotal() {
        return this.orderLines.stream()
            .map(OrderLine::calculateTotal)
            .reduce(0f, Float::sum);
    }
    
    public Order() {
    	
    	watches = new ArrayList<>();
    	
    	orderLines = new ArrayList<>();
    }
}
