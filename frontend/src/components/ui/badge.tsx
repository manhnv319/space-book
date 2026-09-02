import type { ReactNode } from "react";

interface BadgeProps {
  children: ReactNode;
  tone?: "default" | "muted";
  /** Absolute-positioned overlay variant, e.g. category badge on a book cover. */
  overlay?: boolean;
  className?: string;
}

export function Badge({ children, tone = "default", overlay = false, className }: BadgeProps) {
  const classes = ["badge", tone === "muted" && "badge-muted", overlay && "badge-cover", className]
    .filter(Boolean)
    .join(" ");

  return <span className={classes}>{children}</span>;
}
