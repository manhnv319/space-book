package com.velstrong.bookstore.application.service.user;

import com.velstrong.bookstore.application.command.user.UpdateProfileCommand;
import com.velstrong.bookstore.application.response.user.UserResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.port.in.user.UpdateProfileUseCase;
import com.velstrong.bookstore.domain.port.out.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateProfileService implements UpdateProfileUseCase {

    private final UserRepository userRepository;

    public UpdateProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse updateProfile(UpdateProfileCommand command) {
        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new EntityNotFoundException("User", command.userId()));
        user.updateProfile(command.fullname(), command.phone(), command.birthday());
        return UserResponse.from(userRepository.save(user));
    }
}
