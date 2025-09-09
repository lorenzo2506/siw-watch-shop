package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.Watch;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.repository.WatchRepository;

@Service
public class WatchService {
	
	@Autowired private WatchRepository watchRepo;
	@Autowired private ReviewRepository reviewRepo;
	
	public Watch getAvailableWatch(Long id) {
		return watchRepo.findByIdAndAvailabilityTrue(id).get();
	}
	
	public Watch getAvailableWatch(String name, String brand, Integer year) {
		return watchRepo.findByNameAndBrandAndYearAndAvailabilityTrue(name, brand, year).get();
	}
	
	
	public List<Watch> getAvailableWatch(String name, String brand) {
		return watchRepo.findByNameAndBrandAndAvailabilityTrue(name, brand);
	}
	
	public boolean existsByNameAndBrandAndYearAndAvailability(String name, String brand, Integer year) {
		return watchRepo.existsByNameAndBrandAndYearAndAvailabilityTrue(name, brand, year);
	}
	
	public boolean existsByNameAndBrandAndAvailability(String name, String brand) {
		return watchRepo.existsByNameAndBrandAndAvailabilityTrue(name, brand);
	}
	
	
	public Iterable<Watch> getAllAvailableWatches() {
		return watchRepo.findAllAvailable();
	}
	
	
	public Watch getWatch(Long id) {
		return watchRepo.findById(id).get();
	}
	
	public Watch getWatch(String name, String brand, Integer year) {
		return watchRepo.findByNameAndBrandAndYear(name, brand, year).get();
	}
	
	
	public List<Watch> getWatch(String name, String brand) {
		return watchRepo.findByNameAndBrand(name, brand);
	}
	
	public boolean existsByNameAndBrandAndYear(String name, String brand, Integer year) {
		return watchRepo.existsByNameAndBrandAndYear(name, brand, year);
	}
	
	public boolean existsByNameAndBrand(String name, String brand) {
		return watchRepo.existsByNameAndBrand(name, brand);
	}
	
	
	public Iterable<Watch> getAllWatches() {
		return watchRepo.findAll();
	}
	
	public void deactivateWatch(Long id) {
        Watch watch = watchRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Watch not found"));
        watch.setAvailability(false);
        watchRepo.save(watch);
    }
    
   
    public void reactivateWatch(Long id) {
        Watch watch = watchRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Watch not found"));
        watch.setAvailability(true);
        watchRepo.save(watch);
    }
    
    public void save(Watch watch) {
		watchRepo.save(watch);
	}
    
    
    public void addReviewToWatchList(Long reviewId) {
    	
    	Review review = reviewRepo.findById(reviewId).get();
    	Watch watch = review.getWatch();
    	
    	if(watch == null || review == null)
    		throw new IllegalArgumentException("orologio o review non esistenti o id malcodificati");
    	
    	watch.getReviews().add(review);
    	this.calcAndSetAverageRating(watch);
    	this.calcAndSetRatingCount(watch);
    	watchRepo.save(watch);
    }
    
    public void calcAndSetAverageRating(Watch watch) {
    	watch.setAverageRating( this.reviewRepo.getAverageWatchRating(watch.getId()) );
    }
    
    
    public void calcAndSetRatingCount(Watch watch) {
    	watch.setRatingCount( this.reviewRepo.countWatchRating(watch.getId()) );
    }
    
    
    
	public void deleteReviewFromWatchList(Long reviewId) {
	
		Review review = reviewRepo.findById(reviewId).get();
    	Watch watch = review.getWatch();
    	
    	if(watch == null || review == null)
    		throw new IllegalArgumentException("orologio o review non esistenti o id malcodificati");
    	
    	watch.getReviews().remove(review);
	}

}
