/**
 * Biểu tượng cho từng tab đơn hàng, theo lối line-icon Shopee dùng.
 *
 * SVG vẽ tay, không phải emoji: emoji đổi hình theo hệ điều hành và font, nên
 * cùng một trang sẽ trông khác nhau trên máy khác nhau — và không chỉnh được nét
 * hay màu cho khớp phần còn lại của giao diện.
 *
 * Tất cả dùng chung khung 24×24, nét 1.6, không tô nền, `currentColor` — nên
 * chúng thừa hưởng màu của tab và tự đúng ở cả trạng thái thường lẫn đang chọn.
 */
interface IconProps {
  className?: string;
}

const SHARED = {
  viewBox: "0 0 24 24",
  fill: "none",
  stroke: "currentColor",
  strokeWidth: 1.6,
  strokeLinecap: "round" as const,
  strokeLinejoin: "round" as const,
  "aria-hidden": true,
  focusable: false,
};

/** Tất cả — chồng phiếu. */
export function ReceiptStackIcon({ className }: IconProps) {
  return (
    <svg {...SHARED} className={className}>
      <path d="M6 4h9l3 3v13l-2.2-1.4L13.6 20l-2.2-1.4L9.2 20 7 18.6 6 20V4Z" />
      <path d="M9 9h6M9 13h4" />
    </svg>
  );
}

/** Chờ thanh toán — ví tiền. */
export function WalletIcon({ className }: IconProps) {
  return (
    <svg {...SHARED} className={className}>
      <path d="M3 8.5A2.5 2.5 0 0 1 5.5 6H17a2 2 0 0 1 2 2v1" />
      <path d="M3 8.5V17a2 2 0 0 0 2 2h13a2 2 0 0 0 2-2v-2" />
      <path d="M21 10.5v4h-4a2 2 0 0 1 0-4h4Z" />
    </svg>
  );
}

/** Đang chuẩn bị — thùng hàng. */
export function PackageIcon({ className }: IconProps) {
  return (
    <svg {...SHARED} className={className}>
      <path d="M3 7.5 12 3l9 4.5v9L12 21l-9-4.5v-9Z" />
      <path d="m3 7.5 9 4.5 9-4.5M12 12v9" />
    </svg>
  );
}

/** Đang giao — xe tải. */
export function TruckIcon({ className }: IconProps) {
  return (
    <svg {...SHARED} className={className}>
      <path d="M3 6h10v10H3zM13 9h4l3 3v4h-7z" />
      <circle cx="7" cy="18" r="1.7" />
      <circle cx="17" cy="18" r="1.7" />
    </svg>
  );
}

/** Hoàn thành — dấu tích trong vòng tròn. */
export function CheckCircleIcon({ className }: IconProps) {
  return (
    <svg {...SHARED} className={className}>
      <circle cx="12" cy="12" r="9" />
      <path d="m8 12 2.8 2.8L16 9.5" />
    </svg>
  );
}

/** Đã huỷ — dấu nhân trong vòng tròn. */
export function CancelCircleIcon({ className }: IconProps) {
  return (
    <svg {...SHARED} className={className}>
      <circle cx="12" cy="12" r="9" />
      <path d="m9 9 6 6M15 9l-6 6" />
    </svg>
  );
}

/** Tra theo khoá tab; thiếu thì không vẽ gì, tab vẫn dùng được bằng chữ. */
export function OrderTabIcon({ tabKey, className }: { tabKey: string; className?: string }) {
  switch (tabKey) {
    case "all": return <ReceiptStackIcon className={className} />;
    case "unpaid": return <WalletIcon className={className} />;
    case "preparing": return <PackageIcon className={className} />;
    case "shipping": return <TruckIcon className={className} />;
    case "done": return <CheckCircleIcon className={className} />;
    case "cancelled": return <CancelCircleIcon className={className} />;
    default: return null;
  }
}
