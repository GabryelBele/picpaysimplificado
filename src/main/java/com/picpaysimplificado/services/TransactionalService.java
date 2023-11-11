package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.transaction.Transaction;
import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.dtos.TransactionalDTO;
import com.picpaysimplificado.repositories.TransactionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionalService {

	@Autowired
	private UserService userService;

	@Autowired
	private TransactionalRepository transactionalRepository;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private AuthorizationService authorizationService;

	public Transaction createTransactional(TransactionalDTO transactionDTO) throws Exception {
		User sender = this.userService.findUserById(transactionDTO.senderId());
		User receiver = this.userService.findUserById(transactionDTO.receiverId());

		userService.validatedTransactional(sender, transactionDTO.value());

		boolean isAuthorized = this.authorizationService.authorizeTransaction(sender, transactionDTO.value());

		if (!isAuthorized) {
			throw new Exception("Transação não autorizada");
		}

		Transaction newTransaction = new Transaction();
		newTransaction.setAmount(transactionDTO.value());
		newTransaction.setSender(sender);
		newTransaction.setReceiver(receiver);
		newTransaction.setTimeStamps(LocalDateTime.now());

		sender.setBalance(sender.getBalance().subtract(transactionDTO.value()));
		receiver.setBalance(receiver.getBalance().add(transactionDTO.value()));

		this.transactionalRepository.save(newTransaction);
		userService.saveUser(sender);
		userService.saveUser(receiver);

		this.notificationService.sendNotification(sender, "Transação realizada com sucesso!");
		this.notificationService.sendNotification(receiver, "Transação recebida com sucesso!");

		return newTransaction;
	}

}
