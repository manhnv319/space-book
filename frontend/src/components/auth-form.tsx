"use client";

import Link from "next/link";
import { useActionState, useState } from "react";

import {
  loginAction,
  registerAction,
  verifyEmailAction,
  type AuthFormState,
} from "@/app/actions/auth";

const initialState: AuthFormState = {};

type AuthFormProps = { mode: "login" | "register"; next?: string };

function FieldError({ id, message }: Readonly<{ id: string; message?: string }>) {
  return message ? <p className="field-error" id={id} role="alert">{message}</p> : null;
}

function EyeIcon({ hidden }: Readonly<{ hidden: boolean }>) {
  return hidden ? (
    <svg aria-hidden="true" viewBox="0 0 24 24"><path d="m3 3 18 18M10.6 10.6a2 2 0 0 0 2.8 2.8M9.9 5.2A10.7 10.7 0 0 1 12 5c5 0 8.7 5 8.7 5a16 16 0 0 1-3.1 3.6M6.2 6.2C3.9 7.9 2.3 10 2.3 10s3.7 5 9.7 5c.8 0 1.5-.1 2.2-.3" fill="none" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.8" /></svg>
  ) : (
    <svg aria-hidden="true" viewBox="0 0 24 24"><path d="M2.3 10S6 5 12 5s9.7 5 9.7 5-3.7 5-9.7 5-9.7-5-9.7-5Z" fill="none" stroke="currentColor" strokeLinejoin="round" strokeWidth="1.8" /><circle cx="12" cy="10" r="2.2" fill="none" stroke="currentColor" strokeWidth="1.8" /></svg>
  );
}

export function AuthForm({ mode, next = "/account" }: Readonly<AuthFormProps>) {
  const isRegister = mode === "register";
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [confirmation, setConfirmation] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [state, action, pending] = useActionState<AuthFormState, FormData>(
    isRegister ? registerAction : loginAction,
    initialState,
  );

  if (isRegister && state.verified) {
    return <div aria-live="polite" className="reset-success"><p>Email đã được xác minh. Bạn có thể đăng nhập ngay.</p><Link className="button" href="/login">Đăng nhập</Link></div>;
  }
  if (isRegister && state.verificationRequired) {
    return <EmailVerificationForm email={state.verificationEmail ?? ""} />;
  }

  return (
    <form action={action} className="auth-form" noValidate>
      <input name="next" type="hidden" value={next} />
      {isRegister ? (
        <label>
          Họ và tên
          <input aria-describedby={state.fields?.fullname ? "fullname-error" : undefined} autoComplete="name" name="fullname" required />
          <FieldError id="fullname-error" message={state.fields?.fullname} />
        </label>
      ) : null}
      <label>
        {isRegister ? "Tên đăng nhập" : "Tên đăng nhập/Email"}
        <input aria-describedby={state.fields?.username ? "username-error" : undefined} autoComplete="username" minLength={3} name="username" onChange={(event) => setUsername(event.target.value)} required value={username} />
        <FieldError id="username-error" message={state.fields?.username} />
      </label>
      {isRegister ? (
        <label>
          Email
          <input aria-describedby={state.fields?.email ? "email-error" : undefined} autoComplete="email" inputMode="email" name="email" required type="email" />
          <FieldError id="email-error" message={state.fields?.email} />
        </label>
      ) : null}
      <label>
        Mật khẩu
        <span className="password-field">
          <input aria-describedby={state.fields?.password ? "password-error" : undefined} autoComplete={isRegister ? "new-password" : "current-password"} minLength={6} name="password" onChange={(event) => setPassword(event.target.value)} required type={showPassword ? "text" : "password"} value={password} />
          <button aria-label={showPassword ? "Ẩn mật khẩu" : "Hiện mật khẩu"} className="password-toggle" onClick={() => setShowPassword((visible) => !visible)} type="button">
            <EyeIcon hidden={showPassword} />
          </button>
        </span>
        <FieldError id="password-error" message={state.fields?.password} />
      </label>
      {isRegister ? (
        <label>
          Nhập lại mật khẩu
          <span className="password-field">
            <input aria-describedby={state.fields?.confirmation ? "confirmation-error" : undefined} autoComplete="new-password" name="confirmation" onChange={(event) => setConfirmation(event.target.value)} required type={showPassword ? "text" : "password"} value={confirmation} />
            <button aria-label={showPassword ? "Ẩn mật khẩu" : "Hiện mật khẩu"} className="password-toggle" onClick={() => setShowPassword((visible) => !visible)} type="button">
              <EyeIcon hidden={showPassword} />
            </button>
          </span>
          <FieldError id="confirmation-error" message={state.fields?.confirmation} />
        </label>
      ) : null}
      {!isRegister ? <p className="auth-forgot"><Link href="/forgot-password">Quên mật khẩu?</Link></p> : null}
      <p aria-live="polite" className="form-status">{state.error}</p>
      <button className="button" disabled={pending || username.length < 3 || password.length < 6 || (isRegister && confirmation !== password)} type="submit">
        {pending ? "Đang xử lý" : isRegister ? "Tạo tài khoản" : "Đăng nhập"}
      </button>
      <p className="auth-switch">
        {isRegister ? "Đã có tài khoản?" : "Chưa có tài khoản?"} {" "}
        <Link href={`${isRegister ? "/login" : "/register"}?next=${encodeURIComponent(next)}`}>
          {isRegister ? "Đăng nhập" : "Tạo tài khoản"}
        </Link>
      </p>
    </form>
  );
}

function EmailVerificationForm({ email }: Readonly<{ email: string }>) {
  const [state, action, pending] = useActionState<AuthFormState, FormData>(verifyEmailAction, { verificationRequired: true, verificationEmail: email });
  if (state.verified) return <div aria-live="polite" className="reset-success"><p>Email đã được xác minh. Bạn có thể đăng nhập ngay.</p><Link className="button" href="/login">Đăng nhập</Link></div>;
  return <form action={action} className="auth-form" noValidate>
    <input name="email" type="hidden" value={email} />
    <p className="auth-description">Mã xác minh đã được gửi tới <strong>{email}</strong>.</p>
    <label>Mã xác minh<input aria-describedby={state.fields?.otp ? "verification-otp-error" : undefined} autoComplete="one-time-code" inputMode="numeric" maxLength={6} name="otp" pattern="[0-9]{6}" required /><FieldError id="verification-otp-error" message={state.fields?.otp} /></label>
    <p aria-live="polite" className="form-status">{state.error}</p>
    <button className="button" disabled={pending} type="submit">{pending ? "Đang xác minh" : "Xác minh email"}</button>
    <p className="auth-switch"><Link href="/login">Quay lại đăng nhập</Link></p>
  </form>;
}
