import Link from "next/link";
import { ChevronLeftIcon, ChevronRightIcon, ChevronsLeftIcon, ChevronsRightIcon } from "@/components/site-icons";
import type { ReactNode } from "react";

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  hrefForPage: (page: number) => string;
  ariaLabel?: string;
}

type PageItem = number | "ellipsis";

function getPageItems(currentPage: number, totalPages: number): PageItem[] {
  if (totalPages <= 5) return Array.from({ length: totalPages }, (_, index) => index);

  const pageSet = new Set([0, 1, totalPages - 2, totalPages - 1, currentPage]);
  if (currentPage > 1) pageSet.add(currentPage - 1);
  if (currentPage < totalPages - 2) pageSet.add(currentPage + 1);

  const pages = [...pageSet].filter((page) => page >= 0 && page < totalPages).sort((a, b) => a - b);
  return pages.reduce<PageItem[]>((items, page, index) => {
    if (index > 0 && page - pages[index - 1] > 1) items.push("ellipsis");
    items.push(page);
    return items;
  }, []);
}

function NavigationLink({
  label,
  icon,
  page,
  disabled,
  hrefForPage,
}: {
  label: string;
  icon: ReactNode;
  page: number;
  disabled: boolean;
  hrefForPage: (page: number) => string;
}) {
  if (disabled) {
    return <span className="pagination-link pagination-icon-link is-disabled" aria-disabled="true" aria-label={label} title={label}>{icon}</span>;
  }

  return <Link className="pagination-link pagination-icon-link" href={hrefForPage(page)} aria-label={label} title={label}>{icon}</Link>;
}

export function Pagination({ currentPage, totalPages, hrefForPage, ariaLabel = "Phân trang" }: PaginationProps) {
  if (totalPages <= 1) return null;

  const safePage = Math.min(Math.max(currentPage, 0), totalPages - 1);
  const pageItems = getPageItems(safePage, totalPages);

  return (
    <nav className="pagination" aria-label={ariaLabel}>
      <NavigationLink label="Trang đầu" icon={<ChevronsLeftIcon />} page={0} disabled={safePage === 0} hrefForPage={hrefForPage} />
      <NavigationLink label="Trang trước" icon={<ChevronLeftIcon />} page={safePage - 1} disabled={safePage === 0} hrefForPage={hrefForPage} />

      <div className="pagination-pages">
        {pageItems.map((item, index) => item === "ellipsis" ? (
          <span className="pagination-ellipsis" key={`ellipsis-${index}`} aria-hidden="true">…</span>
        ) : (
          <Link
            className={`pagination-page${item === safePage ? " is-current" : ""}`}
            href={hrefForPage(item)}
            aria-label={`Trang ${item + 1}`}
            aria-current={item === safePage ? "page" : undefined}
            key={item}
          >
            {item + 1}
          </Link>
        ))}
      </div>

      <NavigationLink label="Trang sau" icon={<ChevronRightIcon />} page={safePage + 1} disabled={safePage === totalPages - 1} hrefForPage={hrefForPage} />
      <NavigationLink label="Trang cuối" icon={<ChevronsRightIcon />} page={totalPages - 1} disabled={safePage === totalPages - 1} hrefForPage={hrefForPage} />
    </nav>
  );
}
