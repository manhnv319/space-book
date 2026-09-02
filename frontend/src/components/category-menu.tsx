"use client";

import Link from "next/link";
import { useId, useState } from "react";
import type { FocusEvent, KeyboardEvent, PointerEvent } from "react";

import type { Category } from "@/lib/types/category";

export function CategoryMenu({ categories }: { categories: Category[] }) {
  const [open, setOpen] = useState(false);
  const panelId = useId();

  const closeWhenFocusLeaves = (event: FocusEvent<HTMLDivElement>) => {
    if (!event.currentTarget.contains(event.relatedTarget)) setOpen(false);
  };

  const openForMouse = (event: PointerEvent<HTMLDivElement>) => {
    if (event.pointerType === "mouse") setOpen(true);
  };

  const closeForMouse = (event: PointerEvent<HTMLDivElement>) => {
    if (event.pointerType === "mouse") setOpen(false);
  };

  const closeOnEscape = (event: KeyboardEvent<HTMLDivElement>) => {
    if (event.key === "Escape") setOpen(false);
  };

  return <div className="nav-category-menu" data-open={open} onBlur={closeWhenFocusLeaves} onKeyDown={closeOnEscape} onPointerEnter={openForMouse} onPointerLeave={closeForMouse}>
    <button aria-controls={panelId} aria-expanded={open} className="nav-category-menu-trigger" onClick={() => setOpen((current) => !current)} type="button">
      Danh mục
      <svg aria-hidden="true" className="nav-category-menu-chevron" viewBox="0 0 16 16"><path d="m4 6 4 4 4-4" fill="none" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" /></svg>
    </button>
    {open ? <div className="nav-category-menu-panel" id={panelId}>
      {categories.map((category) => <Link href={`/sach?categoryId=${category.id}`} key={category.id}>{category.name}</Link>)}
    </div> : null}
  </div>;
}
