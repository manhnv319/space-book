"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import type { ReactNode } from "react";

interface ScrollCarouselProps {
  children: ReactNode;
  label: string;
  header?: ReactNode;
  headerAction?: ReactNode;
}

export function ScrollCarousel({ children, label, header, headerAction }: ScrollCarouselProps) {
  const rowRef = useRef<HTMLUListElement>(null);
  const [position, setPosition] = useState({ start: true, end: false });
  const updatePosition = useCallback(() => {
    const row = rowRef.current;
    if (!row) return;
    setPosition({ start: row.scrollLeft <= 1, end: row.scrollLeft + row.clientWidth >= row.scrollWidth - 1 });
  }, []);
  const scroll = (direction: -1 | 1) => rowRef.current?.scrollBy({ left: direction * rowRef.current.clientWidth * 0.82, behavior: "smooth" });

  useEffect(() => {
    const row = rowRef.current;
    if (!row) return;
    const observer = new ResizeObserver(updatePosition);
    observer.observe(row);
    updatePosition();
    return () => observer.disconnect();
  }, [children, updatePosition]);

  return (
    <div className="scroll-carousel">
      {header ? <div className="scroll-carousel-header">
        <div className="section-header">{header}</div>
        <div className="scroll-carousel-actions">
          {headerAction}
          <div aria-label={`Điều khiển ${label}`} className="scroll-carousel-controls" role="group">
            <button aria-label={`Xem sách trước trong ${label}`} disabled={position.start} onClick={() => scroll(-1)} type="button">←</button>
            <button aria-label={`Xem sách tiếp theo trong ${label}`} disabled={position.end} onClick={() => scroll(1)} type="button">→</button>
          </div>
        </div>
      </div> : null}
      <ul aria-label={label} className="book-shelf-row" onScroll={updatePosition} ref={rowRef} tabIndex={0}>
        {children}
      </ul>
    </div>
  );
}
