package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Watch;
import it.uniroma3.siw.repository.WatchRepository;

@Service
public class WatchService {
	
	@Autowired private WatchRepository watchRepo;
	
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
	
	public void save(Watch watch) {
		watchRepo.save(watch);
	}
	
	public void removeOneWatch(Long id) {
		
		if(this.getWatch(id)!=null)
			this.getWatch(id).decrementStock();
			
	}
	
	public void deleteWatch(Long id) {
		watchRepo.deleteById(id);
	}
	
	public Iterable<Watch> getAllWatches() {
		return watchRepo.findAll();
	}
	
	

}
