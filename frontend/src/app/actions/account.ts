"use server";

import { revalidatePath } from "next/cache";
import { executeAuthenticatedMutation } from "@/app/actions/authenticated-mutation";

export type AccountActionResult = { status: "ok" | "error"; message: string };

export async function saveProfileAction(_previous: AccountActionResult, data: FormData): Promise<AccountActionResult> {
  const fullname = String(data.get("fullname") ?? "").trim();
  const phone = String(data.get("phone") ?? "").trim();
  const birthday = String(data.get("birthday") ?? "").trim();
  if (!fullname) return { status: "error", message: "Vui lòng nhập họ và tên." };
  try {
    await executeAuthenticatedMutation("/api/v1/users/me", { method: "PUT", headers: { "content-type": "application/json" }, body: JSON.stringify({ fullname, phone: phone || null, birthday: birthday || null }) });
    revalidatePath("/account", "layout");
    return { status: "ok", message: "Đã lưu thông tin cá nhân." };
  } catch { return { status: "error", message: "Không thể lưu thông tin lúc này." }; }
}

export async function changeAccountPasswordAction(_previous: AccountActionResult, data: FormData): Promise<AccountActionResult> {
  const currentPassword = String(data.get("currentPassword") ?? "");
  const newPassword = String(data.get("newPassword") ?? "");
  const confirmation = String(data.get("confirmation") ?? "");
  if (newPassword.length < 6 || newPassword !== confirmation) return { status: "error", message: "Mật khẩu mới tối thiểu 6 ký tự và phải trùng khớp." };
  try {
    await executeAuthenticatedMutation("/api/v1/users/me/password", { method: "PUT", headers: { "content-type": "application/json" }, body: JSON.stringify({ currentPassword, newPassword }) });
    return { status: "ok", message: "Đã đổi mật khẩu." };
  } catch { return { status: "error", message: "Mật khẩu hiện tại không đúng hoặc phiên đã hết hạn." }; }
}
