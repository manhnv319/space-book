import Image from "next/image";
import Link from "next/link";
import type { ReactNode } from "react";

import { logoutAction } from "@/app/actions/auth";
import { sendSupportMessageAction } from "@/app/actions/support";
import { CartBadge } from "@/components/cart/cart-badge";
import { CategoryNav } from "@/components/category-nav";
import { AnnouncementBar } from "@/components/layout/announcement-bar";
import { MobileNavigationDrawer } from "@/components/layout/mobile-navigation-drawer";
import { SiteSearchForm } from "@/components/layout/site-search-form";
import { SiteFooter } from "@/components/site-footer";
import { SupportWidget } from "@/components/support/support-widget";
import { NotificationBell } from "@/components/notification-bell";
import { AdminIcon, CartIcon, UserIcon } from "@/components/site-icons";
import { ADMIN_PERMISSIONS, hasPermission } from "@/lib/auth/permissions";
import { getCurrentUser } from "@/lib/bff/current-user";

export async function SiteShell({ children }: Readonly<{ children: ReactNode }>) {
  const user = await getCurrentUser();
  const navLinks = await CategoryNav();
  const isAdmin = ADMIN_PERMISSIONS.some((permission) => hasPermission(user, permission));
  return <div className="site-shell">
    <div className="utility-bar"><div><Link href="/goi-thue">Hướng dẫn thuê</Link><Link href="/#support">Hỗ trợ</Link><Link href="/bai-viet">Bài viết</Link></div><div>{user ? <><Link href="/account">{user.fullname ?? user.username ?? "Tài khoản"}</Link><form action={logoutAction}><button type="submit">Đăng xuất</button></form></> : <><Link href="/login">Đăng nhập</Link><Link href="/register">Đăng ký</Link></>}</div></div>
    <header className="site-header">
      <div className="site-header-main">
        <MobileNavigationDrawer>{navLinks}</MobileNavigationDrawer>
        <Link aria-label="Sách Nhà, trang chủ" className="brand" href="/"><Image alt="" aria-hidden="true" height={44} priority src="/brand/sach-nha-logo.png" width={44} /><span className="brand-name">Sách Nhà</span></Link>
        <SiteSearchForm />
        <div className="header-actions">
          <Link aria-label="Giỏ hàng" className="header-action" href="/gio-hang" title="Giỏ hàng"><CartIcon /><CartBadge /></Link>
          {user ? <NotificationBell /> : null}
          {user ? <Link aria-label="Tài khoản" className="header-action header-account" href="/account" title="Tài khoản"><UserIcon /></Link> : null}
          {isAdmin ? <Link aria-label="Quản trị" className="header-admin" href="/admin" title="Quản trị"><AdminIcon /></Link> : null}
          {!user ? <><Link className="header-login" href="/login">Đăng nhập</Link><Link className="header-register" href="/register">Đăng ký</Link></> : null}
        </div>
      </div>
      <nav aria-label="Điều hướng chính" className="site-nav">{navLinks}</nav>
      {user ? <div className="mobile-user-row"><Link href="/account"><UserIcon />Tài khoản</Link><Link href="/gio-hang"><CartIcon />Giỏ hàng<CartBadge /></Link><form action={logoutAction}><button type="submit">Đăng xuất</button></form></div> : <div className="mobile-join-row"><span><UserIcon />Tham gia để lưu sách yêu thích</span><span><Link className="header-login" href="/login">Đăng nhập</Link><Link className="header-register" href="/register">Đăng ký</Link></span></div>}
    </header>
    <AnnouncementBar />
    <main className="site-main">{children}</main>
    <SiteFooter />
    <SupportWidget action={user ? sendSupportMessageAction : undefined} signedIn={Boolean(user)} />
  </div>;
}
