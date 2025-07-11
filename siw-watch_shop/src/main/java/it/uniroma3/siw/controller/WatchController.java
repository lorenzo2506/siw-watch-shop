package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Watch;
import it.uniroma3.siw.service.WatchService;
import it.uniroma3.siw.validator.WatchValidator;
import jakarta.validation.Valid;

@Controller
public class WatchController {
	
	@Autowired private WatchService watchService;
	@Autowired private WatchValidator watchValidate;
	
	@GetMapping("/watch/{id}")
	public String showWatch(@PathVariable("id") Long id, Model model) {
		
		model.addAttribute("watch", watchService.getWatch(id));
		return "watch.html";
	}
	
	@GetMapping("/watches")
	public String showWatches(Model model) {
		model.addAttribute("watches", watchService.getAllWatches());
		return "watches.html";
	}
	
	@GetMapping("formNewWatch")
	public String showFormNewWatch(Model model) {
		model.addAttribute("watch", new Watch());
		return "formNewWatch.html";
	}
	
	@PostMapping("watch")
	public String addWatch(@Valid @ModelAttribute("watch") Watch watch, BindingResult bindingResult, Model model) {
		
		watchValidate.validate(watch, bindingResult);
		
		if(bindingResult.hasErrors())
			return "formNewWatch.html";
		
		model.addAttribute("watch", watch);
		watchService.save(watch);
		return "watch.html";
	}
	
	@PostMapping("watch/redirect")
	public String addWatch(@Valid @ModelAttribute("watch") Watch watch, BindingResult bindingResult) {
		
		watchValidate.validate(watch, bindingResult);

		if(bindingResult.hasErrors())
			return "formNewWatch.html";
		watchService.save(watch);
		return "redirect:/watch/" + watch.getId();
	}
	
	

}
