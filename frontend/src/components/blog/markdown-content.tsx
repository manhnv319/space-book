import type { Inline, MarkdownNode } from "@/lib/markdown/parse-markdown";

/**
 * Server component: turns a `MarkdownNode[]` tree into React elements.
 * NEVER use `dangerouslySetInnerHTML` here — every text value below is a
 * JSX child (string or `{value}`), which React escapes automatically. This
 * is the only place `MarkdownNode[]` becomes markup.
 */
function renderInline(children: Inline[]) {
  return children.map((child, index) => {
    switch (child.kind) {
      case "bold":
        return <strong key={index}>{child.value}</strong>;
      case "italic":
        return <em key={index}>{child.value}</em>;
      case "code":
        return <code key={index}>{child.value}</code>;
      case "link":
        return (
          <a key={index} href={child.href} rel="noopener noreferrer" target="_blank">
            {child.value}
          </a>
        );
      case "text":
      default:
        return child.value;
    }
  });
}

export function MarkdownContent({ nodes }: { nodes: MarkdownNode[] }) {
  return (
    <div className="prose">
      {nodes.map((node, index) => {
        switch (node.type) {
          case "heading":
            return node.level === 2 ? (
              <h2 key={index}>{renderInline(node.children)}</h2>
            ) : (
              <h3 key={index}>{renderInline(node.children)}</h3>
            );
          case "paragraph":
            return <p key={index}>{renderInline(node.children)}</p>;
          case "list":
            return node.ordered ? (
              <ol key={index}>
                {node.items.map((item, itemIndex) => (
                  <li key={itemIndex}>{renderInline(item)}</li>
                ))}
              </ol>
            ) : (
              <ul key={index}>
                {node.items.map((item, itemIndex) => (
                  <li key={itemIndex}>{renderInline(item)}</li>
                ))}
              </ul>
            );
          case "quote":
            return <blockquote key={index}>{renderInline(node.children)}</blockquote>;
          case "hr":
            return <hr key={index} />;
          default:
            return null;
        }
      })}
    </div>
  );
}
