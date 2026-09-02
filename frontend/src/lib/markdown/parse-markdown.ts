/**
 * Minimal, zero-dependency Markdown parser for blog post content.
 *
 * SECURITY INVARIANT: this module never produces an HTML string, only a
 * plain data tree (`MarkdownNode[]`). Rendering that tree into JSX (see
 * `components/blog/markdown-content.tsx`) relies on React's automatic text
 * escaping — there is no `dangerouslySetInnerHTML` anywhere in this
 * pipeline, so raw `<script>`/`<img onerror>` markup in admin-authored
 * content can never execute. Do not "optimize" this by generating an HTML
 * string, ever.
 *
 * Supported subset (YAGNI — matches phase-09 spec, not general CommonMark):
 * `##`/`###` headings, paragraphs, `**bold**`, `*italic*`, `` `code` ``,
 * `[text](url)` links, `-`/`1.` lists, `> ` quotes, `---` rules.
 */

export type Inline =
  | { kind: "text"; value: string }
  | { kind: "bold"; value: string }
  | { kind: "italic"; value: string }
  | { kind: "code"; value: string }
  | { kind: "link"; value: string; href: string };

export type MarkdownNode =
  | { type: "heading"; level: 2 | 3; children: Inline[] }
  | { type: "paragraph"; children: Inline[] }
  | { type: "list"; ordered: boolean; items: Inline[][] }
  | { type: "quote"; children: Inline[] }
  | { type: "hr" };

const MAX_BLOCK_LENGTH = 5000;
const MAX_BLOCKS = 200;

/** Only http(s) and root-relative links render as `<a>`; everything else
 * (including `javascript:`, `data:`, `vbscript:`) falls back to plain text. */
const SAFE_HREF_RE = /^(https?:\/\/|\/)/;

export function sanitizeHref(href: string): string | null {
  const trimmed = href.trim();
  return SAFE_HREF_RE.test(trimmed) ? trimmed : null;
}

// Matches, in priority order: `code`, **bold**, *italic*, [text](url).
// Simple character classes, no nested quantifiers — safe from ReDoS.
const INLINE_RE = /`([^`]+)`|\*\*([^*]+)\*\*|\*([^*]+)\*|\[([^\]]+)\]\(([^)]+)\)/g;

export function parseInline(text: string): Inline[] {
  const nodes: Inline[] = [];
  let lastIndex = 0;
  INLINE_RE.lastIndex = 0;

  let match: RegExpExecArray | null;
  while ((match = INLINE_RE.exec(text)) !== null) {
    if (match.index > lastIndex) {
      nodes.push({ kind: "text", value: text.slice(lastIndex, match.index) });
    }
    const [, code, bold, italic, linkText, linkHref] = match;
    if (code !== undefined) {
      nodes.push({ kind: "code", value: code });
    } else if (bold !== undefined) {
      nodes.push({ kind: "bold", value: bold });
    } else if (italic !== undefined) {
      nodes.push({ kind: "italic", value: italic });
    } else if (linkText !== undefined && linkHref !== undefined) {
      const href = sanitizeHref(linkHref);
      // Unsafe scheme: drop the URL entirely, keep the visible label as text.
      nodes.push(href ? { kind: "link", value: linkText, href } : { kind: "text", value: linkText });
    }
    lastIndex = match.index + match[0].length;
  }
  if (lastIndex < text.length) {
    nodes.push({ kind: "text", value: text.slice(lastIndex) });
  }
  return nodes;
}

function splitBlocks(src: string): string[] {
  return src
    .replace(/\r\n/g, "\n")
    .split(/\n{2,}/)
    .map((block) => block.trim())
    .filter(Boolean);
}

function parseHeading(block: string): MarkdownNode | null {
  const h3 = block.match(/^###\s+([\s\S]+)/);
  if (h3) return { type: "heading", level: 3, children: parseInline(h3[1].replace(/\n/g, " ")) };
  const h2 = block.match(/^##\s+([\s\S]+)/);
  if (h2) return { type: "heading", level: 2, children: parseInline(h2[1].replace(/\n/g, " ")) };
  return null;
}

function parseQuote(block: string): MarkdownNode | null {
  if (!block.startsWith(">")) return null;
  const text = block
    .split("\n")
    .map((line) => line.replace(/^>\s?/, ""))
    .join(" ")
    .trim();
  return { type: "quote", children: parseInline(text) };
}

function parseList(block: string): MarkdownNode | null {
  const lines = block.split("\n").map((line) => line.trim()).filter(Boolean);
  if (lines.length === 0) return null;

  const ordered = /^\d+\.\s/.test(lines[0]);
  const unordered = /^-\s/.test(lines[0]);
  if (!ordered && !unordered) return null;

  const marker = ordered ? /^\d+\.\s+/ : /^-\s+/;
  const items = lines.map((line) => parseInline(line.replace(marker, "")));
  return { type: "list", ordered, items };
}

function parseBlock(block: string): MarkdownNode {
  if (block === "---") return { type: "hr" };
  return (
    parseHeading(block) ??
    parseQuote(block) ??
    parseList(block) ??
    { type: "paragraph", children: parseInline(block.replace(/\n/g, " ")) }
  );
}

export function parseMarkdown(src: string): MarkdownNode[] {
  if (!src) return [];
  return splitBlocks(src)
    .slice(0, MAX_BLOCKS)
    .filter((block) => block.length <= MAX_BLOCK_LENGTH)
    .map(parseBlock);
}
