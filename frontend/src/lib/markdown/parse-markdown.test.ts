import { describe, expect, it } from "vitest";

import { parseInline, parseMarkdown, sanitizeHref } from "@/lib/markdown/parse-markdown";

describe("parseMarkdown — XSS payloads (phase-09 acceptance criteria)", () => {
  it("treats a raw <script> tag as plain text, never as a distinct node type", () => {
    const nodes = parseMarkdown("<script>alert(1)</script>");
    expect(nodes).toEqual([
      { type: "paragraph", children: [{ kind: "text", value: "<script>alert(1)</script>" }] },
    ]);
  });

  it("treats a raw <img onerror> tag as plain text — no image syntax is supported", () => {
    const nodes = parseMarkdown('<img src=x onerror=alert(1)>');
    expect(nodes).toEqual([
      { type: "paragraph", children: [{ kind: "text", value: "<img src=x onerror=alert(1)>" }] },
    ]);
  });

  it("drops a javascript: link href, rendering only the label as plain text (never a link node)", () => {
    const nodes = parseMarkdown("[click](javascript:alert(1))");
    expect(nodes).toHaveLength(1);
    const paragraph = nodes[0] as { type: "paragraph"; children: { kind: string; value: string }[] };
    // Nested "(" in `alert(1)` makes the naive `[^)]+` href match stop at the
    // first ")" — the trailing ")" spills out as a harmless literal text
    // node. What matters for security: no "link" kind is ever produced, and
    // the visible label is plain text.
    expect(paragraph.children.some((child) => child.kind === "link")).toBe(false);
    expect(paragraph.children.map((child) => child.value).join("")).toBe("click)");
  });

  it("never produces a link node for javascript:/data:/vbscript: schemes", () => {
    for (const href of ["javascript:alert(1)", "data:text/html,<script>alert(1)</script>", "vbscript:alert(1)"]) {
      const [node] = parseInline(`[x](${href})`);
      expect(node).toEqual({ kind: "text", value: "x" });
    }
  });
});

describe("sanitizeHref", () => {
  it("allows http/https/root-relative URLs", () => {
    expect(sanitizeHref("https://example.com")).toBe("https://example.com");
    expect(sanitizeHref("http://example.com")).toBe("http://example.com");
    expect(sanitizeHref("/sach/1")).toBe("/sach/1");
  });

  it("rejects unsafe schemes", () => {
    expect(sanitizeHref("javascript:alert(1)")).toBeNull();
    expect(sanitizeHref("data:text/html,x")).toBeNull();
    expect(sanitizeHref("vbscript:msgbox(1)")).toBeNull();
  });

  it("trims surrounding whitespace before checking the scheme", () => {
    expect(sanitizeHref("  https://example.com  ")).toBe("https://example.com");
  });
});

describe("parseMarkdown — supported syntax subset", () => {
  it("parses headings", () => {
    expect(parseMarkdown("## Heading 2")).toEqual([
      { type: "heading", level: 2, children: [{ kind: "text", value: "Heading 2" }] },
    ]);
    expect(parseMarkdown("### Heading 3")).toEqual([
      { type: "heading", level: 3, children: [{ kind: "text", value: "Heading 3" }] },
    ]);
  });

  it("parses a paragraph with bold, italic, code and a safe link", () => {
    const [node] = parseMarkdown("**bold** *italic* `code` [site](https://example.com)");
    expect(node).toEqual({
      type: "paragraph",
      children: [
        { kind: "bold", value: "bold" },
        { kind: "text", value: " " },
        { kind: "italic", value: "italic" },
        { kind: "text", value: " " },
        { kind: "code", value: "code" },
        { kind: "text", value: " " },
        { kind: "link", value: "site", href: "https://example.com" },
      ],
    });
  });

  it("parses unordered and ordered lists", () => {
    expect(parseMarkdown("- one\n- two")).toEqual([
      {
        type: "list",
        ordered: false,
        items: [
          [{ kind: "text", value: "one" }],
          [{ kind: "text", value: "two" }],
        ],
      },
    ]);
    expect(parseMarkdown("1. one\n2. two")).toEqual([
      {
        type: "list",
        ordered: true,
        items: [
          [{ kind: "text", value: "one" }],
          [{ kind: "text", value: "two" }],
        ],
      },
    ]);
  });

  it("parses a blockquote spanning multiple lines", () => {
    expect(parseMarkdown("> line one\n> line two")).toEqual([
      { type: "quote", children: [{ kind: "text", value: "line one line two" }] },
    ]);
  });

  it("parses a horizontal rule", () => {
    expect(parseMarkdown("---")).toEqual([{ type: "hr" }]);
  });

  it("separates blocks on blank lines", () => {
    const nodes = parseMarkdown("## Title\n\nParagraph one.\n\nParagraph two.");
    expect(nodes).toHaveLength(3);
    expect(nodes[0].type).toBe("heading");
    expect(nodes[1].type).toBe("paragraph");
    expect(nodes[2].type).toBe("paragraph");
  });

  it("returns an empty array for empty input", () => {
    expect(parseMarkdown("")).toEqual([]);
  });
});

describe("parseMarkdown — input size limits (ReDoS / oversized payload defense)", () => {
  it("skips blocks longer than 5000 characters", () => {
    const hugeBlock = "a".repeat(5001);
    expect(parseMarkdown(hugeBlock)).toEqual([]);
  });

  it("keeps a block at exactly the 5000 character limit", () => {
    const exactBlock = "a".repeat(5000);
    expect(parseMarkdown(exactBlock)).toHaveLength(1);
  });

  it("caps the number of processed blocks at 200", () => {
    const blocks = Array.from({ length: 250 }, (_, i) => `Paragraph ${i}`).join("\n\n");
    expect(parseMarkdown(blocks)).toHaveLength(200);
  });
});
