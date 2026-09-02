"use client";

import Link from "next/link";
import { useRef, useState } from "react";

import { HeroSlide } from "@/components/home/hero-slide";
import type { HeroSlide as HeroSlideModel } from "@/lib/home/home-view-model";

export function HeroCarousel({ slides }: { slides: HeroSlideModel[] }) {
  const [active, setActive] = useState(0);
  const startX = useRef<number | null>(null);
  const count = slides.length;
  const move = (direction: -1 | 1) => setActive((value) => Math.max(0, Math.min(count - 1, value + direction)));

  if (!count) return <HeroFallback />;
  if (count === 1) return <div className="hero-carousel hero-spotlight"><HeroSlide eager headingLevel="h1" slide={slides[0]} /></div>;

  return (
    <section aria-label="Sách nổi bật" aria-roledescription="carousel" className="hero-carousel hero-spotlight" onKeyDown={(event) => {
      if (event.key === "ArrowLeft") move(-1);
      if (event.key === "ArrowRight") move(1);
    }} tabIndex={0}>
      <div className="hero-carousel-track" onPointerDown={(event) => { startX.current = event.clientX; }} onPointerUp={(event) => {
        if (startX.current === null) return;
        const delta = event.clientX - startX.current;
        if (Math.abs(delta) > 48) move(delta > 0 ? -1 : 1);
        startX.current = null;
      }} style={{ transform: `translateX(-${active * 100}%)` }}>
        {slides.map((slide, index) => <div aria-hidden={index !== active} aria-label={`${index + 1} trên ${count}: ${slide.book.title}`} aria-roledescription="slide" className="hero-carousel-item" inert={index !== active} key={slide.book.id} role="group"><HeroSlide eager={index === 0} headingLevel={index === active ? "h1" : "h2"} slide={slide} /></div>)}
      </div>
      <div className="hero-carousel-nav">
        <button aria-label="Xem slide trước" disabled={active === 0} onClick={() => move(-1)} type="button">←</button>
        <div aria-label={`Slide ${active + 1} trên ${count}`} className="hero-carousel-dots" role="tablist">
          {slides.map((slide, index) => <button aria-label={`Xem ${slide.book.title}`} aria-selected={index === active} key={slide.book.id} onClick={() => setActive(index)} role="tab" type="button" />)}
        </div>
        <button aria-label="Xem slide tiếp theo" disabled={active === count - 1} onClick={() => move(1)} type="button">→</button>
      </div>
    </section>
  );
}

function HeroFallback() {
  return <section className="hero-carousel hero-carousel--fallback hero-spotlight"><div className="hero-slide-copy"><p className="eyebrow">Sách Nhà</p><h1>Một không gian để tìm cuốn sách tiếp theo.</h1><p className="hero-slide-description">Khám phá sách để mua sở hữu hoặc thuê linh hoạt theo nhịp đọc của bạn.</p><Link className="button" href="/sach">Khám phá sách</Link></div></section>;
}
