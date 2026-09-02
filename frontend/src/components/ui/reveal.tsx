import type { CSSProperties, ReactNode } from "react";

interface RevealProps {
  children: ReactNode;
  /** Position in a list, used for scroll-reveal stagger via `--i`. */
  index?: number;
  className?: string;
}

/**
 * Server component: wraps content in the `.reveal` scroll-timeline animation
 * (see src/styles/motion.css). Zero JS — falls back to fully visible content
 * on browsers without `animation-timeline: view()` support.
 */
export function Reveal({ children, index, className }: RevealProps) {
  const style = index != null ? ({ "--i": index } as CSSProperties) : undefined;
  const classes = ["reveal", className].filter(Boolean).join(" ");

  return (
    <div className={classes} style={style}>
      {children}
    </div>
  );
}
