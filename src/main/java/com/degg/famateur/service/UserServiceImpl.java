package com.degg.famateur.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.degg.famateur.exception.NoSuchUserException;
import com.degg.famateur.model.User;
import com.degg.famateur.repository.jpa.IUserJpaRepository;

@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	IUserJpaRepository repository;
//	IUserHibernateRepository repository;
	
	@Autowired
	@Lazy
	BCryptPasswordEncoder bcrypt;
	
	/**
	 * Add a User
	 * @throws NoSuchUserException 
	 */
	@Override
	public User save(User user) throws NoSuchUserException {
		user.setPassword(bcrypt.encode(user.getPassword()));
		return repository.save(user);
	}

	@Override
	public List<User> findAll() {
		return repository.findAll();
	}

	@Override
	public void delete(Long id) {
		repository.deleteById(id);		
	}	
	
	@Override
	public User getOne(Long id) throws NoSuchUserException {
		return repository.findById(id).orElseThrow(() -> new NoSuchUserException("No User exists with the id " + String.valueOf(id)));
	}

	@Override
	public User update(Long id, User user) {
		return repository.save(user);
		
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found."));
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthorities(user));
	}

	private static Collection<? extends GrantedAuthority> getAuthorities(User user) {
		return user.getRoles().stream().map((role) -> new SimpleGrantedAuthority("ROLE_" + role.getName())).collect(Collectors.toList());
	}
	

}
