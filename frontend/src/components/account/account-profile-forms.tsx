"use client";

import { useActionState } from "react";
import { changeAccountPasswordAction, saveProfileAction, type AccountActionResult } from "@/app/actions/account";

const INITIAL: AccountActionResult = { status: "ok", message: "" };

function Status({ result }: { result: AccountActionResult }) { return result.message ? <p className={result.status === "error" ? "account-form-status is-error" : "account-form-status"}>{result.message}</p> : null; }

export function PersonalProfileForm({ user }: { user: { fullname?: string; email: string; phone?: string; birthday?: string } }) {
  const [state, action, pending] = useActionState(saveProfileAction, INITIAL);
  return <form action={action} className="account-settings-form"><div className="account-form-grid"><label>Họ và tên<input defaultValue={user.fullname ?? ""} name="fullname" required /></label><label>Email<input defaultValue={user.email} disabled /></label><label>Số điện thoại<input defaultValue={user.phone ?? ""} name="phone" inputMode="tel" /></label><label>Ngày sinh<input defaultValue={user.birthday?.slice(0, 10) ?? ""} name="birthday" type="date" /></label></div><button className="button" disabled={pending} type="submit">{pending ? "Đang lưu…" : "Lưu thay đổi"}</button><Status result={state} /></form>;
}

export function PasswordSecurityForm() {
  const [state, action, pending] = useActionState(changeAccountPasswordAction, INITIAL);
  return <form action={action} className="account-settings-form account-security-form"><label>Mật khẩu hiện tại<input autoComplete="current-password" name="currentPassword" required type="password" /></label><label>Mật khẩu mới<input autoComplete="new-password" name="newPassword" required type="password" /></label><label>Nhập lại mật khẩu mới<input autoComplete="new-password" name="confirmation" required type="password" /></label><button className="button" disabled={pending} type="submit">{pending ? "Đang cập nhật…" : "Đổi mật khẩu"}</button><Status result={state} /></form>;
}
