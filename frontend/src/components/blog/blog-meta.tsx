const DATE_FORMATTER = new Intl.DateTimeFormat("vi-VN", { dateStyle: "long" });
const WORDS_PER_MINUTE = 200;

function estimateReadingMinutes(content: string): number {
  const wordCount = content.trim().split(/\s+/).filter(Boolean).length;
  return Math.max(1, Math.round(wordCount / WORDS_PER_MINUTE));
}

interface BlogMetaProps {
  publishedAt: string | null;
  /** Full post content — when provided, renders an estimated reading time. */
  content?: string;
  className?: string;
}

/** Server component: publish date + estimated reading time. Only surfaces
 * `publishedAt` (never `status`/`createdAt` — those are internal). */
export function BlogMeta({ publishedAt, content, className }: BlogMetaProps) {
  const dateLabel = publishedAt ? DATE_FORMATTER.format(new Date(publishedAt)) : null;
  const minutes = content ? estimateReadingMinutes(content) : null;

  if (!dateLabel && !minutes) return null;

  return (
    <p className={["blog-meta", className].filter(Boolean).join(" ")}>
      {dateLabel && <span>{dateLabel}</span>}
      {dateLabel && minutes && <span aria-hidden="true"> · </span>}
      {minutes && <span>{minutes} phút đọc</span>}
    </p>
  );
}
