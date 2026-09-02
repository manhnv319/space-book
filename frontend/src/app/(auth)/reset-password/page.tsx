import Link from "next/link";

import { AuthBrand } from "@/components/auth-brand";
import { AuthDialog } from "@/components/auth-dialog";
import { ResetPasswordForm } from "@/components/password-reset-form";

export default function ResetPasswordPage() {
  return <div className="auth-page"><AuthDialog titleId="reset-password-title">
    <Link className="auth-back" href="/">Về trang chủ</Link>
    <AuthBrand />
    <h1 id="reset-password-title">Đặt lại mật khẩu</h1><p className="auth-description">Dùng mã 6 chữ số trong email của bạn.</p><ResetPasswordForm />
  </AuthDialog></div>;
}
