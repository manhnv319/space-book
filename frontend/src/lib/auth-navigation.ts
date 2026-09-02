const DEFAULT_AUTH_DESTINATION = "/account";

export function safeAuthDestination(value: FormDataEntryValue | string | null | undefined): string {
  if (typeof value !== "string" || !value.startsWith("/") || value.startsWith("//")) {
    return DEFAULT_AUTH_DESTINATION;
  }

  try {
    const destination = new URL(value, "https://velstrongbook.local");
    return destination.origin === "https://velstrongbook.local"
      ? `${destination.pathname}${destination.search}${destination.hash}`
      : DEFAULT_AUTH_DESTINATION;
  } catch {
    return DEFAULT_AUTH_DESTINATION;
  }
}
