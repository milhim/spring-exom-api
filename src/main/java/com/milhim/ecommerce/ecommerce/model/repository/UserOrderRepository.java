package com.milhim.ecommerce.ecommerce.model.repository;

import com.milhim.ecommerce.ecommerce.model.LocalUser;
import com.milhim.ecommerce.ecommerce.model.UserOrder;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface UserOrderRepository extends ListCrudRepository<UserOrder, Long> {

    List<UserOrder> findByUser(LocalUser user);
}
