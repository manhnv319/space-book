package com.velstrong.bookstore.infrastructure.adapter.in.rest.address;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Vietnam dropped the district tier on 01/07/2025, so a current address has
 * nothing to put in that field. These pin that the API accepts a two-tier
 * address while still refusing one that is missing a tier it does need.
 */
class AddressRequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private AddressRequest request(String province, String district, String ward) {
        return new AddressRequest("Nguyen Van Manh", "0912345678", province, district, ward, "Số 12 ngõ 34", true);
    }

    @Test
    void acceptsATwoTierAddressWithNoDistrict() {
        assertThat(validator.validate(request("Thành phố Hà Nội", null, "Phường Ba Đình"))).isEmpty();
        assertThat(validator.validate(request("Thành phố Hà Nội", "", "Phường Ba Đình"))).isEmpty();
    }

    @Test
    void stillAcceptsALegacyThreeTierAddress() {
        assertThat(validator.validate(request("Hà Nội", "Quận Ba Đình", "Phường Ngọc Hà"))).isEmpty();
    }

    @Test
    void stillRequiresProvinceAndWard() {
        assertThat(validator.validate(request("", null, "Phường Ba Đình"))).isNotEmpty();
        assertThat(validator.validate(request("Thành phố Hà Nội", null, ""))).isNotEmpty();
    }
}
