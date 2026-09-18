package com.example.demo.DTO.external;

import lombok.Data;
import java.util.List;
@Data
public class DummyProductResponse {
    private List<DummyProduct> products;
    private Integer total;
    private Integer skip;
    private Integer limit;
}
