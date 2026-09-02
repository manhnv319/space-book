"use client";

import { useActionState, useEffect, useRef, useState, type KeyboardEvent } from "react";
import { useRouter } from "next/navigation";

import type { SupportState } from "@/app/actions/support";

const INITIAL: SupportState = { status: "idle", message: "" };
const MAX_ATTACHMENTS = 3;
type PendingAttachment = { file: File; url: string };

interface ChatComposerProps {
  action: (state: SupportState, data: FormData) => Promise<SupportState>;
  conversationId?: number;
  idPrefix?: string;
  onSent?: () => void;
  placeholder: string;
  pollSeconds?: number;
}

export function ChatComposer({ action, conversationId, idPrefix = "support", onSent, placeholder, pollSeconds = 6 }: ChatComposerProps) {
  const [state, formAction, pending] = useActionState(action, INITIAL);
  const [pendingAttachments, setPendingAttachments] = useState<PendingAttachment[]>([]);
  const formRef = useRef<HTMLFormElement>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const previewUrls = useRef<string[]>([]);
  const wasPending = useRef(false);
  const router = useRouter();

  function replaceAttachments(files: File[]) {
    const input = fileInputRef.current;
    if (!input) return;
    const transfer = new DataTransfer();
    const limited = files.slice(0, MAX_ATTACHMENTS);
    for (const file of limited) transfer.items.add(file);
    input.files = transfer.files;
    previewUrls.current.forEach((url) => URL.revokeObjectURL(url));
    const next = limited.map((file) => ({ file, url: URL.createObjectURL(file) }));
    previewUrls.current = next.map((item) => item.url);
    setPendingAttachments(next);
  }

  useEffect(() => {
    if (pollSeconds <= 0) return;
    const timer = window.setInterval(() => router.refresh(), pollSeconds * 1000);
    return () => window.clearInterval(timer);
  }, [pollSeconds, router]);

  useEffect(() => () => previewUrls.current.forEach((url) => URL.revokeObjectURL(url)), []);

  useEffect(() => {
    if (pending) wasPending.current = true;
    if (!pending && wasPending.current) {
      wasPending.current = false;
      if (state.status === "idle") {
        window.setTimeout(() => {
          formRef.current?.reset();
          replaceAttachments([]);
        }, 0);
        onSent?.();
      }
    }
  }, [onSent, pending, state.status]);

  function submitOnEnter(event: KeyboardEvent<HTMLTextAreaElement>) {
    if (event.key !== "Enter" || event.shiftKey || event.nativeEvent.isComposing) return;
    event.preventDefault();
    if (!pending) event.currentTarget.form?.requestSubmit();
  }

  function appendClipboardImages(event: React.ClipboardEvent<HTMLTextAreaElement>) {
    const pasted = Array.from(event.clipboardData.items)
      .filter((item) => item.kind === "file" && item.type.startsWith("image/"))
      .map((item) => item.getAsFile())
      .filter((file): file is File => file !== null);
    if (pasted.length === 0) return;
    replaceAttachments([...Array.from(fileInputRef.current?.files ?? []), ...pasted]);
  }

  function removeAttachment(index: number) {
    replaceAttachments(Array.from(fileInputRef.current?.files ?? []).filter((_, current) => current !== index));
  }

  const bodyId = `${idPrefix}-body`;
  const attachmentId = `${idPrefix}-attachments`;
  return <form ref={formRef} action={formAction} className="chat-composer">
    {conversationId != null && <input type="hidden" name="conversationId" value={conversationId} />}
    <label className="visually-hidden" htmlFor={bodyId}>Nội dung tin nhắn</label>
    {pendingAttachments.length > 0 && <div aria-label="Ảnh chuẩn bị gửi" className="chat-pending-attachments">
      {pendingAttachments.map((attachment, index) => <div className="chat-pending-attachment" key={`${attachment.file.name}-${index}`}><img alt={attachment.file.name} src={attachment.url} /><button aria-label={`Xóa ảnh ${index + 1}`} onClick={() => removeAttachment(index)} title="Xóa ảnh" type="button"><RemoveIcon /></button></div>)}
    </div>}
    <div className="chat-input-shell">
      <textarea id={bodyId} name="body" rows={2} maxLength={2000} onKeyDown={submitOnEnter} onPaste={appendClipboardImages} placeholder={placeholder} />
      <div className="chat-composer-actions">
        <label aria-label="Đính kèm ảnh" className="chat-attachment-picker" htmlFor={attachmentId} title="Đính kèm ảnh"><PaperclipIcon /><input accept="image/jpeg,image/png,image/webp" id={attachmentId} multiple name="attachments" onChange={(event) => replaceAttachments(Array.from(event.currentTarget.files ?? []))} ref={fileInputRef} type="file" /></label>
        {pendingAttachments.length > 0 && <span aria-label={`${pendingAttachments.length} ảnh đã chọn`} className="chat-attachment-count">{pendingAttachments.length}</span>}
        <button aria-label="Gửi tin nhắn" className="button chat-send-button" title="Gửi" type="submit" disabled={pending}><SendIcon /></button>
      </div>
    </div>
    {state.status === "error" && <p className="form-status" role="alert">{state.message}</p>}
  </form>;
}

function PaperclipIcon() {
  return <svg aria-hidden="true" viewBox="0 0 24 24"><path d="m8.5 12.5 5.9-5.9a3.2 3.2 0 0 1 4.5 4.5l-7.7 7.7a5 5 0 0 1-7.1-7.1l7.3-7.3" /></svg>;
}

function RemoveIcon() {
  return <svg aria-hidden="true" viewBox="0 0 24 24"><path d="m7 7 10 10M17 7 7 17" /></svg>;
}

function SendIcon() {
  return <svg aria-hidden="true" viewBox="0 0 24 24"><path d="m4 4 16 8-16 8 3-8-3-8Z" /><path d="M7 12h13" /></svg>;
}
