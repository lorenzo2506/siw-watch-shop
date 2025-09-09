package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.model.Watch;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long>{
	

	public List<Review> findAllByWatch(Watch watch);
	
	public List<Review> findAllByUser(User user);
	
	
	@Query("SELECT r FROM Review r WHERE r.user = :user AND r.star_rating= :star_rating")
	public List<Review> findAllByWatchAndRating(@Param("user") User user, @Param("star_rating") String star_rating);
	
	@Query("SELECT AVG(r.star_rating) FROM Review r WHERE r.watch.id= :watch_id")
	public Float getAverageWatchRating(@Param("watch_id") Long watch_id);
	
	@Query("SELECT COUNT(r) FROM Review r WHERE r.watch.id= :watch_id")
	public Integer countWatchRating(@Param("watch_id") Long watch_id);
	
	
	public Review findByWatchIdAndUserId(Long watchId, Long userId);
	
	public Boolean existsByWatchIdAndUserId(Long watchId, Long userId);
	
	public List<Review> findAllByWatchAndUserNot(Watch watch, User user);

}
