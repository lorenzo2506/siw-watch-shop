package it.uniroma3.siw.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name="users")
public class User {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@NotNull
	private String name;
	
	@NotNull
	private String surname;

	@OneToOne
	private Order currentOrder;
	
	/*@OneToMany(mappedBy="user", fetch = FetchType.EAGER)
	private List<Review> reviews;*/

	// Cambiato FetchType per evitare LazyInitializationException
	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
	private List<Order> placedOrders;

	public User() {
		this.placedOrders = new ArrayList<>();
	}

	// Override toString per evitare problemi con Hibernate lazy loading
	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", name='" + name + '\'' +
				", surname='" + surname + '\'' +
				", currentOrder=" + (currentOrder != null ? currentOrder.getId() : "null") +
				", placedOrdersCount=" + (placedOrders != null ? placedOrders.size() : 0) +
				'}';
	}
}