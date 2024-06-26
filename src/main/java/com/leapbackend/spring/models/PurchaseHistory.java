package com.leapbackend.spring.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@Table(name = "purchase_history")
@Getter
@Setter
public class PurchaseHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne // User entity with Customer role
    @JsonIgnore
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;
}
