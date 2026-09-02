export const ACCESS_TOKEN_MAX_AGE_SECONDS = 60 * 60;
export const REFRESH_TOKEN_MAX_AGE_SECONDS = 7 * 24 * 60 * 60;
export const GUEST_CART_MAX_AGE_SECONDS = 30 * 24 * 60 * 60;

export const authCookieOptions = {
  httpOnly: true,
  path: "/",
  sameSite: "lax" as const,
  secure: process.env.NODE_ENV === "production",
};

/** Tái dùng option builder của cookie phiên; chỉ khác maxAge. */
export const guestCartCookieOptions = {
  ...authCookieOptions,
  maxAge: GUEST_CART_MAX_AGE_SECONDS,
};
