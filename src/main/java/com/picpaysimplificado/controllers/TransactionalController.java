package com.picpaysimplificado.controllers;

import com.picpaysimplificado.domain.transaction.Transaction;
import com.picpaysimplificado.dtos.TransactionalDTO;
import com.picpaysimplificado.services.TransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public class TransactionalController {
    @Autowired
    private TransactionalService transactionalService;

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionalDTO transaction) throws Exception {
        Transaction newTransaction = this.transactionalService.createTransactional(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTransaction);
    }

}
