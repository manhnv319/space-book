const PROPS = [
  ["Thuê linh hoạt", "Chọn kỳ thuê phù hợp với nhịp đọc của bạn."],
  ["Giao dịch rõ ràng", "Phí thuê, tiền cọc và tổng thanh toán được tách bạch."],
  ["Khám phá có chọn lọc", "Tìm sách theo chủ đề, tựa sách hoặc bài viết gợi ý."],
] as const;

export function ValueProps() {
  return <section className="home-section value-props" aria-label="Cam kết Sách Nhà"><ul className="features-grid">{PROPS.map(([title, body], index) => <li className="feature-card" key={title}><span aria-hidden="true" className="feature-card-index">0{index + 1}</span><div><h3>{title}</h3><p>{body}</p></div></li>)}</ul></section>;
}
