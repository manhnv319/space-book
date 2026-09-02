import Link from "next/link";

const ITEMS = [
  ["/sach", "Những cuốn được quan tâm", "Dạo một vòng qua tủ sách"],
  ["/sach?collection=new", "Sách cho khởi đầu mới", "Bắt đầu từ điều bạn tò mò"],
  ["/bai-viet", "Gợi ý đọc từ nhà sách", "Đọc chậm, chọn kỹ"],
  ["/goi-thue", "Đọc theo nhịp của bạn", "Gói thuê cần đăng nhập"],
] as const;

export function CampaignShortcutGrid() {
  return <section className="home-section shortcut-section" aria-labelledby="shortcut-title"><div className="section-header"><div><p className="eyebrow">Lựa chọn theo mùa</p><h2 id="shortcut-title">Tìm một cuốn dành cho hôm nay</h2></div></div><ul className="campaign-shortcut-grid">{ITEMS.map(([href, title, text]) => <li key={href}><Link href={href}><span>{text}</span><strong>{title}</strong><b aria-hidden="true">→</b></Link></li>)}</ul></section>;
}
