package com.picpaysimplificado.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.TransactionalDTO;
import com.picpaysimplificado.repositories.TransactionalRepository;

class TransactionalServiceTest {
	
	@Mock
	private UserService userService;

	@Mock
	private TransactionalRepository transactionalRepository;

	@Mock
	private NotificationService notificationService;

	@Mock
	private AuthorizationService authorizationService;
	
	@Autowired
	@InjectMocks
	private TransactionalService transactionalService;
	
	@BeforeEach
	void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	@DisplayName("Should create transaction sucessfully when everithyng is OK")
	void createTransactionCase1() throws Exception {
		User sender = new User(1l, "Gabryel", "bele", "987654321", "gabryel@gmail.com", "12345", new BigDecimal(10), UserType.COMMON);
		User receiver = new User(2l, "Kelyta", "Cristina", "123456789", "kelyta@gmail.com", "12345", new BigDecimal(10), UserType.COMMON);
		
		when(userService.findUserById(1L)).thenReturn(sender);
		when(userService.findUserById(2L)).thenReturn(receiver);
		
		when(authorizationService.authorizeTransaction(any(), any())).thenReturn(true);
		TransactionalDTO request = new TransactionalDTO(new BigDecimal(10), 1l, 2l);
		
		this.transactionalService.createTransactional(request);
		
		verify(transactionalRepository, times(1)).save(any());
		
		sender.setBalance(new BigDecimal(0));
		verify(userService, times(1)).saveUser(sender);
		
		receiver.setBalance(new BigDecimal(20));
		verify(userService, times(1)).saveUser(receiver);
		
		verify(notificationService, times(1)).sendNotification(sender, "Transação realizada com sucesso!");
		verify(notificationService, times(1)).sendNotification(receiver, "Transação recebida com sucesso!");
	}
	
	@Test
	@DisplayName("Should thrown Exception when Transaction is not allowed")
	void createTransactionCase2() throws Exception {
		User sender = new User(1l, "Gabryel", "bele", "987654321", "gabryel@gmail.com", "12345", new BigDecimal(10), UserType.COMMON);
		User receiver = new User(2l, "Kelyta", "Cristina", "123456789", "kelyta@gmail.com", "12345", new BigDecimal(10), UserType.COMMON);
		
		when(userService.findUserById(1L)).thenReturn(sender);
		when(userService.findUserById(2L)).thenReturn(receiver);
		
		when(authorizationService.authorizeTransaction(any(), any())).thenReturn(false);
		
		Exception thrown = org.junit.jupiter.api.Assertions.assertThrows(Exception.class,() -> {
			TransactionalDTO request = new TransactionalDTO(new BigDecimal(10), 1l, 2l);
			
			this.transactionalService.createTransactional(request);
		});
		
		org.junit.jupiter.api.Assertions.assertEquals("Transação não autorizada", thrown.getMessage());
	}

}
