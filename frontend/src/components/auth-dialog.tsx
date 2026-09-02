"use client";

import { useRouter } from "next/navigation";
import type { KeyboardEvent, MouseEvent, ReactNode } from "react";
import { useEffect, useRef } from "react";

type AuthDialogProps = {
  children: ReactNode;
  titleId: string;
};

function focusableElements(dialog: HTMLElement): HTMLElement[] {
  return [...dialog.querySelectorAll<HTMLElement>("a[href], button:not([disabled]), input:not([disabled]):not([type=hidden])")]
    .filter((element) => !element.hasAttribute("hidden") && element.getClientRects().length > 0);
}

export function AuthDialog({ children, titleId }: Readonly<AuthDialogProps>) {
  const dialogRef = useRef<HTMLElement>(null);
  const router = useRouter();

  useEffect(() => {
    dialogRef.current?.focus();
  }, []);

  function closeDialog(): void {
    router.push("/");
  }

  function handleLayerClick(event: MouseEvent<HTMLDivElement>): void {
    if (event.target !== event.currentTarget) return;
    closeDialog();
  }

  function handleKeyDown(event: KeyboardEvent<HTMLElement>): void {
    if (event.key === "Escape") {
      closeDialog();
      return;
    }
    if (event.key !== "Tab" || !dialogRef.current) return;

    const elements = focusableElements(dialogRef.current);
    const first = elements[0];
    const last = elements.at(-1);
    if (!first || !last) return;

    if (event.shiftKey && document.activeElement === first) {
      event.preventDefault();
      last.focus();
    } else if (!event.shiftKey && document.activeElement === last) {
      event.preventDefault();
      first.focus();
    }
  }

  return (
    <div className="auth-layer" onClick={handleLayerClick}>
      <div aria-hidden="true" className="auth-preview" data-testid="auth-backdrop" inert>
        <div className="auth-preview-content">
          <p>VELSTRONGBОOK</p>
          <strong>Nơi những trang sách tìm được người đọc.</strong>
          <span>Khám phá những cuốn sách phù hợp với bạn.</span>
        </div>
      </div>
      <section
        aria-labelledby={titleId}
        aria-modal="true"
        className="auth-card"
        onKeyDown={handleKeyDown}
        ref={dialogRef}
        role="dialog"
        tabIndex={-1}
      >
        {children}
      </section>
    </div>
  );
}
