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
		return watchRepo.findByNameAndBrandAndYearAndAvailabilityTrue(name.toUpperCase().trim(), brand.toUpperCase().trim(), year).get();
	}
	
	
	public List<Watch> getAvailableWatch(String name, String brand) {
		return watchRepo.findByNameAndBrandAndAvailabilityTrue(name.toUpperCase().trim(), brand.toUpperCase().trim());
	}
	
	public List<Watch> getAllAvailableWatchesBySearchBar(String value) {
		return watchRepo.findBySearchBar(value.toUpperCase().trim());
	}
	
	public boolean existsByNameAndBrandAndYearAndAvailability(String name, String brand, Integer year) {
		return watchRepo.existsByNameAndBrandAndYearAndAvailabilityTrue(name.toUpperCase().trim(), brand.toUpperCase().trim(), year);
	}
	
	public boolean existsByNameAndBrandAndAvailability(String name, String brand) {
		return watchRepo.existsByNameAndBrandAndAvailabilityTrue(name.toUpperCase().trim(), brand.toUpperCase().trim());
	}
	
	
	public Iterable<Watch> getAllAvailableWatches() {
		return watchRepo.findAllAvailable();
	}
	
	
	public Watch getWatch(Long id) {
		return watchRepo.findById(id).get();
	}
	
	public Watch getWatch(String name, String brand, Integer year) {
		return watchRepo.findByNameAndBrandAndYear(name.toUpperCase().trim(), brand.toUpperCase().trim(), year).get();
	}
	
	
	public List<Watch> getWatch(String name, String brand) {
		return watchRepo.findByNameAndBrand(name.toUpperCase().trim(), brand.toUpperCase().trim());
	}
	
	public boolean existsByNameAndBrandAndYear(String name, String brand, Integer year) {
		return watchRepo.existsByNameAndBrandAndYear(name.toUpperCase().trim(), brand.toUpperCase().trim(), year);
	}
	
	public boolean existsByNameAndBrand(String name, String brand) {
		return watchRepo.existsByNameAndBrand(name.toUpperCase().trim(), brand.toUpperCase().trim());
	}
	
	
	public Iterable<Watch> getAllWatches() {
		return watchRepo.findAll();
	}
	
	public void deactivateWatch(Long id) {
        Watch watch = watchRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Watch not found"));
        watch.setAvailability(false);
        this.save(watch);
    }
    
   
    public void reactivateWatch(Long id) {
        Watch watch = watchRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Watch not found"));
        watch.setAvailability(true);
        this.save(watch);
    }
    
    public void save(Watch watch) {
    	
    	watch.setName(watch.getName().toUpperCase().trim());
    	watch.setBrand(watch.getBrand().toUpperCase().trim());
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
    	this.save(watch);
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
	
	public List<String> getAllAvailableBrands() {
	    return watchRepo.findAllAvailableBrands();
	}

	public List<Watch> getAllAvailableWatchesByBrand(String brand) {
	    return watchRepo.findAllAvailableByBrand(brand.toUpperCase().trim());
	}

}
