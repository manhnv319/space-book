"use client";

import { useActionState, useState, useTransition } from "react";

import { loadWardsAction, saveAddressAction, type AddressState } from "@/app/actions/address";
import { SearchableSelect } from "@/components/ui/searchable-select";
import type { Province, Ward } from "@/lib/vn-address/units";

const INITIAL: AddressState = { status: "idle", message: "" };

/**
 * Client island: nhập địa chỉ theo cấu trúc hành chính hai cấp (tỉnh → phường/xã).
 *
 * Chỉ nhận sẵn 34 tỉnh/thành (~1 KB). Phường/xã nạp theo tỉnh đã chọn qua server
 * action, nên cả bộ dữ liệu 220 KB không bao giờ xuống trình duyệt.
 *
 * Form gửi lên *mã* tỉnh/phường, không gửi tên — server tra ra tên chuẩn từ dữ
 * liệu của mình. Client không quyết định được tên đơn vị hành chính ghi vào đơn.
 */
export function AddressForm({ provinces }: { provinces: Province[] }) {
  const [state, formAction, pending] = useActionState(saveAddressAction, INITIAL);
  const [provinceCode, setProvinceCode] = useState("");
  const [wards, setWards] = useState<Ward[]>([]);
  const [loadingWards, startLoadingWards] = useTransition();

  /**
   * Nạp ngay trong handler, không qua `useEffect`: đây là hệ quả trực tiếp của
   * thao tác người dùng, không phải việc đồng bộ với hệ thống bên ngoài.
   */
  function selectProvince(code: string) {
    setProvinceCode(code);
    setWards([]);
    if (!code) return;
    startLoadingWards(async () => setWards(await loadWardsAction(code)));
  }

  return (
    <form action={formAction} className="address-form">
      <div className="address-form-row">
        <label>
          <span>Họ tên người nhận</span>
          <input name="fullName" required minLength={2} maxLength={120} autoComplete="name" />
        </label>
        <label>
          <span>Số điện thoại</span>
          <input name="phone" required inputMode="tel" autoComplete="tel" placeholder="0912345678" />
        </label>
      </div>

      <div className="address-form-row">
        <SearchableSelect
          name="provinceCode"
          label="Tỉnh / Thành phố"
          options={provinces}
          placeholder="Gõ để tìm, ví dụ: ha noi"
          required
          onSelect={selectProvince}
        />
        <SearchableSelect
          // Đổi tỉnh thì dựng lại ô này để lựa chọn cũ không còn treo ở đó.
          key={provinceCode || "no-province"}
          name="wardCode"
          label="Phường / Xã"
          options={wards}
          placeholder={
            !provinceCode ? "Chọn tỉnh/thành trước" : loadingWards ? "Đang tải…" : "Gõ để tìm phường/xã"
          }
          disabled={!provinceCode || loadingWards}
          required
        />
      </div>

      <label>
        <span>Số nhà, tên đường</span>
        <input name="addressDetail" required maxLength={200} autoComplete="address-line1" placeholder="Số 12, ngõ 34 Trần Duy Hưng" />
      </label>

      <label className="address-form-check">
        <input type="checkbox" name="isDefault" />
        <span>Đặt làm địa chỉ mặc định</span>
      </label>

      {state.status !== "idle" && (
        <p className={state.status === "error" ? "form-status" : "form-status form-status--ok"} role="status">
          {state.message}
        </p>
      )}

      <button className="button" type="submit" disabled={pending}>
        {pending ? "Đang lưu…" : "Lưu địa chỉ"}
      </button>
    </form>
  );
}
