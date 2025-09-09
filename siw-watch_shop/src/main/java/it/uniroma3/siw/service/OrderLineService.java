package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.OrderLine;
import it.uniroma3.siw.repository.OrderLineRepository;

@Service
public class OrderLineService {

	@Autowired private OrderLineRepository orderLineRepository;
	
	public List<OrderLine> getAllLines() {
		return orderLineRepository.findAll();
	}
	
	public void save(OrderLine orderLine) {
		orderLineRepository.save(orderLine);
	}
	
	public void delete(OrderLine orderLine) {
		orderLineRepository.delete(orderLine);
	}
	
	public OrderLine getById(Long id) {
		return this.orderLineRepository.findById(id).get();
	}
}
