import Link from "next/link";

import { AuthBrand } from "@/components/auth-brand";
import { AuthDialog } from "@/components/auth-dialog";
import { AuthForm } from "@/components/auth-form";

export default async function LoginPage({
  searchParams,
}: Readonly<{ searchParams: Promise<{ next?: string }> }>) {
  const { next } = await searchParams;
  return (
    <div className="auth-page">
      <AuthDialog titleId="login-title">
        <Link className="auth-back" href="/">Về trang chủ</Link>
        <AuthBrand />
        <h1 className="sr-only" id="login-title">Đăng nhập</h1>
        <AuthForm mode="login" next={next} />
      </AuthDialog>
    </div>
  );
}
