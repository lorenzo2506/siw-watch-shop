package it.uniroma3.siw.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.model.Watch;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.repository.WatchRepository;

@Service
public class ReviewService {
	
	@Autowired private ReviewRepository reviewRepo;
	@Autowired private WatchService watchService;
	
	public Review getReviewById(Long id) {
		return reviewRepo.findById(id).get();
	}
	
	
	public List<Review> getAllWatchReview(Long watchId) {
		
		Watch watch = watchService.getWatch(watchId);
		if(watch==null)
			throw new IllegalArgumentException("orologio non trovato nel sistema");
		return reviewRepo.findAllByWatch(watch);
	}
	
	
	
	public List<Review> getAllWatchReviewExclutedCurrentUserReview(Watch watch, User user) {
		
		if(watch==null || user==null)
			throw new IllegalArgumentException("orologio od utent non trovati nel sistema");

		return reviewRepo.findAllByWatchAndUserNot(watch, user);
	}
	
	
	public void addReview(Review review, Long watchId, User user) {
		
		if(review==null || watchId==null)
			throw new IllegalArgumentException("review nulla");
		
		
		Watch watch = watchService.getAvailableWatch(watchId);
		review.setWatch(watch);
	    review.setCreatedAt(LocalDateTime.now());
	    review.setUser(user);
    	reviewRepo.save(review);
		watchService.addReviewToWatchList(review.getId());
	}
	
	
	public Float getWatchAverageRating(Long watch_id) {
		return reviewRepo.getAverageWatchRating(watch_id);
	}
	
	public Integer countWatchRating(Long watch_id) {
		return reviewRepo.countWatchRating(watch_id);
	}
	
	
	public boolean hasUserReviewedWatch(Watch watch, User user) {
		return reviewRepo.existsByWatchIdAndUserId(watch.getId(), user.getId());
	}
	
	
	public Review getUserReviewedWatch(Watch watch, User user) {
		if(this.hasUserReviewedWatch(watch, user) == false )
				return null;
		return reviewRepo.findByWatchIdAndUserId(watch.getId(), user.getId());
	}
	
	
	
	public void deleteReview(Review review) {
		
		watchService.deleteReviewFromWatchList(review.getId());
		reviewRepo.delete(review);
		watchService.calcAndSetAverageRating(review.getWatch());
		watchService.calcAndSetRatingCount(review.getWatch());
		watchService.save(review.getWatch());
	}
	
	
	public void editReview(Long reviewId, Review formReview) {
		
		Review review = this.getReviewById(reviewId);

		review.setText(formReview.getText());
		review.setStar_rating(formReview.getStar_rating());
		review.setCreatedAt(LocalDateTime.now());
		
		this.save(review);
		
	}
	
	public void save(Review r) {
		this.reviewRepo.save(r);
	}

}
