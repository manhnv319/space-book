package com.velstrong.bookstore.infrastructure.adapter.in.rest.address;

import com.velstrong.bookstore.application.command.address.CreateAddressCommand;
import com.velstrong.bookstore.application.command.address.UpdateAddressCommand;
import jakarta.validation.constraints.NotBlank;

/**
 * Địa chỉ giao hàng.
 *
 * `district` cố tình KHÔNG bắt buộc: từ 01/07/2025 Việt Nam bỏ cấp huyện, địa
 * chỉ hành chính chỉ còn hai cấp tỉnh/thành → phường/xã. Cột vẫn giữ để không
 * làm hỏng các địa chỉ đã lưu theo cấu trúc cũ, nhưng địa chỉ mới không có giá
 * trị nào để điền vào đó.
 */
public record AddressRequest(
        @NotBlank String fullName,
        @NotBlank String phone,
        @NotBlank String province,
        String district,
        @NotBlank String ward,
        @NotBlank String addressDetail,
        boolean isDefault
) {
    public CreateAddressCommand toCreateCommand(Long userId) {
        return new CreateAddressCommand(userId, fullName, phone, province, district, ward, addressDetail, isDefault);
    }

    public UpdateAddressCommand toUpdateCommand(Long addressId, Long userId) {
        return new UpdateAddressCommand(addressId, userId, fullName, phone, province, district, ward, addressDetail);
    }
}
