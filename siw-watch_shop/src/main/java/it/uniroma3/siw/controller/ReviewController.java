package it.uniroma3.siw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.model.Watch;
import it.uniroma3.siw.service.AuthenticationService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.service.WatchService;
import it.uniroma3.siw.validator.ReviewValidator;
import jakarta.validation.Valid;

@Controller
public class ReviewController {

	@Autowired private ReviewService reviewService;
	@Autowired private WatchService watchService;
	@Autowired private AuthenticationService authService;
	@Autowired private ReviewValidator reviewValidator;
	
	@GetMapping("/watch/{watchId}/review/{reviewId}")
	private String showReview(@PathVariable("watchId") Long watchId, @PathVariable("reviewId") Long reviewId, Model model) {
		
		model.addAttribute("review", reviewService.getReviewById(reviewId));
		
		User user = reviewService.getReviewById(reviewId).getUser();
		
		if(user==null)
			throw new IllegalArgumentException("UTENTE NON ASSOCIATO ALLA RECENSIONE");
		
		model.addAttribute("user", user);
		return "review.html";
	}
	
	
	@PostMapping("/watch/{watchId}/reviews")
	public String createReview(@PathVariable("watchId") Long watchId,
	                       @Valid @ModelAttribute("review") Review review,
	                       BindingResult bindingResult,
	                       Model model) {
	    
		
		UserDetails userDetails = authService.getCurrentUserDetails();
	    Credentials credential = (Credentials) userDetails;
		
	    review.setUser(credential.getUser());
	    review.setWatch(watchService.getWatch(watchId));
        this.reviewValidator.validate(review, bindingResult);
	    
	    if (bindingResult.hasErrors()) {
	        // Ricarica la pagina recensioni con errori
	        Watch watch = watchService.getAvailableWatch(watchId);
	        List<Review> reviews = reviewService.getAllWatchReview(watchId);
	        model.addAttribute("watch", watch);
	        model.addAttribute("reviews", reviews);

	        
	        return "reviews.html";
	    }
	    
	    // Imposta l'orologio
	    
	    reviewService.addReview(review, watchId, credential.getUser());
	    
	    return "redirect:/watch/" + watchId + "/reviews";
	}
	
	
	@GetMapping("/watch/{watchId}/reviews")
	private String showAllWatchReviewsExclutedCurrentUserReview(@PathVariable("watchId") Long watchId, Model model) {
		
		UserDetails userDetails = authService.getCurrentUserDetails();
	    Credentials credentials = (Credentials) userDetails;
		
	    
		Watch watch = watchService.getWatch(watchId);
		Review userReview=null;
		List<Review> reviews;
		
		if(credentials!=null) {
			userReview = reviewService.getUserReviewedWatch(watch, credentials.getUser());
			reviews = reviewService.getAllWatchReviewExclutedCurrentUserReview(watch, credentials.getUser());
		}
		else 
			reviews = reviewService.getAllWatchReview(watchId);
		
		model.addAttribute("watch", watch);
		model.addAttribute("reviews", reviews);
	    model.addAttribute("review", new Review()); // AGGIUNGI QUESTA RIGA

	    if(credentials!=null && userReview!=null)
	    	model.addAttribute("userReview",userReview);
		
		return "reviews.html";
	}
	
	
	@PostMapping("/admin/watch/{watch_id}/reviews/{review_id}/delete")
	private String adminDeleteReview(@PathVariable("review_id") Long review_id, @PathVariable("watch_id") Long watch_id) {
		
		Review review = reviewService.getReviewById(review_id);
		reviewService.deleteReview(review);
		return "redirect:/watch/" + watch_id + "/reviews";
	}
	
	
	
	@PostMapping("/watch/{watch_id}/reviews/{review_id}/delete")
	private String deleteReview(@PathVariable("review_id") Long review_id, @PathVariable("watch_id") Long watch_id) {
		
		Review review = reviewService.getReviewById(review_id);
		reviewService.deleteReview(review);
		return "redirect:/watch/" + watch_id + "/reviews";
	}
	
	
	
	@GetMapping("/watch/{watchId}/reviews/{reviewId}/editForm")
	private String showEditForm(@PathVariable("reviewId") Long reviewId, Model model) {
		
		model.addAttribute("review", this.reviewService.getReviewById(reviewId));
		return "editForm.html";
	}
	
	
	@PostMapping("/watch/{watchId}/review/{reviewId}/editForm")
	public String editReview(@PathVariable("watchId") Long watchId, @PathVariable("reviewId") Long reviewId,
			@Valid @ModelAttribute("review") Review formReview, BindingResult bindingResult, Model model) {
		
		if(bindingResult.hasErrors()) {
			model.addAttribute("review", reviewService.getReviewById(reviewId));
	        return "editForm.html";
		}
		
		this.reviewService.editReview(reviewId, formReview);
		return "redirect:/watch/" + watchId + "/reviews";
	}
	
	
	
}
