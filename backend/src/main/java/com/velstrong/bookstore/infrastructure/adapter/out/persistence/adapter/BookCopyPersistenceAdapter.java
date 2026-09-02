package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.BookCopy;
import com.velstrong.bookstore.domain.model.enums.book.BookCopyCondition;
import com.velstrong.bookstore.domain.model.enums.book.BookCopyStatus;
import com.velstrong.bookstore.domain.port.out.BookCopyRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.BookCopyJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaBookCopyRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Profile("postgres & !mongodb")
public class BookCopyPersistenceAdapter implements BookCopyRepository {

    private final JpaBookCopyRepository jpaRepository;

    public BookCopyPersistenceAdapter(JpaBookCopyRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    /**
     * Nạp bản ghi đang được quản lý rồi cập nhật lên đó, thay vì dựng một entity
     * mới có sẵn id.
     *
     * Entity này có `@Version`. Một entity dựng tay có id nhưng version null là
     * detached với optimistic lock chưa khởi tạo, và Hibernate từ chối lưu
     * ({@code DataIntegrityViolationException}) — nghĩa là mọi lần CẬP NHẬT đều
     * hỏng, chỉ INSERT (id null) chạy được. Nạp lại trước còn giữ đúng ý nghĩa
     * của optimistic locking: version hiện tại đi cùng bản ghi.
     */
    @Override
    public BookCopy save(BookCopy bookCopy) {
        BookCopyJpaEntity entity = bookCopy.getId() == null ? new BookCopyJpaEntity()
                : jpaRepository.findById(bookCopy.getId()).orElseGet(BookCopyJpaEntity::new);
        return toDomain(jpaRepository.save(applyTo(entity, bookCopy)));
    }

    @Override
    public Optional<BookCopy> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<BookCopy> findByBookId(Long bookId) {
        return jpaRepository.findByBookIdOrderByIdAsc(bookId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<BookCopy> findAvailableByBookId(Long bookId) {
        return jpaRepository.findByBookIdAndStatus(bookId, BookCopyStatus.AVAILABLE.name())
                .stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<BookCopy> findFirstAvailableByBookIdForUpdate(Long bookId) {
        return jpaRepository.findFirstByBookIdAndStatusOrderByIdAsc(bookId, BookCopyStatus.AVAILABLE.name())
                .map(this::toDomain);
    }

    @Override
    public int countAvailableByBookId(Long bookId) {
        return jpaRepository.countByBookIdAndStatus(bookId, BookCopyStatus.AVAILABLE.name());
    }

    private BookCopy toDomain(BookCopyJpaEntity e) {
        return BookCopy.reconstitute(e.getId(), e.getBookId(),
                e.getStatus() != null ? BookCopyStatus.valueOf(e.getStatus()) : null,
                e.getCondition() != null ? BookCopyCondition.valueOf(e.getCondition()) : null,
                e.getNotes());
    }

    private BookCopyJpaEntity applyTo(BookCopyJpaEntity e, BookCopy d) {
        e.setId(d.getId());
        e.setBookId(d.getBookId());
        e.setStatus(d.getStatus() != null ? d.getStatus().name() : null);
        e.setCondition(d.getCondition() != null ? d.getCondition().name() : null);
        e.setNotes(d.getNotes());
        return e;
    }

    @Override
    public java.util.List<BookCopy> findByIds(java.util.List<Long> ids) {
        if (ids == null || ids.isEmpty()) return java.util.List.of();
        return jpaRepository.findAllById(ids).stream().map(this::toDomain).toList();
    }
}
