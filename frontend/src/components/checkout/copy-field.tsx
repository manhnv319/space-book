"use client";

import { useState } from "react";

interface CopyFieldProps {
  label: string;
  value: string;
  /** Hint shown under the value — e.g. why the reference must be exact. */
  hint?: string;
}

/**
 * Client island: hiển thị một giá trị cần chép chính xác kèm nút sao chép.
 *
 * Dùng cho số tài khoản, số tiền và nội dung chuyển khoản — gõ tay sai một ký
 * tự là email báo có không khớp được đơn nào.
 */
export function CopyField({ label, value, hint }: CopyFieldProps) {
  const [copied, setCopied] = useState(false);

  async function copy() {
    try {
      await navigator.clipboard.writeText(value);
      setCopied(true);
      window.setTimeout(() => setCopied(false), 2000);
    } catch {
      // Trình duyệt từ chối clipboard (thiếu quyền, không phải https) — giá trị
      // vẫn hiện đầy đủ để chép tay, không cần báo lỗi làm khách hoảng.
      setCopied(false);
    }
  }

  return (
    <div className="copy-field">
      <span className="copy-field-label">{label}</span>
      <div className="copy-field-row">
        <output className="copy-field-value">{value}</output>
        <button type="button" className="button-small" onClick={copy}>
          {copied ? "Đã chép" : "Chép"}
        </button>
      </div>
      {hint && <p className="copy-field-hint">{hint}</p>}
    </div>
  );
}
