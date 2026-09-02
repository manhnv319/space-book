package com.velstrong.bookstore.application.service.address;

import com.velstrong.bookstore.application.response.address.AddressResponse;
import com.velstrong.bookstore.domain.port.in.address.GetAllAddressesUseCase;
import com.velstrong.bookstore.domain.port.out.UserAddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetAllAddressesService implements GetAllAddressesUseCase {

    private final UserAddressRepository userAddressRepository;

    public GetAllAddressesService(UserAddressRepository userAddressRepository) {
        this.userAddressRepository = userAddressRepository;
    }

    @Override
    public List<AddressResponse> getAllByUserId(Long userId) {
        return userAddressRepository.findByUserId(userId).stream()
                .map(AddressResponse::from)
                .toList();
    }
}
