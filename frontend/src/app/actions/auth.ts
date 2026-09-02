"use server";

import { redirect } from "next/navigation";

import { mergeGuestCartOnLogin } from "@/app/actions/cart";
import { safeAuthDestination } from "@/lib/auth-navigation";
import { clearSessionTokens, persistSessionTokens, readSessionTokens } from "@/lib/bff/auth-cookies";
import { createAuthService } from "@/lib/bff/auth-service";
import { BackendError } from "@/lib/bff/backend-error";
import { backendRequest } from "@/lib/bff/backend-request";
import { parseBackendResponse } from "@/lib/bff/envelope";

export type AuthFormState = {
  error?: string;
  verificationRequired?: boolean;
  verificationEmail?: string;
  verified?: boolean;
  fields?: { username?: string; email?: string; password?: string; fullname?: string; otp?: string; confirmation?: string };
};

export type ResetFormState = AuthFormState & { success?: boolean };

function formValue(data: FormData, field: string): string {
  return String(data.get(field) ?? "").trim();
}

function validateCredentials(data: FormData, requireProfile: boolean): AuthFormState & { values?: Record<string, string> } {
  const username = formValue(data, "username");
  const email = formValue(data, "email").toLowerCase();
  const password = String(data.get("password") ?? "");
  const confirmation = String(data.get("confirmation") ?? "");
  const fullname = formValue(data, "fullname");
  const fields = {
    ...(username.length < 3 ? { username: "Tên đăng nhập cần tối thiểu 3 ký tự." } : {}),
    ...(requireProfile && !/^\S+@\S+\.\S+$/.test(email) ? { email: "Email không hợp lệ." } : {}),
    ...(requireProfile && fullname.length < 2 ? { fullname: "Vui lòng nhập họ và tên." } : {}),
    ...(password.length < 6 ? { password: "Mật khẩu cần tối thiểu 6 ký tự." } : {}),
    ...(requireProfile && password !== confirmation ? { confirmation: "Mật khẩu xác nhận không khớp." } : {}),
  };
  return Object.keys(fields).length > 0 ? { fields } : { values: { username, email, password, fullname, confirmation } };
}

function authError(error: unknown, fallback: string): string {
  if (error instanceof BackendError) {
    if (error.status === 409) return "Email này đã được sử dụng.";
    if (error.status === 401) return "Email hoặc mật khẩu không đúng.";
    if (error.status >= 400 && error.status < 500) return fallback;
  }
  return "Không thể kết nối máy chủ. Vui lòng thử lại.";
}

function authService() {
  return createAuthService({
    request: backendRequest,
    parse: parseBackendResponse,
    persistSession: persistSessionTokens,
  });
}

export async function loginAction(_: AuthFormState, data: FormData): Promise<AuthFormState> {
  const validation = validateCredentials(data, false);
  if (!validation.values) return validation;

  try {
    await authService().login(validation.values as { username: string; password: string });
  } catch (error) {
    await clearSessionTokens();
    return { error: authError(error, "Không thể đăng nhập với thông tin này.") };
  }
  try {
    // Merge lỗi không được chặn login — login là luồng chính, giỏ hàng là phụ.
    await mergeGuestCartOnLogin();
  } catch (error) {
    console.error("Failed to merge guest cart after login:", error);
  }
  redirect(safeAuthDestination(data.get("next")));
}

export async function registerAction(_: AuthFormState, data: FormData): Promise<AuthFormState> {
  const validation = validateCredentials(data, true);
  if (!validation.values) return validation;

  try {
    await authService().register(validation.values as { username: string; email: string; password: string; fullname: string });
  } catch (error) {
    await clearSessionTokens();
    return { error: authError(error, "Không thể tạo tài khoản với thông tin này.") };
  }
  return { verificationRequired: true, verificationEmail: validation.values.email };
}

export async function verifyEmailAction(_: AuthFormState, data: FormData): Promise<AuthFormState> {
  const email = formValue(data, "email").toLowerCase();
  const otp = String(data.get("otp") ?? "").trim();
  if (!/^\d{6}$/.test(otp)) return { fields: { otp: "Nhập mã xác minh gồm 6 chữ số." }, verificationRequired: true, verificationEmail: email };
  try {
    const response = await backendRequest("/api/v1/users/verify-email", {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({ email, otp }),
    });
    await parseBackendResponse<unknown>(response);
    return { verified: true };
  } catch (error) {
    return { error: error instanceof BackendError && error.status === 401 ? "Mã xác minh không đúng hoặc đã hết hạn." : "Không thể xác minh email lúc này.", verificationRequired: true, verificationEmail: email };
  }
}

export async function logoutAction(): Promise<never> {
  const session = await readSessionTokens();
  try {
    await backendRequest("/api/v1/auth/logout", {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({ refreshToken: session.refreshToken }),
      accessToken: session.accessToken,
    });
  } finally {
    await clearSessionTokens();
  }
  redirect("/");
}

export async function forgotPasswordAction(_: ResetFormState, data: FormData): Promise<ResetFormState> {
  const email = formValue(data, "email").toLowerCase();
  if (!/^\S+@\S+\.\S+$/.test(email)) return { fields: { email: "Email không hợp lệ." } };
  try {
    const response = await backendRequest("/api/v1/users/forgot-password", {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({ email }),
    });
    await parseBackendResponse<unknown>(response);
    return { success: true, verificationEmail: email };
  } catch {
    return { error: "Không thể gửi yêu cầu lúc này. Vui lòng thử lại." };
  }
}

export async function resetPasswordAction(_: ResetFormState, data: FormData): Promise<ResetFormState> {
  const email = formValue(data, "email").toLowerCase();
  const otp = String(data.get("otp") ?? "").trim();
  const password = String(data.get("password") ?? "");
  const confirmation = String(data.get("confirmation") ?? "");
  const fields = {
    ...(!/^\S+@\S+\.\S+$/.test(email) ? { email: "Email không hợp lệ." } : {}),
    ...(!/^\d{6}$/.test(otp) ? { otp: "Nhập mã xác minh gồm 6 chữ số." } : {}),
    ...(password.length < 6 ? { password: "Mật khẩu cần tối thiểu 6 ký tự." } : {}),
    ...(password !== confirmation ? { confirmation: "Mật khẩu xác nhận không khớp." } : {}),
  };
  if (Object.keys(fields).length > 0) return { fields };
  try {
    const response = await backendRequest("/api/v1/users/reset-password", {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({ email, otp, newPassword: password }),
    });
    await parseBackendResponse<unknown>(response);
    return { success: true };
  } catch (error) {
    if (error instanceof BackendError && error.status === 401) {
      return { error: "Mã xác minh không đúng hoặc đã hết hạn." };
    }
    return { error: "Không thể đặt lại mật khẩu lúc này. Vui lòng thử lại." };
  }
}
