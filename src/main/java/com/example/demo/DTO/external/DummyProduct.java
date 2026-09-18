package com.example.demo.DTO.external;

import lombok.Data;

@Data
public class DummyProduct {
    private Long id;
    private String title;
    private String category;
    private Double price;
}