import Link from "next/link";

const STEPS = [
  ["Chọn sách và kỳ thuê", "Chọn thời hạn theo ngày, tuần hoặc tháng ngay trên trang chi tiết."],
  ["Đặt cọc và nhận sách", "Phí thuê và tiền cọc được máy chủ xác nhận rõ ràng trong giỏ hàng."],
  ["Trả sách, nhận lại cọc", "Theo dõi hạn trả để hoàn tất quá trình thuê thuận tiện hơn."],
] as const;

export function RentalSteps() {
  return <section className="rental-steps home-section" aria-labelledby="rental-title"><div className="section-header"><div><p className="eyebrow">Thuê sách</p><h2 id="rental-title">Đọc nhiều hơn, trả ít hơn</h2></div><Link className="text-link" href="/goi-thue">Xem gói thuê <span aria-hidden="true">→</span></Link></div><ol className="rental-steps-row">{STEPS.map(([title, body], index) => <li className="rental-step" key={title}><span aria-hidden="true" className="rental-step-number">0{index + 1}</span><h3>{title}</h3><p>{body}</p></li>)}</ol></section>;
}
