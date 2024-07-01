package com.milhim.ecommerce.ecommerce.service;

import com.milhim.ecommerce.ecommerce.api.model.LoginBody;
import com.milhim.ecommerce.ecommerce.api.model.RegistrationBody;
import com.milhim.ecommerce.ecommerce.exception.EmailFailureException;
import com.milhim.ecommerce.ecommerce.exception.UserAlreadyExistException;
import com.milhim.ecommerce.ecommerce.exception.UserNotVerifiedException;
import com.milhim.ecommerce.ecommerce.model.LocalUser;
import com.milhim.ecommerce.ecommerce.model.VerificationToken;
import com.milhim.ecommerce.ecommerce.model.repository.LocalUserRepository;
import com.milhim.ecommerce.ecommerce.model.repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final LocalUserRepository localUserRepository;
    private final EncryptionService encryptionService;
    private final JWTService jwtService;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;

    public UserService(LocalUserRepository localUserRepository, EncryptionService encryptionService, JWTService jwtService, EmailService emailService, VerificationTokenRepository verificationTokenRepository) {
        this.localUserRepository = localUserRepository;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistException, EmailFailureException {

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

        //try to send email
        VerificationToken verificationToken = this.creaeteVerificationToken(localUser);
        emailService.sendVerificationEmail(verificationToken);
        //save to db
        verificationTokenRepository.save(verificationToken);

        return this.localUserRepository.save(localUser);
    }

    private VerificationToken creaeteVerificationToken(LocalUser user) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(jwtService.generateVerificationJWT(user));
        verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setUser(user);

        user.getVerificationTokens().add(verificationToken);

        return verificationToken;
    }

    /**
     * Log user in and provide a verification token back
     *
     * @param loginBody login request
     * @return auth token
     */
    public String loginUser(LoginBody loginBody) throws UserNotVerifiedException, EmailFailureException {
        Optional<LocalUser> opUser = localUserRepository.findByUsernameIgnoreCase(loginBody.getUsername());

        if (opUser.isPresent()) {
            LocalUser user = opUser.get();
            if (encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())) {
                if (user.isEmailVerified()) {
                    return jwtService.generateJWT(user);
                } else {
                    List<VerificationToken> tokenList = user.getVerificationTokens();

                    boolean resend = tokenList.isEmpty() ||
                            tokenList.get(0).getCreatedTimestamp().before(new Timestamp(System.currentTimeMillis() - 60 * 60 * 1000));
                    if (resend) {
                        VerificationToken verificationToken = creaeteVerificationToken(user);
                        verificationTokenRepository.save(verificationToken);
                        emailService.sendVerificationEmail(verificationToken);
                    }
                    throw new UserNotVerifiedException(resend);
                }
            }
            return "";
        }
        return null;
    }

    @Transactional
    public boolean verifyUser(String token) {
        Optional<VerificationToken> optToken = verificationTokenRepository.findByToken(token);
        if (optToken.isPresent()) {
            VerificationToken verificationToken = optToken.get();
            LocalUser user = verificationToken.getUser();

            if (!user.isEmailVerified()) {
                user.setEmailVerified(true);
                verificationTokenRepository.deleteByUser(user);
                return true;
            }
        }
        return false;
    }
}
