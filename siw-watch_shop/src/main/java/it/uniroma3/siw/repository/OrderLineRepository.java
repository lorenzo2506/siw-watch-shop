package it.uniroma3.siw.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.uniroma3.siw.model.Order;
import it.uniroma3.siw.model.OrderLine;


@Repository
public interface OrderLineRepository extends JpaRepository<OrderLine,Long>{

	
}
