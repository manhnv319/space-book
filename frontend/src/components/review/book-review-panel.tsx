"use client";

import { useActionState } from "react";
import { saveBookReviewAction, type ReviewActionResult } from "@/app/actions/book-review";
import type { BookReview, ReviewPage, ReviewTransaction } from "@/lib/types/review";

const INITIAL: ReviewActionResult = { status: "ok", message: "" };
const sourceLabel = (source: string) => source === "RENTAL" ? "Đã thuê" : "Đã mua";

function Stars({ rating }: { rating: number }) { return <span className="review-stars" aria-label={`${rating} trên 5 sao`}>{"★".repeat(rating)}{"☆".repeat(5 - rating)}</span>; }

function ReviewForm({ bookId, transaction }: { bookId: number; transaction: ReviewTransaction }) {
  const [state, action, pending] = useActionState(saveBookReviewAction, INITIAL);
  const review = transaction.review;
  return <form action={action} className="review-form">
    <input name="bookId" type="hidden" value={bookId} /><input name="orderItemId" type="hidden" value={transaction.orderItemId} />
    {review ? <input name="reviewId" type="hidden" value={review.id} /> : null}
    <p className="review-form-label">{review ? "Sửa đánh giá" : "Đánh giá giao dịch"} · {sourceLabel(transaction.source)}</p>
    <div className="review-rating-input" role="radiogroup" aria-label="Chọn số sao">{[1, 2, 3, 4, 5].map((value) => <label key={value}><input defaultChecked={review?.rating === value || (!review && value === 5)} name="rating" type="radio" value={value} /><span aria-hidden="true">★</span><span className="sr-only">{value} sao</span></label>)}</div>
    <textarea defaultValue={review?.comment ?? ""} name="comment" placeholder="Chia sẻ cảm nhận của bạn…" maxLength={2000} required />
    <button className="button" disabled={pending} type="submit">{pending ? "Đang lưu…" : "Lưu đánh giá"}</button>
    {state.message ? <p className={state.status === "error" ? "form-error" : "form-success"}>{state.message}</p> : null}
  </form>;
}

export function BookReviewPanel({ bookId, reviews, options, canReview }: { bookId: number; reviews: ReviewPage; options: ReviewTransaction[]; canReview: boolean }) {
  return <section className="book-review-panel" aria-labelledby="book-reviews-title">
    <div className="review-summary"><div><p className="eyebrow">Cộng đồng độc giả</p><h2 id="book-reviews-title">Đánh giá ({reviews.totalElements})</h2></div>{reviews.totalElements ? <Stars rating={Math.round(reviews.content.reduce((sum, item) => sum + item.rating, 0) / reviews.content.length)} /> : <span className="review-empty">Chưa có đánh giá</span>}</div>
    {canReview ? <div className="review-forms">{options.map((transaction) => <ReviewForm bookId={bookId} key={transaction.orderItemId} transaction={transaction} />)}</div> : null}
    <div className="review-list">{reviews.content.map((review: BookReview) => <article className="review-item" key={review.id}><div><Stars rating={review.rating} /><span className="review-source">{sourceLabel(review.source)}</span></div><p>{review.comment}</p></article>)}</div>
  </section>;
}
