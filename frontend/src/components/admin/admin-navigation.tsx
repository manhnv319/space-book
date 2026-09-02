"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useState, type ReactNode } from "react";

export interface AdminNavigationGroup {
  label: string;
  icon: AdminNavigationIconName;
  items: Array<{ href: string; label: string; icon: AdminNavigationIconName; badge?: number }>;
}

export type AdminNavigationIconName = "dashboard" | "books" | "article" | "orders" | "rentals" | "payments" | "support";

function AdminNavigationIcon({ name }: Readonly<{ name: AdminNavigationIconName }>) {
  const paths: Record<AdminNavigationIconName, ReactNode> = {
    dashboard: <><rect height="6" rx="1" width="6" x="4" y="4" /><rect height="6" rx="1" width="6" x="14" y="4" /><rect height="6" rx="1" width="6" x="4" y="14" /><rect height="6" rx="1" width="6" x="14" y="14" /></>,
    books: <><path d="M5 4.5h11a3 3 0 0 1 3 3V20H8a3 3 0 0 1-3-3V4.5Z" /><path d="M8 20V7.5a3 3 0 0 1 3-3" /><path d="M9 9h7M9 12h7" /></>,
    article: <><path d="M5 4h14v16H5z" /><path d="M8 8h8M8 12h8M8 16h5" /></>,
    orders: <><path d="M5 7h14v13H5z" /><path d="M8 7a4 4 0 0 1 8 0M8 11h8" /></>,
    rentals: <><circle cx="12" cy="12" r="8" /><path d="M12 7v5l3 2" /></>,
    payments: <><rect height="14" rx="2" width="16" x="4" y="5" /><path d="M4 9h16M8 14h3" /></>,
    support: <><path d="M5 17.5V12a7 7 0 0 1 14 0v5.5" /><path d="M5 15H4a2 2 0 0 0 0 4h1M19 15h1a2 2 0 0 1 0 4h-1M12 19h3" /></>,
  };

  return <svg aria-hidden="true" className="admin-nav-icon" viewBox="0 0 24 24">{paths[name]}</svg>;
}

export function AdminNavigation({ collapsed, groups }: Readonly<{ collapsed: boolean; groups: AdminNavigationGroup[] }>) {
  const pathname = usePathname();
  const [expanded, setExpanded] = useState<string | null>(() => groups.find((group) => group.items.some((item) => pathname === item.href || pathname.startsWith(`${item.href}/`)))?.label ?? groups[0]?.label ?? null);

  return (
    <nav aria-label="Điều hướng quản trị" className="admin-nav">
      {groups.map((group) => (
        <div className="admin-nav-group" key={group.label}>
          <button aria-expanded={expanded === group.label} className="admin-nav-group-trigger" onClick={() => setExpanded((current) => current === group.label ? null : group.label)} type="button">
            <span className="admin-nav-group-icon"><AdminNavigationIcon name={group.icon} /><span>{group.label}</span></span>
            <svg aria-hidden="true" className="admin-nav-group-chevron" viewBox="0 0 16 16"><path d="m4 6 4 4 4-4" fill="none" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" /></svg>
          </button>
          <div aria-hidden={!collapsed && expanded !== group.label} className={`admin-nav-group-panel${expanded === group.label ? "" : " is-hidden"}`}>{group.items.map((item) => {
            const active = item.href === "/admin" ? pathname === item.href : pathname.startsWith(item.href);
            return <Link aria-label={item.label} className={active ? "is-active" : ""} href={item.href} key={item.href} title={item.label}><AdminNavigationIcon name={item.icon} /><span>{item.label}</span>{item.badge ? <strong aria-label={`${item.badge} hội thoại chưa đọc`} className="admin-nav-badge">{item.badge > 99 ? "99+" : item.badge}</strong> : null}</Link>;
          })}</div>
        </div>
      ))}
    </nav>
  );
}
