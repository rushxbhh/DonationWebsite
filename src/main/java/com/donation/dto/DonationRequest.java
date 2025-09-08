package com.donation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DonationRequest {
    private Long amount;
    private String donorName;
    private String email;
    private String currency;  //  "usd", "inr"
}

