package it.uniroma3.siw.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.uniroma3.siw.model.Order;
import it.uniroma3.siw.model.OrderStatus;
import it.uniroma3.siw.model.User;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long>{

	public Optional<Order> findByCode(String code);
	
	public boolean existsByCode(String code);
	
	public List<Order> findByStatus(OrderStatus status);
	
	@Query("SELECT o FROM Order o WHERE o.user = :user")
	public List<Order> findByUser(@Param("user") User user);
	
	@Query("SELECT o FROM Order o WHERE o.user = :user AND o.status = :status")
	public List<Order> findByUserAndStatus(@Param("user") User user, @Param("status") OrderStatus status);
}
