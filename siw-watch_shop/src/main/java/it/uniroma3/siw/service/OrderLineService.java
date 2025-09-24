package it.uniroma3.siw.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.OrderLine;
import it.uniroma3.siw.model.Watch;
import it.uniroma3.siw.repository.OrderLineRepository;
import jakarta.transaction.Transactional;

@Service
public class OrderLineService {

    @Autowired 
    private OrderLineRepository orderLineRepository;
    
    public List<OrderLine> getAllLines() {
        return orderLineRepository.findAll();
    }
    
    public void save(OrderLine orderLine) {
        orderLineRepository.save(orderLine);
    }
    
    public void delete(OrderLine orderLine) {
        orderLineRepository.delete(orderLine);
    }
    
    public Optional<OrderLine> findById(Long id) {
        return orderLineRepository.findById(id);
    }
    
    public OrderLine getById(Long id) {
        return orderLineRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("OrderLine con ID " + id + " non trovata"));
    }
    
    
    public OrderLine createOrderLineFromWatch(Watch watch, int quantity) {
        if (watch == null) {
            throw new IllegalArgumentException("Watch non può essere null");
        }
        
        OrderLine orderLine = new OrderLine();
        
        orderLine.setWatchName(watch.getName());
        orderLine.setWatchBrand(watch.getBrand());
        orderLine.setWatchDescription(watch.getDescription());
        orderLine.setWatchYear(watch.getYear());
        orderLine.setWatchImagePath(watch.getImagePath());
        
        orderLine.setQuantity(quantity);
        orderLine.setUnitPrice(watch.getPrice());
        
        orderLine.setWatch(watch);
        
        return orderLine;
    }
    
    
    @Transactional
    public void increaseQuantity(Long orderLineId) {
        OrderLine orderLine = getById(orderLineId);
        orderLine.increaseQuantityByOne();
        save(orderLine);
    }
    
    
    @Transactional
    public void decreaseQuantity(Long orderLineId) {
        OrderLine orderLine = getById(orderLineId);
        orderLine.decreaseQuantity();
        save(orderLine);
    }
    
    
    public boolean shouldRemoveOrderLine(OrderLine orderLine) {
        return orderLine.getQuantity() <= 1;
    }
    
    
    @Transactional
    public void deleteById(Long id) {
        OrderLine orderLine = getById(id);
        delete(orderLine);
    }
}