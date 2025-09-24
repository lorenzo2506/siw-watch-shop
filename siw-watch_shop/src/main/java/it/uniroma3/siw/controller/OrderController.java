package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.model.Order;
import it.uniroma3.siw.service.OrderLineService;
import it.uniroma3.siw.service.OrderService;

@Controller
public class OrderController {
    
    @Autowired private OrderService orderService;
    
    @Autowired private OrderLineService orderLineService;
    
    
   
    
    
    @GetMapping("/currentOrder")
    public String showCurrentOrder(Model model) {
    	Order currentOrder = orderService.getCurrentOrder();
        model.addAttribute("currentOrder", currentOrder);
        model.addAttribute("orderLines", currentOrder.getOrderLines());
        model.addAttribute("itemCount", orderService.getCurrentOrderItemCount());
        model.addAttribute("isEmpty", orderService.isCurrentOrderEmpty());
        return "currentOrder";
    }
    
   
    @GetMapping("/currentOrder/{id}")
    public String showCurrentOrderById(@PathVariable("id") Long id, Model model) {
    	Order order = orderService.getById(id);
        if (order == null) {
            model.addAttribute("error", "Ordine non trovato");
            return "error";
        }
        
        model.addAttribute("currentOrder", order);
        model.addAttribute("orderLines", order.getOrderLines());
        model.addAttribute("itemCount", order.getOrderLines().stream()
            .mapToInt(ol -> ol.getQuantity()).sum());
        model.addAttribute("isEmpty", order.getOrderLines().isEmpty());
        return "currentOrder";

    }
    
   
    @GetMapping("/orders")
    public String showUserPlacedOrders(Model model) {
    	model.addAttribute("orders", orderService.getAllPlacedOrders());
        return "orders";
    }
    
    
    @GetMapping("/order/{id}")
    public String showPlacedOrder(@PathVariable("id") Long id, Model model) {
    	Order order = orderService.getById(id);
        if (order == null) {
            model.addAttribute("error", "Ordine non trovato");
            return "error";
        }
        
        model.addAttribute("order", order);
        model.addAttribute("orderLines", order.getOrderLines());
        return "orderDetail";
    }
    
    
    @GetMapping("/placedOrder/{id}")
    public String showPlacedOrderOld(@PathVariable("id") Long id, Model model) {
        return showPlacedOrder(id, model);
    }
    
    
    
    @PostMapping("/currentOrder/add/{id}")
    public String addWatchToCurrentOrder(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
    	orderService.addWatchToCurrentOrder(id);
        Order currentOrder = orderService.getCurrentOrder();
        return "redirect:/currentOrder/" + currentOrder.getId();
    }
    
    
    
    @PostMapping("/currentOrder/remove/{id}")
    public String deleteOrderLineFromCurrentOrder(@PathVariable("id") Long id, 
                                                 RedirectAttributes redirectAttributes) {
       
    	 orderService.removeOrderLine(id, this.orderService.getCurrentOrder());
         Order currentOrder = orderService.getCurrentOrder();
         return "redirect:/currentOrder/" + currentOrder.getId();
    }
    
    
    @PostMapping("/currentOrder/increase/{orderLineId}")
    public String increaseQuantity(@PathVariable("orderLineId") Long orderLineId, 
                                  RedirectAttributes redirectAttributes) {
        
            orderLineService.increaseQuantity(orderLineId);
            // Ricalcola il totale dell'ordine
            Order currentOrder = orderService.getCurrentOrder();
            currentOrder.setTotalPrice(currentOrder.calculateTotalPrice());
            orderService.save(currentOrder);
            
            return "redirect:/currentOrder";
        
    }
    
    
    @PostMapping("/currentOrder/decrease/{orderLineId}")
    public String decreaseQuantity(@PathVariable("orderLineId") Long orderLineId, 
                                  RedirectAttributes redirectAttributes) {
    	orderService.removeOrderLine(orderLineId, this.orderService.getCurrentOrder()); // Usa il metodo esistente che gestisce già la logica
        redirectAttributes.addFlashAttribute("success", "Quantità diminuita!");
        return "redirect:/currentOrder";
    }
    
    
    @PostMapping("/currentOrder/clear")
    public String clearCart(RedirectAttributes redirectAttributes) {
    	orderService.clearCurrentOrder();
        redirectAttributes.addFlashAttribute("success", "Carrello svuotato!");
        return "redirect:/currentOrder";
    }
    
    
    @PostMapping("/currentOrder/checkout")
    public String confirmOrder(RedirectAttributes redirectAttributes) {
    	if (orderService.isCurrentOrderEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Il carrello è vuoto!");
            return "redirect:/currentOrder";
        }
        
        orderService.confirmOrder();
        redirectAttributes.addFlashAttribute("success", "Ordine confermato con successo!");
        return "redirect:/orders";
    }
    
   
    @PostMapping("/currentOrder")
    public String confirmOrderOld(RedirectAttributes redirectAttributes) {
        try {
            if (orderService.isCurrentOrderEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Il carrello è vuoto!");
                return "redirect:/currentOrder";
            }
            
            orderService.confirmOrder();
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore nella conferma dell'ordine: " + e.getMessage());
            return "redirect:/currentOrder";
        }
    }
}