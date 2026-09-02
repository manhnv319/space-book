"use client";

import { useState } from "react";

interface BookCoverProps {
  src: string | null | undefined;
  alt: string;
  variant: "card" | "detail";
  className?: string;
  eager?: boolean;
}

const SAFE_SRC_PREFIXES = ["http://", "https://", "/"];

function isSafeImageSrc(src: string | null | undefined): src is string {
  return !!src && SAFE_SRC_PREFIXES.some((prefix) => src.startsWith(prefix));
}

export function BookCover({ src, alt, variant, className, eager = false }: BookCoverProps) {
  const [failed, setFailed] = useState(false);
  const imageClass = className ?? (variant === "card" ? "book-card-image" : "detail-cover-image");

  if (isSafeImageSrc(src) && !failed) {
    return (
      <img
        alt={alt}
        className={imageClass}
        fetchPriority={eager ? "high" : "auto"}
        loading={eager ? "eager" : "lazy"}
        onError={() => setFailed(true)}
        src={src}
      />
    );
  }

  const placeholderClass = variant === "detail" ? "book-cover-placeholder book-cover-placeholder--detail" : "book-cover-placeholder";
  return <div aria-label={alt} className={placeholderClass}><span>Bìa sách<br />đang cập nhật</span></div>;
}
