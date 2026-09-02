"use client";

import Link from "next/link";
import { useCallback, useEffect, useRef, useState } from "react";

import { BellIcon } from "@/components/site-icons";
import type { NotificationPage, UserNotification } from "@/lib/types/notification";

const TIME = new Intl.DateTimeFormat("vi-VN", { hour: "2-digit", minute: "2-digit" });

export function NotificationBell() {
  const [open, setOpen] = useState(false);
  const [items, setItems] = useState<UserNotification[]>([]);
  const [unread, setUnread] = useState(0);
  const panelRef = useRef<HTMLDivElement>(null);

  const load = useCallback(async () => {
    const [pageResponse, countResponse] = await Promise.all([fetch("/notification-center?size=50"), fetch("/notification-center/unread-count")]);
    if (pageResponse.ok) setItems((await pageResponse.json() as NotificationPage).content);
    if (countResponse.ok) setUnread(await countResponse.json() as number);
  }, []);

  useEffect(() => {
    const initialLoad = window.setTimeout(() => void load(), 0);
    const stream = new EventSource("/notification-center/stream");
    stream.addEventListener("notification", (event) => {
      const payload = JSON.parse((event as MessageEvent<string>).data) as { unreadCount: number; notification: UserNotification | null };
      setUnread(payload.unreadCount);
      if (payload.notification) setItems((current) => [payload.notification!, ...current.filter((item) => item.id !== payload.notification!.id)].slice(0, 50));
    });
    return () => { window.clearTimeout(initialLoad); stream.close(); };
  }, [load]);

  useEffect(() => {
    const closeOnOutsidePointer = (event: PointerEvent) => {
      if (panelRef.current && !panelRef.current.contains(event.target as Node)) setOpen(false);
    };
    document.addEventListener("pointerdown", closeOnOutsidePointer);
    return () => document.removeEventListener("pointerdown", closeOnOutsidePointer);
  }, []);

  async function markRead(item: UserNotification) {
    if (!item.readAt) await fetch(`/notification-center/${item.id}/read`, { method: "PATCH" });
    setItems((current) => current.map((value) => value.id === item.id ? { ...value, readAt: value.readAt ?? new Date().toISOString() } : value));
    setUnread((current) => Math.max(0, current - (item.readAt ? 0 : 1)));
  }

  async function markAllRead() {
    await fetch("/notification-center/read-all", { method: "PATCH" });
    setItems((current) => current.map((item) => ({ ...item, readAt: item.readAt ?? new Date().toISOString() })));
    setUnread(0);
  }

  async function enablePush() {
    if (!("serviceWorker" in navigator) || !("PushManager" in window) || !("Notification" in window)) return;
    const permission = await Notification.requestPermission();
    if (permission !== "granted") return;
    const keyResponse = await fetch("/notification-center/push/public-key");
    const publicKey = keyResponse.ok ? await keyResponse.json() as string : "";
    if (!publicKey) return;
    const registration = await navigator.serviceWorker.register("/notification-service-worker.js");
    const subscription = await registration.pushManager.subscribe({ userVisibleOnly: true, applicationServerKey: base64UrlToUint8Array(publicKey) });
    await fetch("/notification-center/push/subscriptions", { method: "POST", headers: { "content-type": "application/json" }, body: JSON.stringify(subscription) });
  }

  return <div className="notification-bell" ref={panelRef}>
    <button aria-expanded={open} aria-label="Thông báo" className="header-action" onClick={() => { setOpen((value) => !value); if (!open) void load(); }} title="Thông báo" type="button">
      <BellIcon />{unread > 0 && <span className="notification-badge">{unread > 99 ? "99+" : unread}</span>}
    </button>
    {open && <section aria-label="Thông báo" className="notification-panel">
      <header><strong>Thông báo</strong><span>{unread > 0 && <button onClick={() => void markAllRead()} type="button">Đánh dấu đã đọc</button>}<button onClick={() => void enablePush()} type="button">Bật thông báo</button></span></header>
      {items.length === 0 ? <p>Chưa có thông báo mới.</p> : <ol>{items.map((item) => <li className={item.readAt ? "" : "is-unread"} key={item.id}>
        <Link href={item.targetPath} onClick={(event) => {
          const opensCustomerChat = item.type === "CHAT" && !item.targetPath.startsWith("/admin/");
          if (opensCustomerChat) {
            event.preventDefault();
            window.sessionStorage.setItem("velstrong-open-support", "1");
            const messageId = new URL(item.targetPath, window.location.origin).searchParams.get("supportMessage");
            if (messageId) window.sessionStorage.setItem("velstrong-support-message", messageId);
            window.dispatchEvent(new Event("velstrong-open-support"));
          }
          void markRead(item);
        }}><strong>{item.title}</strong><span>{item.body}</span><time>{TIME.format(new Date(item.createdAt))}</time></Link>
      </li>)}</ol>}
    </section>}
  </div>;
}

function base64UrlToUint8Array(value: string): ArrayBuffer {
  const padded = value.padEnd(value.length + (4 - value.length % 4) % 4, "=").replace(/-/g, "+").replace(/_/g, "/");
  return Uint8Array.from(atob(padded), (character) => character.charCodeAt(0)).buffer as ArrayBuffer;
}
