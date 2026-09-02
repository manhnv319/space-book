import Link from "next/link";

import { AuthBrand } from "@/components/auth-brand";
import { AuthDialog } from "@/components/auth-dialog";
import { ForgotPasswordForm } from "@/components/password-reset-form";

export default function ForgotPasswordPage() {
  return <div className="auth-page"><AuthDialog titleId="forgot-password-title">
    <Link className="auth-back" href="/">Về trang chủ</Link>
    <AuthBrand />
    <ForgotPasswordForm titleId="forgot-password-title" />
  </AuthDialog></div>;
}
