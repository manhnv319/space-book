import Link from "next/link";
import { notFound } from "next/navigation";
import { getBookById } from "@/lib/services/book-service";
import { formatVnd } from "@/lib/format/currency";
import { BookCover } from "@/components/ui/book-cover";
import { Badge } from "@/components/ui/badge";
import { AddToCartForm } from "@/components/cart/add-to-cart-form";
import { BookReviewPanel } from "@/components/review/book-review-panel";
import { getBookReviews, getMyBookReviewOptions } from "@/lib/services/review-service";
import { getCurrentUser } from "@/lib/bff/current-user";

type BookDetailPageProps = {
  params: Promise<{ id: string }>;
};

export default async function BookDetailPage({ params }: BookDetailPageProps) {
  const { id } = await params;
  const book = await getBookById(id);

  if (!book) {
    notFound();
  }

  const formattedListPrice = formatVnd(book.listPrice);
  const formattedRentalDay = formatVnd(book.rentalPriceDay);
  const formattedRentalWeek = formatVnd(book.rentalPriceWeek);
  const formattedRentalMonth = formatVnd(book.rentalPriceMonth);
  const formattedDeposit = formatVnd(book.depositAmount);
  const canBuy = book.listPrice > 0;
  const canRent = book.rentalPriceDay > 0;
  const hasCommerceOption = canBuy || canRent;
  const reviews = await getBookReviews(book.id);
  const user = await getCurrentUser();
  const reviewOptions = user ? await getMyBookReviewOptions(book.id) : [];

  return (
    <div className="book-detail-container">
      <div className="book-detail-layout">
        {/* Breadcrumb Navigation */}
        <nav className="breadcrumb">
          <Link href="/">Trang chủ</Link>
          <span className="breadcrumb-separator">/</span>
          <Link href="/sach">Sách</Link>
          <span className="breadcrumb-separator">/</span>
          <span className="breadcrumb-current">{book.title}</span>
        </nav>

        {/* Book Cover Image */}
        <div className="book-detail-cover">
          <BookCover src={book.imageUrl} alt={book.title} variant="detail" />
        </div>

        {/* Book Info */}
        <div className="book-detail-info">
          {book.categories && book.categories.length > 0 && (
            <div className="detail-category-list">
              {book.categories.map((cat, i) => (
                <Badge key={i} tone="muted">
                  {cat}
                </Badge>
              ))}
            </div>
          )}

          <h1 className="detail-title">{book.title}</h1>
          <p className="detail-publisher">
            Nhà xuất bản: <strong>{book.publisher}</strong> ({book.publishYear})
          </p>

          <div className="detail-summary">
            <h3>Tóm tắt nội dung</h3>
            <p>{book.description || "Chưa có thông tin mô tả cho cuốn sách này."}</p>
          </div>

          {hasCommerceOption ? (
            <div className="purchase-options">
              {canBuy && (
                <div className="option-card purchase-card">
                  <div className="option-header">
                    <h3>Mua Sở Hữu</h3>
                    <span className="option-price">{formattedListPrice}</span>
                  </div>
                  <p className="option-desc">Sách mới 100%, sở hữu vĩnh viễn.</p>
                  <AddToCartForm variant="purchase" bookId={book.id} />
                </div>
              )}
              {canRent && (
                <div className="option-card book-rental-option">
                  <div className="option-header">
                    <h3>Thuê Linh Hoạt</h3>
                    <span className="option-price">{formattedRentalDay}<small>/ngày</small></span>
                  </div>
                  <div className="rental-rates">
                    <div className="rate-row"><span>Gói tuần (7 ngày):</span><strong>{formattedRentalWeek}</strong></div>
                    <div className="rate-row"><span>Gói tháng (30 ngày):</span><strong>{formattedRentalMonth}</strong></div>
                    <div className="rate-row deposit-row"><span>Tiền đặt cọc:</span><strong>{formattedDeposit}</strong></div>
                  </div>
                  <p className="option-desc">Hoàn 100% tiền cọc khi trả sách đúng hạn và nguyên vẹn.</p>
                  <AddToCartForm
                    variant="rental"
                    bookId={book.id}
                    rentalPriceDay={book.rentalPriceDay}
                    rentalPriceWeek={book.rentalPriceWeek}
                    rentalPriceMonth={book.rentalPriceMonth}
                  />
                </div>
              )}
            </div>
          ) : (
            <p className="status-note">Sách này hiện được giới thiệu trong bài đọc; thông tin mua và thuê đang cập nhật.</p>
          )}

          {/* Book Metadata Attributes */}
          <div className="book-attributes">
            <h3>Thông tin chi tiết</h3>
            <div className="attribute-grid">
              <div className="attribute-item">
                <span className="attribute-label">Mã ISBN:</span>
                <span className="attribute-value">{book.isbn || "Đang cập nhật"}</span>
              </div>
              <div className="attribute-item">
                <span className="attribute-label">Định dạng:</span>
                <span className="attribute-value">
                  {book.format === "HARDCOVER" ? "Bìa cứng" : book.format === "PAPERBACK" ? "Bìa mềm" : "Đang cập nhật"}
                </span>
              </div>
              <div className="attribute-item">
                <span className="attribute-label">Ngôn ngữ:</span>
                <span className="attribute-value">{book.language === "vi" ? "Tiếng Việt" : "Tiếng Anh"}</span>
              </div>
              <div className="attribute-item">
                <span className="attribute-label">Số trang:</span>
                <span className="attribute-value">{book.pageCount ? `${book.pageCount} trang` : "Đang cập nhật"}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <BookReviewPanel bookId={book.id} reviews={reviews} options={reviewOptions} canReview={Boolean(user && reviewOptions.length)} />
    </div>
  );
}
