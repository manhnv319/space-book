"use server";

import { revalidatePath } from "next/cache";

import { executeAuthenticatedMutation } from "@/app/actions/authenticated-mutation";
import { BackendError } from "@/lib/bff/backend-error";
import { listWards, resolveNames, type Ward } from "@/lib/vn-address/units";

export type AddressState = { status: "idle" | "ok" | "error"; message: string };

/** Chấp nhận 0xxxxxxxxx (10 số) và +84xxxxxxxxx. */
const PHONE = /^(0\d{9}|\+84\d{9})$/;

function revalidateAddressViews(): void {
  revalidatePath("/account");
  revalidatePath("/checkout");
}

function addressError(error: unknown): string {
  if (error instanceof BackendError) {
    if (error.status === 404) return "Không tìm thấy địa chỉ này.";
    if (error.status === 400) return "Thông tin địa chỉ không hợp lệ.";
  }
  return "Không lưu được địa chỉ lúc này. Vui lòng thử lại.";
}

/** Danh sách phường/xã của một tỉnh — client gọi khi người dùng đổi tỉnh. */
export async function loadWardsAction(provinceCode: string): Promise<Ward[]> {
  return listWards(provinceCode);
}

interface ParsedAddress {
  fullName: string;
  phone: string;
  province: string;
  ward: string;
  addressDetail: string;
  isDefault: boolean;
}

function parse(data: FormData): { value: ParsedAddress } | { error: string } {
  const fullName = String(data.get("fullName") ?? "").trim();
  const phone = String(data.get("phone") ?? "").replace(/\s/g, "");
  const addressDetail = String(data.get("addressDetail") ?? "").trim();
  const provinceCode = String(data.get("provinceCode") ?? "");
  const wardCode = String(data.get("wardCode") ?? "");

  if (fullName.length < 2) return { error: "Vui lòng nhập họ tên người nhận." };
  if (!PHONE.test(phone)) return { error: "Số điện thoại không hợp lệ (10 số, bắt đầu bằng 0)." };
  if (!addressDetail) return { error: "Vui lòng nhập số nhà, tên đường." };

  // Tên tỉnh/phường lấy từ dữ liệu server theo mã, không lấy chuỗi client gửi lên.
  const names = resolveNames(provinceCode, wardCode);
  if (!names) return { error: "Vui lòng chọn tỉnh/thành và phường/xã." };

  return {
    value: {
      fullName: fullName.slice(0, 120),
      phone,
      province: names.province,
      ward: names.ward,
      addressDetail: addressDetail.slice(0, 200),
      isDefault: data.get("isDefault") === "on",
    },
  };
}

/**
 * `district` không gửi lên: từ 01/07/2025 địa chỉ hành chính chỉ còn hai cấp.
 * Backend đã bỏ ràng buộc bắt buộc cho trường này.
 */
export async function saveAddressAction(_state: AddressState, data: FormData): Promise<AddressState> {
  const parsed = parse(data);
  if ("error" in parsed) return { status: "error", message: parsed.error };

  try {
    await executeAuthenticatedMutation<unknown>("/api/v1/addresses", {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify(parsed.value),
    });
  } catch (error) {
    console.error("Failed to save address:", error);
    return { status: "error", message: addressError(error) };
  }

  revalidateAddressViews();
  return { status: "ok", message: "Đã thêm địa chỉ." };
}

export async function deleteAddressAction(_state: AddressState, data: FormData): Promise<AddressState> {
  const rawId = String(data.get("addressId") ?? "");
  if (!/^\d+$/.test(rawId)) return { status: "error", message: "Địa chỉ không hợp lệ." };

  try {
    await executeAuthenticatedMutation<unknown>(`/api/v1/addresses/${rawId}`, { method: "DELETE" });
  } catch (error) {
    console.error("Failed to delete address:", error);
    return { status: "error", message: addressError(error) };
  }

  revalidateAddressViews();
  return { status: "ok", message: "Đã xoá địa chỉ." };
}
