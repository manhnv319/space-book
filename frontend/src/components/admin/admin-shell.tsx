"use client";

import Link from "next/link";
import { useState, type ReactNode } from "react";

import { logoutAction } from "@/app/actions/auth";
import { AdminNavigation, type AdminNavigationGroup } from "@/components/admin/admin-navigation";
import { NotificationBell } from "@/components/notification-bell";
import { LogoutIcon, StorefrontIcon, UserIcon } from "@/components/site-icons";
import {
  hasPermission,
  PERMISSION_CHECKIN_RENTALS,
  PERMISSION_HANDLE_PAYMENTS,
  PERMISSION_HANDLE_SUPPORT,
  PERMISSION_MANAGE_CONTENT,
  PERMISSION_READ_RENTALS,
  PERMISSION_UPDATE_ORDERS,
} from "@/lib/auth/permissions";
import type { CurrentUser } from "@/lib/bff/current-user";

/** Dense, scan-first shell — deliberately not `<SiteShell>` (D6). */
export function AdminShell({ user, children, supportUnreadCount }: Readonly<{ user: CurrentUser; children: ReactNode; supportUnreadCount: number }>) {
  const [collapsed, setCollapsed] = useState(false);
  const operations = [
    ...(hasPermission(user, PERMISSION_UPDATE_ORDERS) || hasPermission(user, PERMISSION_HANDLE_SUPPORT) ? [{ href: "/admin/don-hang", label: "Đơn hàng", icon: "orders" as const }] : []),
    ...(hasPermission(user, PERMISSION_READ_RENTALS) || hasPermission(user, PERMISSION_CHECKIN_RENTALS) ? [{ href: "/admin/thue-sach", label: "Thuê sách", icon: "rentals" as const }] : []),
    ...(hasPermission(user, PERMISSION_HANDLE_PAYMENTS) ? [{ href: "/admin/doi-soat", label: "Đối soát", icon: "payments" as const }] : []),
    ...(hasPermission(user, PERMISSION_HANDLE_SUPPORT) ? [{ href: "/admin/ho-tro", label: "Hỗ trợ khách hàng", icon: "support" as const, badge: supportUnreadCount }] : []),
  ];
  const groups: AdminNavigationGroup[] = [
    { label: "Tổng quan", icon: "dashboard", items: [{ href: "/admin", label: "Bảng điều khiển", icon: "dashboard" }] },
    ...(hasPermission(user, PERMISSION_MANAGE_CONTENT) ? [{ label: "Nội dung", icon: "books" as const, items: [{ href: "/admin/sach", label: "Trưng bày sách", icon: "books" as const }, { href: "/admin/bai-viet", label: "Bài viết", icon: "article" as const }] }] : []),
    ...(operations.length ? [{ label: "Vận hành", icon: "orders" as const, items: operations }] : []),
  ];

  return (
    <div className={`admin-shell${collapsed ? " is-sidebar-collapsed" : ""}`}>
      <aside className="admin-sidebar">
        <button aria-label={collapsed ? "Mở rộng menu quản trị" : "Thu gọn menu quản trị"} className="admin-sidebar-collapse" onClick={() => setCollapsed((current) => !current)} title={collapsed ? "Mở rộng menu" : "Thu gọn menu"} type="button">
          <svg aria-hidden="true" viewBox="0 0 24 24"><path d={collapsed ? "m9 5 7 7-7 7" : "m15 5-7 7 7 7"} fill="none" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.8" /></svg>
        </button>
        <Link className="admin-brand" href="/admin">
          VelstrongBook <span>Quản trị</span>
        </Link>
        <AdminNavigation collapsed={collapsed} groups={groups} />
        <div className="admin-sidebar-footer">
          <span className="admin-user-icon" aria-label={`Tài khoản: ${user.fullname ?? user.username ?? user.email}`} title={user.fullname ?? user.username ?? user.email}><UserIcon /></span>
          <Link aria-label="Về cửa hàng" className="admin-footer-icon" href="/" title="Về cửa hàng"><StorefrontIcon /></Link>
          <form action={logoutAction}><button aria-label="Đăng xuất" className="admin-footer-icon" title="Đăng xuất" type="submit"><LogoutIcon /></button></form>
        </div>
      </aside>

      <div className="admin-main">
        <header className="admin-topbar">
          <span className="admin-topbar-user">Không gian vận hành</span>
          <NotificationBell />
        </header>
        <main className="admin-content">{children}</main>
      </div>
    </div>
  );
}
