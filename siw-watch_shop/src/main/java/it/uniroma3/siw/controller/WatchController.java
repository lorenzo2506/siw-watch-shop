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
	
	@GetMapping("formNewWatch")
	public String showFormNewWatch(Model model) {
		model.addAttribute("watch", new Watch());
		return "formNewWatch.html";
	}
	
	@PostMapping("watch")
	public String addWatch(@Valid @ModelAttribute("watch") Watch watch, 
	                      BindingResult bindingResult,
	                      @RequestParam("imageFile") MultipartFile file,
	                      Model model) {
	    
	    watchValidate.validate(watch, bindingResult);
	    
	    // Valida che ci sia un'immagine
	    if (file.isEmpty()) {
	        bindingResult.rejectValue("name", "error.watch", "Devi caricare un'immagine");
	    } else {
	        // Valida tipo file
	        if (!imageStorageService.isValidImageType(file.getContentType())) {
	            bindingResult.rejectValue("name", "error.watch", 
	                "Tipo file non valido. Usa solo JPEG, PNG, GIF o WebP");
	        }
	        
	        // Valida dimensione (max 5MB)
	        if (file.getSize() > 5 * 1024 * 1024) {
	            bindingResult.rejectValue("name", "error.watch", 
	                "File troppo grande. Dimensione massima: 5MB");
	        }
	    }
	    
	    if(bindingResult.hasErrors()) {
	        return "formNewWatch.html";
	    }
	    
	    try {
	        // Salva l'immagine
	        String imagePath = imageStorageService.saveImage(file);
	        watch.setImagePath(imagePath);
	        
	        watchService.save(watch);
	        
	        model.addAttribute("watch", watch);
	        return "watch.html";
	        
	    } catch (IOException e) {
	        bindingResult.rejectValue("name", "error.watch", "Errore nel caricamento immagine");
	        return "formNewWatch.html";
	    }
	}
	
	@PostMapping("watch/redirect")
	public String addWatchRedirect(@Valid @ModelAttribute("watch") Watch watch, 
	                              BindingResult bindingResult,
	                              @RequestParam("imageFile") MultipartFile file) {

	    watchValidate.validate(watch, bindingResult);
	    
	    // Valida che ci sia un'immagine
	    if (file.isEmpty()) {
	        bindingResult.rejectValue("name", "error.watch", "Devi caricare un'immagine");
	    } else {
	        // Valida tipo file
	        if (!imageStorageService.isValidImageType(file.getContentType())) {
	            bindingResult.rejectValue("name", "error.watch", 
	                "Tipo file non valido. Usa solo JPEG, PNG, GIF o WebP");
	        }
	        
	        // Valida dimensione (max 5MB)
	        if (file.getSize() > 5 * 1024 * 1024) {
	            bindingResult.rejectValue("name", "error.watch", 
	                "File troppo grande. Dimensione massima: 5MB");
	        }
	    }
	    
	    if(bindingResult.hasErrors()) {
	        return "formNewWatch.html";
	    }
	    
	    try {
	        // Salva l'immagine
	        String imagePath = imageStorageService.saveImage(file);
	        watch.setImagePath(imagePath);
	        
	        watchService.save(watch);
	        
	        return "redirect:/watch/" + watch.getId();
	        
	    } catch (IOException e) {
	        return "formNewWatch.html";
	    }
	}
	
	// Endpoint per servire le immagini
	@GetMapping("/images/{filename:.+}")
	public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
	    try {
	        Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
	        Resource resource = new UrlResource(filePath.toUri());
	        
	        if (resource.exists() && resource.isReadable()) {
	            String contentType = Files.probeContentType(filePath);
	            if (contentType == null) {
	                contentType = "application/octet-stream";
	            }
	            
	            return ResponseEntity.ok()
	                .contentType(MediaType.parseMediaType(contentType))
	                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
	                .body(resource);
	        } else {
	            return ResponseEntity.notFound().build();
	        }
	    } catch (Exception e) {
	        return ResponseEntity.notFound().build();
	    }
	}
}