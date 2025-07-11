package it.uniroma3.siw.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Order;
import it.uniroma3.siw.model.OrderLine;
import it.uniroma3.siw.model.OrderStatus;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.model.Watch;
import it.uniroma3.siw.repository.OrderRepository;
import it.uniroma3.siw.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class OrderService {
	
	@Autowired private OrderRepository orderRepository;
	
	@Autowired private AuthenticationService authService;
	
	@Autowired private WatchService watchService;
	
	@Autowired private UserRepository userService;
	
	public Order getById(Long id) {
		return orderRepository.findById(id).get();
	}
	
	
	public Order getByCode(String code) {
		return orderRepository.findByCode(code).get();
	}
	
	
	public Iterable<Order> getAllOrders() {
		return orderRepository.findAll();
	}
	
	@Transactional
	public Iterable<OrderLine> getAllOrderLinesById(Long id) {
		if(this.getById(id)==null)
            throw new IllegalStateException("##Nessun ordine relativo all'id nel parametro.");

			
		return this.getById(id).getOrderLines();
	}
	
	@Transactional
	private Order createNewCurrentOrder(User user) {
	    Order newOrder = new Order();
	    newOrder.setStatus(OrderStatus.IN_CREAZIONE);
	    newOrder.setUser(user);
	    user.setCurrentOrder(newOrder);
	    
	    Order savedOrder = orderRepository.save(newOrder);
	    userService.save(user);
	    
	    return savedOrder;
	}
	
	@Transactional
	public Order getCurrentOrder() {
		
        User currentUser = this.authService.getCurrentUser();
        if (currentUser == null)
            throw new IllegalStateException("##Utente non autenticato nella funzione getCurrentOrder.");
            
        if(currentUser.getCurrentOrder()==null)
        	return createNewCurrentOrder(currentUser);
        
        System.out.println("Righe presenti: " +currentUser.getCurrentOrder().getOrderLines().size());
        return currentUser.getCurrentOrder();
    }
	
		
	public OrderLine getOrderLineWithWatch(Order order, Watch watch) {
		
		List<OrderLine> orderLines= order.getOrderLines();
		if(orderLines==null)
            throw new IllegalStateException("##OrderLines non inizializzate.");
		
		for(OrderLine orderLine: orderLines)
			if(orderLine.getWatch() == watch)
				return orderLine;
		
		return null;
	}
	
	@Transactional	
	public Iterable<Order> getAllPlacedOrders() {
		
		User currentUser = authService.getCurrentUser();
		return this.orderRepository.findByUserAndStatus(currentUser, OrderStatus.EFFETTUATO);
	}
	
	
	
	
	public OrderLine existingOrderLineInOrder(Order order, Watch watch) {
	    for (OrderLine ol : order.getOrderLines()) {
	        Watch w = ol.getWatch();
	        System.out.println("→ Confronto:");
	        System.out.println("   OL: brand=" + w.getBrand() + ", name=" + w.getName() + ", year=" + w.getYear());
	        System.out.println("   CW: brand=" + watch.getBrand() + ", name=" + watch.getName() + ", year=" + watch.getYear());
	        System.out.println("   equals? " + watch.equals(w));
	    }

	    return order.getOrderLines().stream()
	        .filter(ol -> ol.getWatch() != null && watch != null && watch.equals(ol.getWatch()))
	        .findFirst()
	        .orElse(null);
	}

	
	
	
	@Transactional
	public void addWatchToCurrentOrder(Long id) {
	    Order currentOrder = this.getCurrentOrder();
	    Watch currentWatch = watchService.getWatch(id);
	    
	    
	    OrderLine existingOrderLine = this.existingOrderLineInOrder(currentOrder, currentWatch);
	    
	    if (existingOrderLine != null) {
	        existingOrderLine.increaseQuantityByOne();
	        System.out.println("################################################################");
	        System.out.println("################################################################");
	        System.out.println("################################################################");
	        System.out.println("ESISTE GIA NELL ORDINE");
	        System.out.println("################################################################");
	        System.out.println("################################################################");
	        System.out.println("################################################################");

	    }
	    else {
	        OrderLine newOrderLine = new OrderLine();
	        newOrderLine.setWatch(currentWatch);
	        newOrderLine.setQuantity(1);
	        newOrderLine.setUnitPrice(currentWatch.getPrice());
	        currentOrder.getOrderLines().add(newOrderLine);
	    }
	    
	    this.save(currentOrder);
	    
	    System.out.println("==== STATO FINALE DELL’ORDINE ====");
	    for (OrderLine ol : currentOrder.getOrderLines()) {
	        Watch w = ol.getWatch();
	        System.out.println("→ OL: brand=" + w.getBrand() + ", name=" + w.getName() + ", year=" + w.getYear() + ", quantity=" + ol.getQuantity());
	    }

	}
	
	
	@Transactional
	public void confirmOrder() {
		
		Order currentOrder = this.getCurrentOrder();
		User currentUser = authService.getCurrentUser();
		// Validazioni
		
        if (currentOrder.getOrderLines().isEmpty()) {
            throw new IllegalStateException("Carrello vuoto!");
        }
        
        currentOrder.setStatus(OrderStatus.EFFETTUATO);
        currentOrder.setCode( this.generateOrderCode() );
        currentOrder.setCreationTime(LocalDateTime.now());
        
        
        ((List<Order>) this.getAllPlacedOrders()).add(currentOrder);
        createNewCurrentOrder(currentUser);
        
        this.save(currentOrder);
        
	}
	
	
	private String generateOrderCode() {
        return "ORD-" + System.currentTimeMillis() + "-" + 
               String.format("%04d", new Random().nextInt(10000));
    }
	
	
	
	public void save(Order order) {
		orderRepository.save(order);
	}

}
