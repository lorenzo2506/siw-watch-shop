package it.uniroma3.siw.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import it.uniroma3.siw.model.Watch;

@Repository
public interface WatchRepository extends JpaRepository<Watch, Long>{

	public List<Watch> findByNameAndBrandAndAvailabilityTrue(String name, String brand);
	
	public boolean existsByNameAndBrandAndAvailabilityTrue(String name, String brand);
	
	public Optional<Watch> findByNameAndBrandAndYearAndAvailabilityTrue(String name, String brand, Integer year);
	
	public boolean existsByNameAndBrandAndYearAndAvailabilityTrue(String name, String brand, Integer year);

	public void deleteByNameAndBrandAndAvailabilityTrue(String name,String brand);
	
	public Optional<Watch> findByIdAndAvailabilityTrue(Long id);
	
	@Query("SELECT w FROM Watch w WHERE w.availability = true")
	public List<Watch> findAllAvailable();
	
	
	
	
	public List<Watch> findByNameAndBrand(String name, String brand);
	
	public boolean existsByNameAndBrand(String name, String brand);
	
	public Optional<Watch> findByNameAndBrandAndYear(String name, String brand, Integer year);
	
	public boolean existsByNameAndBrandAndYear(String name, String brand, Integer year);

	public void deleteByNameAndBrand(String name,String brand);
	
	
	@Query("SELECT w FROM Watch w WHERE w.availability = true and (LOWER(w.name) LIKE LOWER(CONCAT(:value, '%'))) OR (LOWER(w.brand) LIKE LOWER(CONCAT(:value, '%')))")
	public List<Watch> findAllBySearchBar(@Param("value") String value);
	
	@Query("SELECT w FROM Watch w WHERE w.availability = true and (LOWER(w.name) LIKE LOWER(CONCAT(:value, '%'))) OR (LOWER(w.brand) LIKE LOWER(CONCAT(:value, '%'))) and w.availability = true")
	public List<Watch> findAllAvailableBySearchBar(@Param("value") String value);
	
	
	@Query("SELECT DISTINCT w.brand FROM Watch w WHERE w.availability = true ORDER BY w.brand")
	public List<String> findAllAvailableBrands();
	
	@Query("SELECT DISTINCT w.brand FROM Watch w ORDER BY w.brand")
	public List<String> findAllBrands();

	@Query("SELECT w FROM Watch w WHERE w.availability = true AND w.brand = :brand")
	public List<Watch> findAllAvailableByBrand(@Param("brand") String brand);
	
	public List<Watch> findAllByBrand(String brand);
	
}
