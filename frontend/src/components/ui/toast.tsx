"use client";

import { useState } from "react";

export type ToastTone = "info" | "success" | "error";

interface ToastProps {
  message: string;
  tone?: ToastTone;
  /** Called once the toast has finished its auto-hide animation. */
  onDismiss?: () => void;
}

const TONE_CLASS: Record<ToastTone, string> = {
  info: "",
  success: "toast-success",
  error: "toast-error",
};

/**
 * Client island: local toast next to an action (e.g. add-to-cart button).
 * Auto-hides via a CSS animation (`toast-autohide`, see motion.css) instead
 * of a JS timer; `onAnimationEnd` only reports completion back to the caller.
 * No global provider/context — each call site owns its own toast state.
 */
export function Toast({ message, tone = "info", onDismiss }: ToastProps) {
  const [dismissed, setDismissed] = useState(false);

  if (dismissed || !message) {
    return null;
  }

  const classes = ["toast", TONE_CLASS[tone]].filter(Boolean).join(" ");

  return (
    <div
      role="status"
      aria-live="polite"
      className={classes}
      onAnimationEnd={(event) => {
        if (event.animationName === "toast-autohide" || event.animationName === "toast-autohide-reduced") {
          setDismissed(true);
          onDismiss?.();
        }
      }}
    >
      <span className="toast-message">{message}</span>
    </div>
  );
}
