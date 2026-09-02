"use client";

import { Fragment, useEffect, useRef, useState } from "react";

import type { SupportMessage, SupportSender } from "@/lib/types/support";

const TIME = new Intl.DateTimeFormat("vi-VN", { hour: "2-digit", minute: "2-digit" });
const DATE = new Intl.DateTimeFormat("vi-VN", { day: "numeric", month: "long", year: "numeric" });
const DATE_WITHOUT_YEAR = new Intl.DateTimeFormat("vi-VN", { day: "numeric", month: "long" });

function formatMoment(value: string): string {
  const parsed = new Date(value);
  return Number.isNaN(parsed.getTime()) ? "" : TIME.format(parsed);
}

function dayKey(value: string): string {
  const parsed = new Date(value);
  return Number.isNaN(parsed.getTime())
    ? ""
    : `${parsed.getFullYear()}-${parsed.getMonth()}-${parsed.getDate()}`;
}

function dateLabel(value: string): string {
  const parsed = new Date(value);
  if (Number.isNaN(parsed.getTime())) return "";

  const today = new Date();
  const startOfDay = (date: Date) => new Date(date.getFullYear(), date.getMonth(), date.getDate()).getTime();
  const delta = Math.round((startOfDay(today) - startOfDay(parsed)) / 86_400_000);

  if (delta === 0) return "Hôm nay";
  if (delta === 1) return "Hôm qua";
  return parsed.getFullYear() === today.getFullYear()
    ? DATE_WITHOUT_YEAR.format(parsed)
    : DATE.format(parsed);
}

function initials(value: string): string {
  const words = value.trim().split(/\s+/).filter(Boolean);
  return (words.length > 1 ? `${words[0][0]}${words[words.length - 1][0]}` : words[0]?.slice(0, 2) || "KH").toUpperCase();
}

function mediaUrl(value: string): string {
  if (value.startsWith("/media/")) return value;
  try {
    const parsed = new URL(value);
    return parsed.pathname.startsWith("/media/") ? `${parsed.pathname}${parsed.search}` : "";
  } catch {
    return "";
  }
}

function scrollToLatest(node: HTMLElement, behavior: ScrollBehavior) {
  let container = node.parentElement;
  while (container && container !== document.body) {
    if (container.scrollHeight > container.clientHeight) {
      container.scrollTo({ top: container.scrollHeight, behavior });
      return;
    }
    container = container.parentElement;
  }
  window.scrollTo({ top: document.documentElement.scrollHeight, behavior });
}

function ImageAttachment({ alt, url }: Readonly<{ alt: string; url: string }>) {
  const [failed, setFailed] = useState(!url);
  if (failed) return <span className="chat-attachment-fallback">Không tải được ảnh</span>;
  return <img alt={alt} decoding="async" loading="lazy" onError={() => setFailed(true)} src={url} />;
}

/** The viewer's own messages always sit on the right and new messages stay visible. */
export function ChatThread({ messages, emptyHint, focusMessageId, participantName = "Khách hàng", showAvatars = false, viewer = "CUSTOMER" }: { messages: SupportMessage[]; emptyHint: string; focusMessageId?: string; participantName?: string; showAvatars?: boolean; viewer?: SupportSender }) {
  const endRef = useRef<HTMLLIElement>(null);
  const mounted = useRef(false);
  const [preview, setPreview] = useState<{ alt: string; url: string } | null>(null);
  const lastMessageId = messages[messages.length - 1]?.id;

  useEffect(() => {
    if (!lastMessageId) return;
    if (endRef.current) scrollToLatest(endRef.current, mounted.current ? "smooth" : "auto");
    mounted.current = true;
  }, [lastMessageId]);

  useEffect(() => {
    if (!focusMessageId) return;
    const timer = window.requestAnimationFrame(() => {
      const message = document.getElementById(`chat-message-${focusMessageId}`);
      const container = message?.closest<HTMLElement>("[data-chat-scroll-container]");
      if (!message || !container) return;
      container.scrollTo({ top: Math.max(0, message.offsetTop - container.offsetTop - container.clientHeight / 2 + message.clientHeight / 2), behavior: "smooth" });
    });
    return () => window.cancelAnimationFrame(timer);
  }, [focusMessageId, lastMessageId]);

  useEffect(() => {
    if (!preview) return;
    const closeOnEscape = (event: KeyboardEvent) => { if (event.key === "Escape") setPreview(null); };
    window.addEventListener("keydown", closeOnEscape);
    return () => window.removeEventListener("keydown", closeOnEscape);
  }, [preview]);

  if (messages.length === 0) return <p className="chat-empty">{emptyHint}</p>;
  return <>
    <ol className="chat-thread">
      {messages.map((message, index) => {
        const own = message.sender === viewer;
        const currentDay = dayKey(message.sentAt);
        const previousMessage = messages[index - 1];
        const showDate = currentDay !== dayKey(previousMessage?.sentAt ?? "");

        return <Fragment key={message.id}>
          {showDate && <li className="chat-date-separator"><span>{dateLabel(message.sentAt)}</span></li>}
          <li className={`chat-message-row${own ? " is-own" : ""}`} id={`chat-message-${message.id}`} ref={index === messages.length - 1 ? endRef : undefined}>
            {!own && showAvatars && <span aria-label={participantName} className="chat-avatar" title={participantName}>{initials(participantName)}</span>}
            <div className={own ? "chat-bubble is-own" : "chat-bubble"}>
              {message.body && <p className="chat-body">{message.body}</p>}
              {message.attachments.length > 0 && <div className="chat-attachments">
                {message.attachments.map((attachment) => {
                  const url = mediaUrl(attachment.imageUrl);
                  return <button aria-label={`Xem ảnh ${attachment.originalName}`} className="chat-attachment-open" key={attachment.id} onClick={() => url && setPreview({ alt: attachment.originalName, url })} type="button"><ImageAttachment alt={attachment.originalName} url={url} /></button>;
                })}
              </div>}
              <time dateTime={message.sentAt}>{formatMoment(message.sentAt)}</time>
            </div>
          </li>
        </Fragment>;
      })}
    </ol>
    {preview && <div aria-label="Xem ảnh đính kèm" aria-modal="true" className="chat-image-lightbox" onClick={() => setPreview(null)} role="dialog">
      <button aria-label="Đóng ảnh" className="chat-image-lightbox-close" onClick={() => setPreview(null)} type="button"><RemoveIcon /></button>
      <img alt={preview.alt} onClick={(event) => event.stopPropagation()} src={preview.url} />
    </div>}
  </>;
}

function RemoveIcon() {
  return <svg aria-hidden="true" viewBox="0 0 24 24"><path d="m7 7 10 10M17 7 7 17" /></svg>;
}
