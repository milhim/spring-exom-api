package com.milhim.ecommerce.ecommerce.model.repository;

import com.milhim.ecommerce.ecommerce.model.Product;
import org.springframework.data.repository.ListCrudRepository;

public interface ProductRepository extends ListCrudRepository<Product, Long> {
}
