package com.milhim.ecommerce.ecommerce.model.repository;

import com.milhim.ecommerce.ecommerce.model.LocalUser;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface LocalUserRepository extends ListCrudRepository<LocalUser,Long> {

    Optional<LocalUser> findByUsernameIgnoreCase(String username);

    Optional<LocalUser> findByEmailIgnoreCase(String email);
}
