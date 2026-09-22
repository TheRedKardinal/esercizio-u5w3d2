package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;

/**
 * L'API pubblica di Marvel Comics non fornisce i prezzi dei fumetti,
 * quindi il prezzo viene generato casualmente in un range realistico da fumetteria.
 */
@Service
public class PriceGeneratorService {

    private static final BigDecimal MIN_PRICE = new BigDecimal("2.99");
    private static final BigDecimal MAX_PRICE = new BigDecimal("49.99");

    private final SecureRandom random = new SecureRandom();

    public BigDecimal generate() {
        double range = MAX_PRICE.subtract(MIN_PRICE).doubleValue();
        double value = MIN_PRICE.doubleValue() + random.nextDouble() * range;
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
}
