package com.velstrong.bookstore.application.service.address;

import com.velstrong.bookstore.domain.port.in.address.DeleteAddressUseCase;
import com.velstrong.bookstore.domain.port.out.UserAddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteAddressService implements DeleteAddressUseCase {

    private final UserAddressRepository userAddressRepository;

    public DeleteAddressService(UserAddressRepository userAddressRepository) {
        this.userAddressRepository = userAddressRepository;
    }

    @Override
    public void delete(Long addressId, Long userId) {
        userAddressRepository.deleteById(addressId);
    }
}
