package it.uniroma3.siw.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Order;
import it.uniroma3.siw.model.OrderLine;
import it.uniroma3.siw.model.OrderStatus;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.model.Watch;
import it.uniroma3.siw.repository.OrderLineRepository;
import it.uniroma3.siw.repository.OrderRepository;
import it.uniroma3.siw.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private AuthenticationService authService;
    @Autowired private WatchService watchService;
    @Autowired private UserRepository userRepository;
    @Autowired private OrderLineService orderLineService;
    @Autowired private OrderLineRepository orderLineRepository;

    public Order getById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order getByCode(String code) {
        return orderRepository.findByCode(code).orElse(null);
    }

    public Iterable<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public List<Order> getAllCurrentOrder() {
    	return this.orderRepository.findByStatus(OrderStatus.IN_CREAZIONE);
    }
    
    public List<Order> getAllByStatus(OrderStatus orderStatus) {
    	return this.orderRepository.findByStatus(orderStatus);
    }

    public Iterable<OrderLine> getAllOrderLinesById(Long id) {
        Order order = getById(id);
        if (order == null)
            throw new IllegalStateException("Nessun ordine trovato con ID: " + id);
        return order.getOrderLines();
    }

    /**
     * ✅ Crea un nuovo ordine in stato "IN_CREAZIONE" per l'utente
     */
    private Order createNewCurrentOrder(User user) {
        Order newOrder = new Order();
        newOrder.setStatus(OrderStatus.IN_CREAZIONE);
        newOrder.setUser(user);
        newOrder.setTotalPrice(0.0f);
        user.setCurrentOrder(newOrder);

        Order savedOrder = orderRepository.save(newOrder);
        userRepository.save(user);

        return savedOrder;
    }

    /**
     * ✅ Ottiene l'ordine corrente dell'utente (carrello)
     */
    @Transactional
    public Order getCurrentOrder() {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null)
            throw new IllegalStateException("Utente non autenticato");

        if (currentUser.getCurrentOrder() == null || 
            !OrderStatus.IN_CREAZIONE.equals(currentUser.getCurrentOrder().getStatus())) {
            return createNewCurrentOrder(currentUser);
        }

        // Ricarica l'Order dal database per avere lo stato aggiornato
        return orderRepository.findById(currentUser.getCurrentOrder().getId())
                   .orElseThrow(() -> new IllegalStateException("Ordine corrente non trovato"));
    }

    /**
     * ✅ Trova una OrderLine esistente che corrisponde al Watch specificato
     * Usa la nuova logica di confronto con snapshot dei dati
     */
    public OrderLine findExistingOrderLine(Order order, Watch watch) {
        if (order.getOrderLines() == null || watch == null) {
            return null;
        }

        return order.getOrderLines().stream()
                .filter(orderLine -> {
                    // Prima controlla il riferimento diretto se esiste
                    if (orderLine.getWatch() != null && orderLine.getWatch().getId().equals(watch.getId())) {
                        return true;
                    }
                    
                    // Altrimenti confronta per dati snapshot
                    return watch.getName().equals(orderLine.getWatchName()) &&
                           watch.getBrand().equals(orderLine.getWatchBrand()) &&
                           watch.getYear().equals(orderLine.getWatchYear());
                })
                .findFirst()
                .orElse(null);
    }

    /**
     * ✅ Ottiene tutti gli ordini effettuati dall'utente corrente
     */
    public Iterable<Order> getAllPlacedOrders() {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("Utente non autenticato");
        }
        return orderRepository.findByUserAndStatus(currentUser, OrderStatus.EFFETTUATO);
    }

    /**
     * ✅ Aggiunge un Watch all'ordine corrente
     */
    @Transactional
    public void addWatchToCurrentOrder(Long watchId) {
        Order currentOrder = getCurrentOrder();
        Watch watch = watchService.getAvailableWatch(watchId);
        
        if (watch == null) {
            throw new IllegalArgumentException("Watch con ID " + watchId + " non trovato o non disponibile");
        }

        OrderLine existingOrderLine = findExistingOrderLine(currentOrder, watch);

        if (existingOrderLine != null) {
            // Se esiste già, aumenta la quantità
            existingOrderLine.increaseQuantityByOne();
            orderLineService.save(existingOrderLine);
        } else {
            // Crea una nuova OrderLine con snapshot dei dati
            OrderLine newOrderLine = orderLineService.createOrderLineFromWatch(watch, 1);
            currentOrder.getOrderLines().add(newOrderLine);
        }

        // Ricalcola il totale e salva
        currentOrder.setTotalPrice(currentOrder.calculateTotalPrice());
        save(currentOrder);
    }

    /**
     * ✅ Rimuove una OrderLine dall'ordine corrente
     */
    @Transactional
    public void removeOrderLine(Long orderLineId, Order order) {
        Order currentOrder = order;
        OrderLine orderLine = orderLineService.getById(orderLineId);

        if (!currentOrder.getOrderLines().contains(orderLine)) {
            throw new IllegalArgumentException("La riga d'ordine non appartiene all'ordine corrente");
        }

        if (orderLineService.shouldRemoveOrderLine(orderLine)) {
            // Rimuovi completamente la OrderLine
            currentOrder.getOrderLines().remove(orderLine);
            orderLineService.delete(orderLine);
        } else {
            // Diminuisci solo la quantità
            orderLine.decreaseQuantity();
            orderLineService.save(orderLine);
        }

        // Ricalcola il totale e salva
        currentOrder.setTotalPrice(currentOrder.calculateTotalPrice());
        save(currentOrder);
    }
    
    
    @Transactional
    public void removeAllWatchOrderLinesToCarts(Watch watch) {
        
        if(watch == null)
            throw new IllegalArgumentException("watch nullo");
        
        List<Order> currentOrders = this.getAllCurrentOrder();
        
        for(Order order : currentOrders) {
            OrderLine orderLineToRemove = this.findExistingOrderLine(order, watch);
            
            while(orderLineToRemove != null) {                
                this.removeOrderLine(orderLineToRemove.getId(), order);
                orderLineToRemove = this.findExistingOrderLine(order, watch);   
            } 
        }
        
        //toglie il watch ma mantiene gli snapshow
        List<OrderLine> orderLinesWithThisWatch = orderLineRepository.findByWatch(watch);
        for(OrderLine orderLine : orderLinesWithThisWatch) {
            // Mantieni tutti i dati snapshot (watchName, watchBrand, etc.)
            // ma rimuovi il riferimento FK per permettere l'eliminazione del watch
            orderLine.setWatch(null); 
            orderLineService.save(orderLine);
        }
    }

    
    @Transactional
    public void confirmOrder() {
        Order currentOrder = getCurrentOrder();
        User currentUser = authService.getCurrentUser();

        if (currentOrder.getOrderLines().isEmpty()) {
            throw new IllegalStateException("Impossibile confermare un carrello vuoto");
        }

        // Aggiorna lo stato dell'ordine
        currentOrder.setStatus(OrderStatus.EFFETTUATO);
        currentOrder.setCode(generateOrderCode());
        currentOrder.setCreationTime(LocalDateTime.now());
        currentOrder.setTotalPrice(currentOrder.calculateTotalPrice());

        save(currentOrder);

        // Crea un nuovo ordine corrente per l'utente
        createNewCurrentOrder(currentUser);
    }

    /**
     * ✅ Calcola il numero totale di articoli nell'ordine corrente
     */
    public int getCurrentOrderItemCount() {
        try {
            Order currentOrder = getCurrentOrder();
            return currentOrder.getOrderLines().stream()
                    .mapToInt(OrderLine::getQuantity)
                    .sum();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * ✅ Verifica se l'ordine corrente è vuoto
     */
    public boolean isCurrentOrderEmpty() {
        try {
            Order currentOrder = getCurrentOrder();
            return currentOrder.getOrderLines().isEmpty();
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * ✅ Svuota completamente l'ordine corrente
     */
    @Transactional
    public void clearCurrentOrder() {
        Order currentOrder = getCurrentOrder();
        
        // Rimuovi tutte le OrderLine
        for (OrderLine orderLine : List.copyOf(currentOrder.getOrderLines())) {
            orderLineService.delete(orderLine);
        }
        
        currentOrder.getOrderLines().clear();
        currentOrder.setTotalPrice(0.0f);
        save(currentOrder);
    }

    private String generateOrderCode() {
        return "ORD-" + System.currentTimeMillis() + "-" +
               String.format("%04d", new Random().nextInt(10000));
    }

    public void save(Order order) {
        orderRepository.save(order);
    }
}