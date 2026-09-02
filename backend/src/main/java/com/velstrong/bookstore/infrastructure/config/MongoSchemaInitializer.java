package com.velstrong.bookstore.infrastructure.config;

import com.mongodb.MongoCommandException;
import org.bson.Document;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@Profile("mongodb & !postgres")
public class MongoSchemaInitializer implements ApplicationRunner {

    private static final String COUNTERS_COLLECTION = "_mongo_sequences";
    private static final Set<String> SEQUENCED_COLLECTIONS = Set.of(
            "books", "book_copies", "categories", "users", "user_addresses", "carts", "cart_items",
            "orders", "order_items", "order_status_history", "subscriptions", "customer_subscriptions",
            "rentals", "rental_fulfillments", "payments", "vouchers", "voucher_usages", "blog_posts",
            "book_reviews", "support_conversations", "support_messages", "support_message_attachments",
            "user_notifications", "push_subscriptions", "unmatched_transfers");

    private final MongoTemplate mongo;

    public MongoSchemaInitializer(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    @Override
    public void run(ApplicationArguments args) {
        createIndexes();
        reconcileSequences();
        seedRbacIfEmpty();
    }

    private void createIndexes() {
        unique("books", "isbn", true);
        index("books", "isActive", Sort.Direction.ASC, "createdAt", Sort.Direction.DESC);
        index("book_categories", "categoryId", Sort.Direction.ASC, "bookId", Sort.Direction.ASC);
        uniqueCompound("book_categories", "bookId", "categoryId");
        unique("categories", "name", false);
        unique("categories", "slug", true);

        unique("users", "username", false);
        unique("users", "email", false);
        unique("users", "iamId", true);
        unique("roles", "code", false);
        unique("permissions", "code", false);
        uniqueCompound("user_roles", "userId", "roleId");
        uniqueCompound("user_permissions", "userId", "permissionId");
        uniqueCompound("role_permissions", "roleId", "permissionId");

        unique("carts", "userId", true);
        uniqueCompound("cart_items", "cartId", "bookId", "itemType", "rentalTermValue", "rentalTermUnit");
        unique("orders", "orderCode", false);
        index("orders", "userId", Sort.Direction.ASC, "createdAt", Sort.Direction.DESC);
        index("order_items", "orderId", Sort.Direction.ASC);
        index("order_status_history", "orderId", Sort.Direction.ASC, "changedAt", Sort.Direction.ASC);

        unique("payments", "transferReference", true);
        index("payments", "orderId", Sort.Direction.ASC, "createdAt", Sort.Direction.DESC);
        index("rentals", "userId", Sort.Direction.ASC, "createdAt", Sort.Direction.DESC);
        unique("rentals", "orderItemId", true);
        index("rentals", "actualReturnDate", Sort.Direction.ASC, "plannedReturnDate", Sort.Direction.ASC, "status", Sort.Direction.ASC);
        unique("rental_fulfillments", "orderId", false);
        index("rental_fulfillments", "status", Sort.Direction.ASC, "updatedAt", Sort.Direction.ASC);
        unique("vouchers", "code", false);
        index("vouchers", "status", Sort.Direction.ASC, "startAt", Sort.Direction.ASC, "endAt", Sort.Direction.ASC);
        index("voucher_usages", "userId", Sort.Direction.ASC, "voucherId", Sort.Direction.ASC);
        unique("blog_posts", "slug", false);
        index("blog_posts", "status", Sort.Direction.ASC, "publishedAt", Sort.Direction.DESC);
        uniqueCompound("book_reviews", "userId", "orderItemId");
        uniqueCompound("push_subscriptions", "userId", "endpoint");
        unique("processed_bank_messages", "messageId", false);
        index("unmatched_transfers", "createdAt", Sort.Direction.DESC);
        unique("processed_bank_messages", "bankTxnRef", true);
        index("support_conversations", "lastMessageAt", Sort.Direction.DESC);
        unique("support_conversations", "userId", false);
        index("support_messages", "conversationId", Sort.Direction.ASC, "createdAt", Sort.Direction.ASC);
        index("support_message_attachments", "messageId", Sort.Direction.ASC, "_id", Sort.Direction.ASC);
        index("user_notifications", "userId", Sort.Direction.ASC, "createdAt", Sort.Direction.DESC);
        index("user_notifications", "userId", Sort.Direction.ASC, "readAt", Sort.Direction.ASC);
        index("user_addresses", "userId", Sort.Direction.ASC, "createdAt", Sort.Direction.DESC);
        index("book_copies", "bookId", Sort.Direction.ASC, "status", Sort.Direction.ASC);
    }

    private void reconcileSequences() {
        for (String collection : SEQUENCED_COLLECTIONS) {
            Document highest = mongo.findOne(Query.query(new Criteria()).with(
                    Sort.by(Sort.Direction.DESC, "_id")).limit(1), Document.class, collection);
            if (highest == null || !(highest.get("_id") instanceof Number number)) continue;
            mongo.upsert(Query.query(Criteria.where("_id").is(collection)),
                    new Update().max("value", number.longValue()), Document.class, COUNTERS_COLLECTION);
        }
    }

    private void seedRbacIfEmpty() {
        if (mongo.count(new Query(), Document.class, "roles") > 0) return;
        List<Document> roles = List.of(
                new Document("_id", 1L).append("code", "CUSTOMER").append("name", "Customer").append("description", "Customer account"),
                new Document("_id", 2L).append("code", "SALES_STAFF").append("name", "Sales Staff").append("description", "Sales and rental counter staff"),
                new Document("_id", 3L).append("code", "WAREHOUSE_MANAGER").append("name", "Warehouse Manager").append("description", "Inventory and book-copy manager"),
                new Document("_id", 4L).append("code", "ADMIN").append("name", "Administrator").append("description", "System administrator"));
        List<String> permissionCodes = List.of("book:read", "order:create", "order:read:own", "rental:read:own", "rental:extend:own", "subscription:purchase",
                "order:read:all", "order:update-status", "rental:read:all", "rental:checkin", "order:create:counter", "payment:refund", "copy:manage",
                "stock:receive", "stock:adjust", "inventory:audit", "book:manage", "voucher:manage", "subscription:manage", "user:manage", "role:assign", "report:view", "config:manage");
        roles.forEach(role -> mongo.insert(role, "roles"));
        for (int i = 0; i < permissionCodes.size(); i++) {
            mongo.insert(new Document("_id", (long) i + 1).append("code", permissionCodes.get(i)), "permissions");
        }
        long relationId = 1;
        for (long roleId = 1; roleId <= 4; roleId++) {
            for (long permissionId = 1; permissionId <= 6; permissionId++) {
                mongo.insert(new Document("_id", relationId++).append("roleId", roleId).append("permissionId", permissionId), "role_permissions");
            }
        }
        for (long permissionId = 7; permissionId <= 12; permissionId++) {
            mongo.insert(new Document("_id", relationId++).append("roleId", 2L).append("permissionId", permissionId), "role_permissions");
            mongo.insert(new Document("_id", relationId++).append("roleId", 4L).append("permissionId", permissionId), "role_permissions");
        }
        for (long permissionId = 13; permissionId <= 16; permissionId++) {
            mongo.insert(new Document("_id", relationId++).append("roleId", 3L).append("permissionId", permissionId), "role_permissions");
            mongo.insert(new Document("_id", relationId++).append("roleId", 4L).append("permissionId", permissionId), "role_permissions");
        }
        for (long permissionId = 17; permissionId <= 23; permissionId++) {
            mongo.insert(new Document("_id", relationId++).append("roleId", 4L).append("permissionId", permissionId), "role_permissions");
        }
    }

    private void unique(String collection, String field, boolean sparse) {
        Index index = new Index().on(field, Sort.Direction.ASC).unique().named("uk_" + collection + "_" + field);
        if (sparse) index.sparse();
        safeEnsureIndex(collection, index);
    }

    private void uniqueCompound(String collection, String... fields) {
        Index index = new Index();
        for (String field : fields) index.on(field, Sort.Direction.ASC);
        safeEnsureIndex(collection, index.unique().named("uk_" + collection + "_" + String.join("_", fields)));
    }

    private void index(String collection, Object... fields) {
        Index index = new Index();
        for (int i = 0; i < fields.length; i += 2) index.on((String) fields[i], (Sort.Direction) fields[i + 1]);
        StringBuilder name = new StringBuilder("idx_").append(collection);
        for (int i = 0; i < fields.length; i += 2) name.append('_').append(fields[i]);
        safeEnsureIndex(collection, index.named(name.toString()));
    }

    private void safeEnsureIndex(String collection, Index index) {
        IndexOperations operations = mongo.indexOps(collection);
        try {
            operations.ensureIndex(index);
        } catch (DataIntegrityViolationException exception) {
            Throwable cause = exception;
            while (cause != null) {
                if (cause instanceof MongoCommandException mongoException
                        && (mongoException.getErrorCode() == 85 || mongoException.getErrorCode() == 86)) {
                    // A previous schema version may have created an equivalent index
                    // under another name. Keep existing data and continue startup.
                    return;
                }
                cause = cause.getCause();
            }
            throw exception;
        }
    }
}
