import { Skeleton } from "@/components/ui/skeleton";

function BlogCardSkeleton() {
  return (
    <div className="skeleton-card">
      <Skeleton variant="cover" />
      <div style={{ padding: "1.25rem", display: "grid", gap: ".6rem" }}>
        <Skeleton variant="text" width="40%" />
        <Skeleton variant="text" width="90%" />
        <Skeleton variant="text" width="70%" />
      </div>
    </div>
  );
}

export default function Loading() {
  return (
    <div className="catalog-container blog-list-container" aria-busy="true" aria-live="polite">
      <div className="catalog-header">
        <Skeleton variant="text" width="10rem" />
        <Skeleton variant="text" width="60%" />
      </div>
      <div className="blog-grid">
        {Array.from({ length: 6 }).map((_, i) => (
          <BlogCardSkeleton key={i} />
        ))}
      </div>
    </div>
  );
}
