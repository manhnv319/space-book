interface SkeletonProps {
  variant: "text" | "card" | "cover";
  className?: string;
  /** Only used by the "text" variant to vary line width, e.g. "60%". */
  width?: string;
}

/**
 * Server component: shimmering placeholder for loading states. CSS-only
 * (gradient + background-position animation), no JS timers.
 */
export function Skeleton({ variant, className, width }: SkeletonProps) {
  const classes = ["skeleton", `skeleton-${variant}`, className].filter(Boolean).join(" ");
  const style = variant === "text" && width ? { width } : undefined;

  return <div className={classes} style={style} aria-hidden="true" />;
}
