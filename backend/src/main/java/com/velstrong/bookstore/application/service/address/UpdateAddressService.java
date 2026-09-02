package com.velstrong.bookstore.application.service.address;

import com.velstrong.bookstore.application.command.address.UpdateAddressCommand;
import com.velstrong.bookstore.application.response.address.AddressResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.port.in.address.UpdateAddressUseCase;
import com.velstrong.bookstore.domain.port.out.UserAddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateAddressService implements UpdateAddressUseCase {

    private final UserAddressRepository userAddressRepository;

    public UpdateAddressService(UserAddressRepository userAddressRepository) {
        this.userAddressRepository = userAddressRepository;
    }

    @Override
    public AddressResponse update(UpdateAddressCommand command) {
        var address = userAddressRepository.findById(command.addressId())
                .orElseThrow(() -> new EntityNotFoundException("Address", command.addressId()));
        address.update(command.fullName(), command.phone(), command.province(),
                command.district(), command.ward(), command.addressDetail());
        return AddressResponse.from(userAddressRepository.save(address));
    }
}
