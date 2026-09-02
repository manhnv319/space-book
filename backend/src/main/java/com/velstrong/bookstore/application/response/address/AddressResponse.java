package com.velstrong.bookstore.application.response.address;

import com.velstrong.bookstore.domain.model.UserAddress;

public record AddressResponse(
        Long id,
        String fullName,
        String phone,
        String province,
        String district,
        String ward,
        String addressDetail,
        Boolean isDefault
) {
    public static AddressResponse from(UserAddress address) {
        return new AddressResponse(address.getId(), address.getFullName(), address.getPhone(),
                address.getProvince(), address.getDistrict(), address.getWard(),
                address.getAddressDetail(), address.getIsDefault());
    }
}
