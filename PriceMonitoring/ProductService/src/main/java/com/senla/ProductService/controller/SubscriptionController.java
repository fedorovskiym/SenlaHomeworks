package com.senla.ProductService.controller;

import com.senla.ProductService.dto.SubscriptionDTO;
import com.senla.ProductService.model.Subscription;
import com.senla.ProductService.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/subscription")
public class SubscriptionController {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);
    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<SubscriptionDTO>> findAllSubscriptions() {
        logger.info("Received request to find all subscriptions /api/subscription/");
        return ResponseEntity.status(HttpStatus.OK).body(subscriptionService.findAll());
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<HttpStatus> deleteSubscription(@PathVariable UUID id) {
        logger.info("Received request to delete subscription /api/subscription/{}", id);
        subscriptionService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
