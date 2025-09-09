package it.uniroma3.siw.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.Watch;
import it.uniroma3.siw.service.ReviewService;
import org.springframework.validation.Validator;


@Component
public class ReviewValidator implements Validator{

	
	@Autowired ReviewService reviewService;
	
	@Override
	public void validate(Object o, Errors errors) {
		
		
		Review review = (Review) o;
		
		if(reviewService.hasUserReviewedWatch(review.getWatch(), review.getUser())) {
			
			System.out.println("è gia stata pubblicata" + reviewService.hasUserReviewedWatch(review.getWatch(), review.getUser()) );
			errors.reject("reviews.user.hasReviewed");
		}
	}
	
	
	@Override
	public boolean supports(Class<?> aClass) {
		return Review.class.equals(aClass);
	}
	
	
}
