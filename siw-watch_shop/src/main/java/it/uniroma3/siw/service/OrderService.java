package it.uniroma3.siw.service;

import java.time.LocalDateTime;
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
    @Autowired private OrderLineService orderLineService;

    public Order getById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order getByCode(String code) {
        return orderRepository.findByCode(code).orElse(null);
    }

    public Iterable<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Iterable<OrderLine> getAllOrderLinesById(Long id) {
        Order order = this.getById(id);
        if (order == null)
            throw new IllegalStateException("##Nessun ordine relativo all'id nel parametro.");
        return order.getOrderLines();
    }

    /**
     * Crea un nuovo ordine in stato "IN_CREAZIONE" per l'utente, solo se non ne ha già uno.
     */
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

        if (currentUser.getCurrentOrder() == null || !OrderStatus.IN_CREAZIONE.equals(currentUser.getCurrentOrder().getStatus()))
            return createNewCurrentOrder(currentUser);

        // IMPORTANTE: Ricarica l'Order dal database per avere lo stato aggiornato
        return this.orderRepository.findById(currentUser.getCurrentOrder().getId())
                   .orElseThrow(() -> new IllegalStateException("Order non trovato"));
    }

    
    
    public OrderLine getOrderLineWithWatch(Order order, Watch watch) {
        List<OrderLine> orderLines = order.getOrderLines();
        if (orderLines == null)
            throw new IllegalStateException("##OrderLines non inizializzate.");

        return orderLines.stream()
                .filter(ol -> watch != null && watch.equals(ol.getWatch()))
                .findFirst()
                .orElse(null);
    }

    
    
    public Iterable<Order> getAllPlacedOrders() {
        User currentUser = authService.getCurrentUser();
        return orderRepository.findByUserAndStatus(currentUser, OrderStatus.EFFETTUATO);
    }
    
    
    
    public OrderLine existingOrderLineInOrder(Order order, Watch watch) {
        return order.getOrderLines().stream()
            .filter(ol -> ol.getWatch() != null && watch != null && watch.getId().equals(ol.getWatch().getId()))
            .findFirst()
            .orElse(null);
    }

    
    
    
    @Transactional
    public void addWatchToCurrentOrder(Long id) {
        Order currentOrder = this.getCurrentOrder();
        
        // Forza il refresh per assicurarti di avere lo stato più aggiornato
       
        
        Watch currentWatch = watchService.getAvailableWatch(id);
        OrderLine existingOrderLine = this.existingOrderLineInOrder(currentOrder, currentWatch);

        if (existingOrderLine != null) {
            existingOrderLine.increaseQuantityByOne();
        } else {
            OrderLine newOrderLine = new OrderLine();
            newOrderLine.setWatch(currentWatch);
            newOrderLine.setQuantity(1);
            newOrderLine.setUnitPrice(currentWatch.getPrice());
            currentOrder.getOrderLines().add(newOrderLine);
        }
        
        currentOrder.setTotalPrice( currentOrder.calculateTotalPrice() );
        this.save(currentOrder);
    }
    

    @Transactional
    public void confirmOrder() {
        Order currentOrder = this.getCurrentOrder();
        User currentUser = authService.getCurrentUser();

        if (currentOrder.getOrderLines().isEmpty()) {
            throw new IllegalStateException("Carrello vuoto!");
        }

        currentOrder.setStatus(OrderStatus.EFFETTUATO);
        currentOrder.setCode(generateOrderCode());
        currentOrder.setCreationTime(LocalDateTime.now());

        this.save(currentOrder);
        createNewCurrentOrder(currentUser); // nuovo ordine dopo conferma
    }
    
    
    public void removeOrderLine(Long id) {
    	Order order = this.getCurrentOrder();
    	OrderLine orderLine = orderLineService.getById(id);
    	
    	if(orderLine==null)
    		throw new IllegalArgumentException("riga d'ordine nulla");
    	
    	if(!(this.orderLineService.getAllLines().contains(orderLine)))
    		throw new IllegalArgumentException("la riga d'ordine non appartiene all ordine");
    	
    	if(orderLine.getQuantity()==1) {
	    	order.getOrderLines().remove(orderLine);
	    	orderLineService.delete(orderLine);
    	} else {
    		orderLine.decreaseQuantity();
    		orderLineService.save(orderLine);
    	}
    	
    	
    	order.calculateTotalPrice();
    	this.save(order); 
    	
    }

    private String generateOrderCode() {
        return "ORD-" + System.currentTimeMillis() + "-" +
               String.format("%04d", new Random().nextInt(10000));
    }

    public void save(Order order) {
        orderRepository.save(order);
    }
}
