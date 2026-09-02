package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.Voucher;
import com.velstrong.bookstore.domain.model.enums.voucher.VoucherDiscountType;
import com.velstrong.bookstore.domain.port.out.VoucherRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.VoucherJpaEntity;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Profile("mongodb & !postgres")
public class MongoVoucherPersistenceAdapter extends MongoPersistenceSupport implements VoucherRepository {

    private static final String COLLECTION = "vouchers";

    public MongoVoucherPersistenceAdapter(MongoTemplate mongo) { super(mongo); }

    @Override public Voucher save(Voucher value) { return toDomain(save(COLLECTION, toEntity(value))); }
    @Override public Optional<Voucher> findById(Long id) { return findById(COLLECTION, VoucherJpaEntity.class, id).map(this::toDomain); }
    @Override public Optional<Voucher> findByCode(String code) { return findOne(COLLECTION, VoucherJpaEntity.class, Query.query(Criteria.where("code").is(code))).map(this::toDomain); }

    @Override
    public PageResult<Voucher> findAll(Byte status, String discountType, String search, LocalDate fromDate,
                                       LocalDate toDate, int page, int size) {
        Criteria criteria = new Criteria();
        if (status != null) criteria = new Criteria().andOperator(criteria, Criteria.where("status").is(status));
        if (discountType != null) criteria = new Criteria().andOperator(criteria, Criteria.where("discountType").is(discountType));
        if (search != null && !search.isBlank()) criteria = new Criteria().andOperator(criteria,
                new Criteria().orOperator(Criteria.where("code").regex(java.util.regex.Pattern.quote(search.trim()), "i"),
                        Criteria.where("name").regex(java.util.regex.Pattern.quote(search.trim()), "i")));
        if (fromDate != null) criteria = new Criteria().andOperator(criteria, Criteria.where("startAt").gte(fromDate.atStartOfDay()));
        if (toDate != null) criteria = new Criteria().andOperator(criteria, Criteria.where("endAt").lte(toDate.atTime(23, 59, 59)));
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC, "_id"));
        List<Voucher> values = find(COLLECTION, VoucherJpaEntity.class, query.limit(size).skip((long) page * size)).stream().map(this::toDomain).toList();
        long total = mongo.count(Query.of(query).limit(-1).skip(-1), VoucherJpaEntity.class, COLLECTION);
        return PageResult.of(values, total);
    }

    @Override public void deleteById(Long id) { deleteById(COLLECTION, id, VoucherJpaEntity.class); }

    @Override
    public boolean tryIncrementUsage(Long voucherId) {
        Document query = new Document("_id", voucherId).append("$or", List.of(
                new Document("usageLimitTotal", null),
                new Document("$expr", new Document("$lt", List.of(
                        new Document("$ifNull", List.of("$usedCount", 0)), "$usageLimitTotal")))));
        UpdateResult result = updateFirst(COLLECTION, new BasicQuery(query),
                new Update().inc("usedCount", 1), VoucherJpaEntity.class);
        return result.getMatchedCount() > 0;
    }

    @Override
    public boolean decrementUsage(Long voucherId) {
        UpdateResult result = updateFirst(COLLECTION, Query.query(new Criteria().andOperator(
                Criteria.where("_id").is(voucherId), Criteria.where("usedCount").gt(0))),
                new Update().inc("usedCount", -1), VoucherJpaEntity.class);
        return result.getMatchedCount() > 0;
    }

    private Voucher toDomain(VoucherJpaEntity e) {
        return Voucher.reconstitute(e.getId(), e.getCode(), e.getName(), e.getDescription(),
                e.getDiscountType() == null ? null : VoucherDiscountType.valueOf(e.getDiscountType()), e.getDiscountValue(),
                e.getMaxDiscountAmount(), e.getMinOrderAmount(), e.getStartAt(), e.getEndAt(), e.getUsageLimitTotal(),
                e.getUsageLimitPerUser(), e.getUsedCount(), e.getStatus());
    }

    private VoucherJpaEntity toEntity(Voucher d) {
        VoucherJpaEntity e = new VoucherJpaEntity(); e.setId(d.getId()); e.setCode(d.getCode()); e.setName(d.getName()); e.setDescription(d.getDescription());
        e.setDiscountType(d.getDiscountType() == null ? null : d.getDiscountType().name()); e.setDiscountValue(d.getDiscountValue());
        e.setMaxDiscountAmount(d.getMaxDiscountAmount()); e.setMinOrderAmount(d.getMinOrderAmount()); e.setStartAt(d.getStartAt()); e.setEndAt(d.getEndAt());
        e.setUsageLimitTotal(d.getUsageLimitTotal()); e.setUsageLimitPerUser(d.getUsageLimitPerUser()); e.setUsedCount(d.getUsedCount()); e.setStatus(d.getStatus()); return e;
    }
}
