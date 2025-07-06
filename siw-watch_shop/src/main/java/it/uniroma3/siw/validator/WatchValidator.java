package it.uniroma3.siw.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Watch;
import it.uniroma3.siw.service.WatchService;


@Component
public class WatchValidator implements Validator{
	
	@Autowired private WatchService watchService;
	
	@Override
	public void validate(Object o, Errors errors) {
		
		Watch watch = (Watch) o;
		
		if(watch.getYear()!=null && watch.getName()!=null && watch.getBrand()!=null 
			&& watchService.existsByNameAndBrandAndYear(watch.getName(), watch.getBrand(), watch.getYear())) {
			
			errors.reject("watch.duplicate");
		}
	}
	

	@Override
	public boolean supports(Class<?> aClass) {
		return Watch.class.equals(aClass);
	}
	

}
