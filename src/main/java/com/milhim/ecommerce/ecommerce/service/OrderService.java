package com.milhim.ecommerce.ecommerce.service;

import com.milhim.ecommerce.ecommerce.model.LocalUser;
import com.milhim.ecommerce.ecommerce.model.UserOrder;
import com.milhim.ecommerce.ecommerce.model.repository.UserOrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final UserOrderRepository userOrderRepository;

    public OrderService(UserOrderRepository userOrderRepository) {
        this.userOrderRepository = userOrderRepository;
    }

    public List<UserOrder> getOrders(LocalUser user) {
        return userOrderRepository.findByUser(user);
    }
}
