package it.uniroma3.siw.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Watch;
import it.uniroma3.siw.service.ImageStorageService;
import it.uniroma3.siw.service.WatchService;
import it.uniroma3.siw.validator.WatchValidator;
import jakarta.validation.Valid;

@Controller
public class WatchController {
	
	@Autowired private WatchService watchService;
	@Autowired private WatchValidator watchValidate;
	@Autowired private ImageStorageService imageStorageService;
	
	@Value("${app.image.upload.dir:uploads/images}")
	private String uploadDir;
	
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
	

}