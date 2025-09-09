package it.uniroma3.siw.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Order;
import it.uniroma3.siw.model.OrderStatus;
import it.uniroma3.siw.model.Watch;
import it.uniroma3.siw.service.OrderLineService;
import it.uniroma3.siw.service.OrderService;

@Controller
public class OrderController {
	
	@Autowired private OrderService orderService;
	
	@Autowired private OrderLineService orderLineService;
	
	
	// ✅ NUOVO: Endpoint per aprire il carrello corrente
	@GetMapping("/currentOrder")
	public String showCurrentOrder(Model model) {
		Order currentOrder = orderService.getCurrentOrder();
		model.addAttribute("currentOrder", currentOrder);
		model.addAttribute("orderLines", orderService.getAllOrderLinesById(currentOrder.getId()));
		return "currentOrder";
	}
	
	
	@GetMapping("/currentOrder/{id}")
	public String showCurrentOrderById(@PathVariable("id") Long id, Model model) {
		model.addAttribute("currentOrder", orderService.getById(id));
		model.addAttribute("orderLines", orderService.getAllOrderLinesById(id));
		return "currentOrder";
	}
	
	
	@GetMapping("/orders")
	public String showUserPlacedOrders(Model model) {
		model.addAttribute("orders", orderService.getAllPlacedOrders());
		return "placedOrders";
	}
	
	
	@GetMapping("/placedOrder/{id}")
	public String showPlacedOrder(@PathVariable("id") Long id, Model model) {
		model.addAttribute("order", orderService.getById(id));
		model.addAttribute("orderLines", orderService.getAllOrderLinesById(id));
		return "placedOrder";
	}
	
	
	@PostMapping("/currentOrder/add/{id}")
	public String addWatchToCurrentOrder(@PathVariable("id") Long id) {
		this.orderService.addWatchToCurrentOrder(id);
		Order currentOrder = this.orderService.getCurrentOrder();
		return "redirect:/currentOrder/" + currentOrder.getId();
	}
	
	
	@PostMapping("/currentOrder/remove/{id}")
	public String deletOrderLineToCurrentOrder(@PathVariable("id") Long id) {
		
		this.orderService.removeOrderLine(id);
		Order currentOrder = this.orderService.getCurrentOrder();
		return "redirect:/currentOrder/" + currentOrder.getId();
	}
	
	
	@PostMapping("/currentOrder")
	public String confirmOrder() {
		this.orderService.confirmOrder();
		return "redirect:/"; // ✅ Corretto con redirect
	}
}