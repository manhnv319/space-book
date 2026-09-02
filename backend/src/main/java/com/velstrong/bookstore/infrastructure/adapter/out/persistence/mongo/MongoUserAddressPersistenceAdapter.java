package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.model.UserAddress;
import com.velstrong.bookstore.domain.port.out.UserAddressRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.UserAddressJpaEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Profile("mongodb & !postgres")
public class MongoUserAddressPersistenceAdapter extends MongoPersistenceSupport implements UserAddressRepository {

    private static final String COLLECTION = "user_addresses";

    public MongoUserAddressPersistenceAdapter(MongoTemplate mongo) { super(mongo); }

    @Override public UserAddress save(UserAddress value) { return toDomain(save(COLLECTION, toEntity(value))); }
    @Override public Optional<UserAddress> findById(Long id) { return findById(COLLECTION, UserAddressJpaEntity.class, id).map(this::toDomain); }
    @Override public List<UserAddress> findByUserId(Long userId) { return find(COLLECTION, UserAddressJpaEntity.class, Query.query(Criteria.where("userId").is(userId))).stream().map(this::toDomain).toList(); }
    @Override public void deleteById(Long id) { deleteById(COLLECTION, id, UserAddressJpaEntity.class); }

    private UserAddress toDomain(UserAddressJpaEntity e) {
        return UserAddress.reconstitute(e.getId(), e.getUserId(), e.getFullName(), e.getPhone(), e.getProvince(),
                e.getDistrict(), e.getWard(), e.getAddressDetail(), e.getIsDefault());
    }

    private UserAddressJpaEntity toEntity(UserAddress d) {
        UserAddressJpaEntity e = new UserAddressJpaEntity();
        e.setId(d.getId()); e.setUserId(d.getUserId()); e.setFullName(d.getFullName()); e.setPhone(d.getPhone());
        e.setProvince(d.getProvince()); e.setDistrict(d.getDistrict()); e.setWard(d.getWard());
        e.setAddressDetail(d.getAddressDetail()); e.setIsDefault(d.getIsDefault());
        return e;
    }
}
