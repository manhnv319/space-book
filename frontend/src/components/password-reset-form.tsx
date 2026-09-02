"use client";

import Link from "next/link";
import { useActionState, useState } from "react";

import { forgotPasswordAction, resetPasswordAction, type ResetFormState } from "@/app/actions/auth";

function FieldError({ id, message }: Readonly<{ id: string; message?: string }>) {
  return message ? <p className="field-error" id={id} role="alert">{message}</p> : null;
}

export function ForgotPasswordForm({ titleId }: Readonly<{ titleId: string }>) {
  const [resetKey, setResetKey] = useState(0);

  return <ForgotPasswordSteps
    key={resetKey}
    titleId={titleId}
    onUseAnotherEmail={() => setResetKey((current) => current + 1)}
  />;
}

function ForgotPasswordSteps({
  onUseAnotherEmail,
  titleId,
}: Readonly<{ onUseAnotherEmail: () => void; titleId: string }>) {
  const [state, action, pending] = useActionState<ResetFormState, FormData>(forgotPasswordAction, {});
  const isResetStep = state.success;

  return <>
    <h1 id={titleId}>{isResetStep ? "Đặt lại mật khẩu" : "Quên mật khẩu?"}</h1>
    <p className="auth-description">{isResetStep ? "Dùng mã 6 chữ số trong email của bạn." : "Nhập email để nhận mã xác minh."}</p>
    {isResetStep ? <ResetPasswordForm email={state.verificationEmail} onUseAnotherEmail={onUseAnotherEmail} /> : <form action={action} className="auth-form" noValidate>
      <label>Email<input aria-describedby={state.fields?.email ? "email-error" : undefined} autoComplete="email" inputMode="email" name="email" required type="email" /><FieldError id="email-error" message={state.fields?.email} /></label>
      <p aria-live="polite" className="form-status">{state.error}</p>
      <button className="button" disabled={pending} type="submit">{pending ? "Đang gửi" : "Gửi mã xác minh"}</button>
      <p className="auth-switch"><Link href="/login">Quay lại đăng nhập</Link></p>
    </form>}
  </>;
}

export function ResetPasswordForm({
  email,
  onUseAnotherEmail,
}: Readonly<{ email?: string; onUseAnotherEmail?: () => void }>) {
  const [state, action, pending] = useActionState<ResetFormState, FormData>(resetPasswordAction, {});
  if (state.success) return <div aria-live="polite" className="reset-success"><p>Mật khẩu đã được đặt lại. Bạn có thể đăng nhập bằng mật khẩu mới.</p><Link className="button" href="/login">Đăng nhập</Link></div>;
  return <form action={action} className="auth-form" noValidate>
    {email ? <>
      <input name="email" type="hidden" defaultValue={email} />
      <p aria-live="polite" className="form-status">Mã xác minh đã được gửi tới {email}.</p>
      <p className="auth-switch"><button className="link-button" onClick={onUseAnotherEmail} type="button">Dùng email khác</button></p>
      <FieldError id="email-error" message={state.fields?.email} />
    </> : <label>Email<input aria-describedby={state.fields?.email ? "email-error" : undefined} autoComplete="email" inputMode="email" name="email" required type="email" /><FieldError id="email-error" message={state.fields?.email} /></label>}
    <label>Mã xác minh<input aria-describedby={state.fields?.otp ? "otp-error" : undefined} autoComplete="one-time-code" inputMode="numeric" maxLength={6} name="otp" pattern="[0-9]{6}" required /><FieldError id="otp-error" message={state.fields?.otp} /></label>
    <label>Mật khẩu mới<input aria-describedby={state.fields?.password ? "password-error" : undefined} autoComplete="new-password" minLength={6} name="password" required type="password" /><FieldError id="password-error" message={state.fields?.password} /></label>
    <label>Xác nhận mật khẩu<input aria-describedby={state.fields?.confirmation ? "confirmation-error" : undefined} autoComplete="new-password" name="confirmation" required type="password" /><FieldError id="confirmation-error" message={state.fields?.confirmation} /></label>
    <p aria-live="polite" className="form-status">{state.error}</p>
    <button className="button" disabled={pending} type="submit">{pending ? "Đang xử lý" : "Đặt lại mật khẩu"}</button>
    <p className="auth-switch"><Link href="/login">Quay lại đăng nhập</Link></p>
  </form>;
}
