package com.donation.controllers;

import com.donation.dto.DonationRequest;
import com.donation.dto.StripeResponse;
import com.donation.services.StripeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/donation")
public class DonationController {

    private final StripeService stripeService;

    public DonationController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse> createCheckout(@RequestBody DonationRequest request) {
        StripeResponse response = stripeService.createCheckoutSession(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
