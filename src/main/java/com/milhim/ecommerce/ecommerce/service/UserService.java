package com.milhim.ecommerce.ecommerce.service;

import com.milhim.ecommerce.ecommerce.api.model.LoginBody;
import com.milhim.ecommerce.ecommerce.api.model.RegistrationBody;
import com.milhim.ecommerce.ecommerce.exception.UserAlreadyExistException;
import com.milhim.ecommerce.ecommerce.model.LocalUser;
import com.milhim.ecommerce.ecommerce.model.repository.LocalUserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final LocalUserRepository localUserRepository;
    private final EncryptionService encryptionService;
    private final JWTService jwtService;

    public UserService(LocalUserRepository localUserRepository, EncryptionService encryptionService, JWTService jwtService) {
        this.localUserRepository = localUserRepository;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
    }

    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistException {

        if (this.localUserRepository.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()
                || this.localUserRepository.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()) {
            throw new UserAlreadyExistException();
        }
        LocalUser localUser = new LocalUser();
        localUser.setEmail(registrationBody.getEmail());
        localUser.setFirstName(registrationBody.getFirstName());
        localUser.setLastName(registrationBody.getLastName());
        localUser.setUsername(registrationBody.getUsername());

        localUser.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));

        return this.localUserRepository.save(localUser);
    }

    public String loginUser(LoginBody loginBody) {
        Optional<LocalUser> opUser = localUserRepository.findByUsernameIgnoreCase(loginBody.getUsername());

        if (!opUser.isPresent()) {
            return null;
        }
        LocalUser user = opUser.get();
        if (!encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())) {
            return "";
        }
        return jwtService.generateJWT(user);
    }
}
