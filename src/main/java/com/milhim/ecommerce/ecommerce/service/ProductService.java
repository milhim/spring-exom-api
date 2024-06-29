package com.milhim.ecommerce.ecommerce.service;

import com.milhim.ecommerce.ecommerce.model.Product;
import com.milhim.ecommerce.ecommerce.model.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }
}
