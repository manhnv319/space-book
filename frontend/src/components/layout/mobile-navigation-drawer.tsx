"use client";

import { useEffect, useRef, useState } from "react";
import type { ReactNode } from "react";

import { MenuIcon } from "@/components/site-icons";

export function MobileNavigationDrawer({ children }: { children: ReactNode }) {
  const [open, setOpen] = useState(false);
  const triggerRef = useRef<HTMLButtonElement>(null);
  const closeRef = useRef<HTMLButtonElement>(null);
  const panelRef = useRef<HTMLElement>(null);

  useEffect(() => {
    if (!open) return;
    const trigger = triggerRef.current;
    closeRef.current?.focus();
    const onKeyDown = (event: KeyboardEvent) => {
      if (event.key === "Escape") { setOpen(false); return; }
      if (event.key !== "Tab") return;
      const focusable = panelRef.current?.querySelectorAll<HTMLElement>('button:not(:disabled), a[href], input:not(:disabled)');
      if (!focusable?.length) return;
      const first = focusable[0];
      const last = focusable[focusable.length - 1];
      if (event.shiftKey && document.activeElement === first) { event.preventDefault(); last.focus(); }
      if (!event.shiftKey && document.activeElement === last) { event.preventDefault(); first.focus(); }
    };
    document.body.style.overflow = "hidden";
    window.addEventListener("keydown", onKeyDown);
    return () => { document.body.style.overflow = ""; window.removeEventListener("keydown", onKeyDown); trigger?.focus(); };
  }, [open]);

  return <div className="mobile-drawer"><button aria-expanded={open} aria-label="Mở menu" className="mobile-menu-button" onClick={() => setOpen(true)} ref={triggerRef} type="button"><MenuIcon /></button>{open ? <div className="mobile-drawer-layer" onMouseDown={() => setOpen(false)}><nav aria-label="Menu mobile" aria-modal="true" className="mobile-drawer-panel" onMouseDown={(event) => event.stopPropagation()} ref={panelRef} role="dialog"><button aria-label="Đóng menu" className="mobile-drawer-close" onClick={() => setOpen(false)} ref={closeRef} type="button">×</button>{children}</nav></div> : null}</div>;
}
