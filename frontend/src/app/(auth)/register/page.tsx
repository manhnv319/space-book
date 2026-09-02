import Link from "next/link";

import { AuthBrand } from "@/components/auth-brand";
import { AuthDialog } from "@/components/auth-dialog";
import { AuthForm } from "@/components/auth-form";

export default async function RegisterPage({
  searchParams,
}: Readonly<{ searchParams: Promise<{ next?: string }> }>) {
  const { next } = await searchParams;
  return (
    <div className="auth-page">
      <AuthDialog titleId="register-title">
        <Link className="auth-back" href="/">Về trang chủ</Link>
        <AuthBrand />
        <h1 className="sr-only" id="register-title">Tạo tài khoản</h1>
        <AuthForm mode="register" next={next} />
      </AuthDialog>
    </div>
  );
}
