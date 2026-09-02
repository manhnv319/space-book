package com.velstrong.bookstore.application.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.command.user.ChangePasswordCommand;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.User;
import com.velstrong.bookstore.domain.port.in.user.ChangePasswordUseCase;
import com.velstrong.bookstore.domain.port.out.UserRepository;
import com.velstrong.bookstore.domain.port.out.PasswordEncoder;
import com.velstrong.bookstore.domain.port.out.SessionVersionRepository;


@Service
@Transactional
public class ChangePasswordService implements ChangePasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionVersionRepository sessionVersionRepository;

    public ChangePasswordService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                 SessionVersionRepository sessionVersionRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionVersionRepository = sessionVersionRepository;
    }

    @Override
    public void changePassword(ChangePasswordCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new EntityNotFoundException("User", command.userId()));

        if (!passwordEncoder.matches(command.currentPassword(), user.getPassword()))
            throw new InvalidOperationException("Current password is incorrect");
        if (command.newPassword() == null || command.newPassword().length() < 6)
            throw new InvalidOperationException("Password must be at least 6 characters");

        userRepository.updatePassword(command.userId(), passwordEncoder.encode(command.newPassword()));
        sessionVersionRepository.incrementVersion(command.userId());
    }
}
