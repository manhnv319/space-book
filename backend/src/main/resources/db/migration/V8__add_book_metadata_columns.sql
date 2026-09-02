ALTER TABLE books ADD COLUMN created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE books ADD COLUMN is_featured BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE books ADD COLUMN is_bestseller BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX idx_books_active_created_at ON books (is_active, created_at DESC, id DESC);
CREATE INDEX idx_books_active_featured   ON books (is_active, is_featured);
CREATE INDEX idx_books_active_bestseller ON books (is_active, is_bestseller);
