"use client";

import { useState } from "react";

import { formatVnd } from "@/lib/format/currency";
import type { RentalTermUnit } from "@/lib/types/cart";

interface RentalTermPickerProps {
  rentalPriceDay: number;
  rentalPriceWeek: number;
  rentalPriceMonth: number;
}

const UNIT_META: Record<RentalTermUnit, { label: string; noun: string; maxTerms: number }> = {
  DAY: { label: "Ngày", noun: "ngày", maxTerms: 14 },
  WEEK: { label: "Tuần", noun: "tuần", maxTerms: 4 },
  MONTH: { label: "Tháng", noun: "tháng", maxTerms: 3 },
};

/**
 * Client island: chọn đơn vị + số kỳ thuê. Chỉ hiển thị đơn giá BE trả về
 * cho đơn vị đang chọn — KHÔNG nhân với số kỳ, không cộng dồn. Tổng tiền và
 * tiền cọc thật sự luôn do BE tính (xem ở /gio-hang sau khi submit).
 */
export function RentalTermPicker({ rentalPriceDay, rentalPriceWeek, rentalPriceMonth }: RentalTermPickerProps) {
  const [unit, setUnit] = useState<RentalTermUnit>("DAY");

  const unitPrice = unit === "DAY" ? rentalPriceDay : unit === "WEEK" ? rentalPriceWeek : rentalPriceMonth;
  const meta = UNIT_META[unit];

  return (
    <div className="rental-term-picker">
      <div className="rental-term-fields">
        <label>
          Đơn vị thuê
          <select
            name="rentalTermUnit"
            value={unit}
            onChange={(event) => setUnit(event.target.value as RentalTermUnit)}
          >
            {Object.entries(UNIT_META).map(([value, info]) => (
              <option key={value} value={value}>
                {info.label}
              </option>
            ))}
          </select>
        </label>
        <label>
          Số kỳ
          <select name="rentalTermValue" key={unit} defaultValue={1}>
            {Array.from({ length: meta.maxTerms }, (_, i) => i + 1).map((n) => (
              <option key={n} value={n}>
                {n} {meta.noun}
              </option>
            ))}
          </select>
        </label>
      </div>
      <p className="rental-term-price">
        Đơn giá: <strong>{formatVnd(unitPrice)}</strong> / {meta.noun}
      </p>
      <p className="rental-term-note">Tổng tiền và tiền cọc do máy chủ tính, xem ở giỏ hàng.</p>
    </div>
  );
}
