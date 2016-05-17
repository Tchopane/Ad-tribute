package nl.marketingsciences.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import nl.marketingsciences.beans.database.User;

public interface UserRepository extends JpaRepository<User,Integer>{

	User findByUsername(String username);
	
}