package it.uniroma3.siw.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;


@Data
@Entity
public class Credentials {
	
	
    public static final Role DEFAULT_ROLE = Role.USER;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String username;
	
	private String password;
	
	@Enumerated(EnumType.STRING)
	private Role role;
	
	private String email;
	
	@OneToOne(cascade = CascadeType.ALL)
	private User user;

}
