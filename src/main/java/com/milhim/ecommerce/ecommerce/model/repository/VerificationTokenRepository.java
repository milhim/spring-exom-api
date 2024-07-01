package com.milhim.ecommerce.ecommerce.model.repository;

import com.milhim.ecommerce.ecommerce.model.LocalUser;
import com.milhim.ecommerce.ecommerce.model.VerificationToken;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends ListCrudRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

    void deleteByUser(LocalUser user);
}
