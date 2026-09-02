package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.UserAddress;
import com.velstrong.bookstore.domain.port.out.UserAddressRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.UserAddressJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaUserAddressRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Profile("postgres & !mongodb")
public class UserAddressPersistenceAdapter implements UserAddressRepository {

    private final JpaUserAddressRepository jpaRepository;

    public UserAddressPersistenceAdapter(JpaUserAddressRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public UserAddress save(UserAddress address) {
        return toDomain(jpaRepository.save(toJpaEntity(address)));
    }

    @Override
    public Optional<UserAddress> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<UserAddress> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private UserAddress toDomain(UserAddressJpaEntity e) {
        return UserAddress.reconstitute(e.getId(), e.getUserId(), e.getFullName(), e.getPhone(),
                e.getProvince(), e.getDistrict(), e.getWard(), e.getAddressDetail(), e.getIsDefault());
    }

    private UserAddressJpaEntity toJpaEntity(UserAddress d) {
        UserAddressJpaEntity e = new UserAddressJpaEntity();
        e.setId(d.getId());
        e.setUserId(d.getUserId());
        e.setFullName(d.getFullName());
        e.setPhone(d.getPhone());
        e.setProvince(d.getProvince());
        e.setDistrict(d.getDistrict());
        e.setWard(d.getWard());
        e.setAddressDetail(d.getAddressDetail());
        e.setIsDefault(d.getIsDefault());
        return e;
    }
}
