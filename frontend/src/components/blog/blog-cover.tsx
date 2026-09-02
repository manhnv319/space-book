interface BlogCoverProps {
  src: string | null | undefined;
  alt: string;
  variant: "card" | "detail";
  className?: string;
}

const SAFE_SRC_PREFIXES = ["http://", "https://", "/"];

/** `coverImageUrl` is admin-entered free text (D14, no upload yet) — validate
 * the scheme before ever putting it in `src`, same pattern as `<BookCover>`
 * (Phase 05). Blocks `javascript:`/`data:` URIs from being used as an image
 * source. */
function isSafeImageSrc(src: string | null | undefined): src is string {
  return !!src && SAFE_SRC_PREFIXES.some((prefix) => src.startsWith(prefix));
}

/**
 * Server component: blog post cover image, or a shared placeholder when
 * there is no (or an unsafe) image URL. Not the same component as
 * `<BookCover>` — different placeholder copy/aspect for blog cards.
 */
export function BlogCover({ src, alt, variant, className }: BlogCoverProps) {
  if (isSafeImageSrc(src)) {
    return (
      <img
        src={src}
        alt={alt}
        loading="lazy"
        className={className ?? (variant === "card" ? "blog-card-image" : "blog-detail-cover-image")}
      />
    );
  }

  const placeholderClass =
    variant === "detail" ? "blog-cover-placeholder blog-cover-placeholder--detail" : "blog-cover-placeholder";

  return (
    <div className={placeholderClass}>
      <span>Bài viết</span>
    </div>
  );
}
