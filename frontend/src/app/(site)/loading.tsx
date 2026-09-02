import { Skeleton } from "@/components/ui/skeleton";

export default function Loading() {
  return <div aria-busy="true" aria-live="polite" className="home-container"><div className="hero-carousel hero-carousel--fallback"><div className="hero-slide-copy"><Skeleton variant="text" width="8rem" /><Skeleton variant="text" width="70%" /><Skeleton variant="text" width="45%" /></div></div><div className="book-shelf-row">{Array.from({ length: 5 }).map((_, index) => <Skeleton className="book-shelf-item" key={index} variant="cover" />)}</div></div>;
}
