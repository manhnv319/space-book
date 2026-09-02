"use client";

import Link from "next/link";
import { useCallback, useEffect, useState } from "react";

import type { SupportState } from "@/app/actions/support";
import { ChatComposer } from "@/components/support/chat-composer";
import { ChatThread } from "@/components/support/chat-thread";
import type { SupportConversation } from "@/lib/types/support";

type SupportAction = (state: SupportState, data: FormData) => Promise<SupportState>;

export function SupportWidget({ action, signedIn }: Readonly<{ action?: SupportAction; signedIn: boolean }>) {
  const [open, setOpen] = useState(false);
  const [conversation, setConversation] = useState<SupportConversation | null>(null);
  const [error, setError] = useState("");
  const [focusMessageId, setFocusMessageId] = useState<string>();

  const loadConversation = useCallback(async () => {
    try {
      const response = await fetch("/support-widget/conversation", { cache: "no-store" });
      if (!response.ok) throw new Error("Unable to load support conversation.");
      setConversation(await response.json() as SupportConversation);
      setError("");
    } catch {
      setError("Không tải được hội thoại. Vui lòng thử lại.");
    }
  }, []);

  useEffect(() => {
    if (!open || !signedIn) return;
    const initialTimer = window.setTimeout(() => void loadConversation(), 0);
    const timer = window.setInterval(() => void loadConversation(), 6000);
    return () => { window.clearTimeout(initialTimer); window.clearInterval(timer); };
  }, [loadConversation, open, signedIn]);

  useEffect(() => {
    if (!open) return;
    const closeOnEscape = (event: KeyboardEvent) => { if (event.key === "Escape") setOpen(false); };
    window.addEventListener("keydown", closeOnEscape);
    return () => window.removeEventListener("keydown", closeOnEscape);
  }, [open]);

  useEffect(() => {
    const openWidget = () => {
      const messageId = window.sessionStorage.getItem("velstrong-support-message");
      if (messageId) { setFocusMessageId(messageId); window.sessionStorage.removeItem("velstrong-support-message"); }
      setOpen(true);
    };
    const openFromHash = () => {
      if (window.location.hash !== "#support") return;
      openWidget();
      const url = new URL(window.location.href);
      url.hash = "";
      window.history.replaceState({}, "", url.toString());
    };
    if (window.sessionStorage.getItem("velstrong-open-support") === "1") {
      window.sessionStorage.removeItem("velstrong-open-support");
      openWidget();
    }
    openFromHash();
    window.addEventListener("hashchange", openFromHash);
    window.addEventListener("velstrong-open-support", openWidget);
    return () => {
      window.removeEventListener("hashchange", openFromHash);
      window.removeEventListener("velstrong-open-support", openWidget);
    };
  }, []);

  if (!signedIn) return <Link aria-label="Đăng nhập để nhắn với nhà sách" className="support-widget-trigger" href="/login?next=%2F%23support" title="Nhắn với nhà sách"><SupportIcon /></Link>;

  return <div className="support-widget">
    {open && <section aria-label="Nhắn với nhà sách" className="support-widget-panel">
      <header className="support-widget-header"><div><p>Hỗ trợ Sách Nhà</p><strong>Nhắn với nhà sách</strong></div><button aria-label="Đóng khung chat" onClick={() => setOpen(false)} title="Đóng" type="button"><CloseIcon /></button></header>
      <div className="support-widget-body" data-chat-scroll-container>
        {error ? <p className="form-status" role="alert">{error}</p>
          : conversation ? <ChatThread emptyHint="Bạn cứ nhắn, nhà sách sẽ phản hồi trong giờ làm việc." focusMessageId={focusMessageId} messages={conversation.messages} />
            : <p className="chat-empty">Đang tải hội thoại…</p>}
      </div>
      <div className="support-widget-composer">
        <ChatComposer action={action!} idPrefix="support-widget" onSent={() => void loadConversation()} placeholder="Nhập tin nhắn…" pollSeconds={0} />
      </div>
    </section>}
    <button aria-expanded={open} aria-label={open ? "Đóng khung chat" : "Nhắn với nhà sách"} className="support-widget-trigger" onClick={() => setOpen((current) => !current)} title={open ? "Đóng chat" : "Nhắn với nhà sách"} type="button"><SupportIcon /></button>
  </div>;
}

function SupportIcon() {
  return <svg aria-hidden="true" viewBox="0 0 24 24"><path d="M5 18.5V8.8A3.8 3.8 0 0 1 8.8 5h6.4A3.8 3.8 0 0 1 19 8.8v5.4a3.8 3.8 0 0 1-3.8 3.8H9l-4 2.5Z" /><path d="M9 11.5h6M9 14.5h4" /></svg>;
}

function CloseIcon() {
  return <svg aria-hidden="true" viewBox="0 0 24 24"><path d="m7 7 10 10M17 7 7 17" /></svg>;
}
