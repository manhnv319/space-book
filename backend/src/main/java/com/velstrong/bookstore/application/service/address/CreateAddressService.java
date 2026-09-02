package com.velstrong.bookstore.application.service.address;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.command.address.CreateAddressCommand;
import com.velstrong.bookstore.application.response.address.AddressResponse;
import com.velstrong.bookstore.domain.model.UserAddress;
import com.velstrong.bookstore.domain.port.in.address.CreateAddressUseCase;
import com.velstrong.bookstore.domain.port.out.UserAddressRepository;


@Service
@Transactional
public class CreateAddressService implements CreateAddressUseCase {

    private final UserAddressRepository userAddressRepository;

    public CreateAddressService(UserAddressRepository userAddressRepository) {
        this.userAddressRepository = userAddressRepository;
    }

    @Override
    public AddressResponse create(CreateAddressCommand command) {
        UserAddress address = UserAddress.create(command.userId(), command.fullName(),
                command.phone(), command.province(), command.district(), command.ward(),
                command.addressDetail(), command.isDefault());
        return AddressResponse.from(userAddressRepository.save(address));
    }
}
