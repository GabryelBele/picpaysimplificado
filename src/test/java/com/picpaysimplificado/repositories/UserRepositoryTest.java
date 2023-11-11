package com.picpaysimplificado.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;

import javax.naming.spi.DirStateFactory.Result;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.UserDTO;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("teste")
class UserRepositoryTest {

	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private UserRepository userRepository;
	
	@Test
	@DisplayName("Should getUser Sucessfully from DB")
	void testFindUserByDocumentCase1() {
		String document = "9876543210";
		UserDTO data = new UserDTO("Gabryel", "Bele" , document, new BigDecimal(10), "gabryel@gmail.com", "123456", UserType.COMMON);
		this.createUser(data);
		
		Optional<User> result = this.userRepository.findUserByDocument(document);
		 assertThat(result.isPresent()).isTrue();
	}
	
	@Test
	@DisplayName("Should not geUser from DB when user not exists")
	void testFindUserByDocumentCase2() {
		String document = "9876543210";
		Optional<User> result = this.userRepository.findUserByDocument(document);
		 assertThat(result.isEmpty()).isTrue();
	}
	
	private User createUser(UserDTO data) {
		User newUser = new User(data);
		this.entityManager.persist(newUser);
		return newUser;
		
	}

}
