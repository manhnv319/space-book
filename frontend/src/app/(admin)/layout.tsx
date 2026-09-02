import { redirect } from "next/navigation";
import type { ReactNode } from "react";

import { AdminShell } from "@/components/admin/admin-shell";
import { ADMIN_PERMISSIONS, hasPermission, PERMISSION_HANDLE_SUPPORT } from "@/lib/auth/permissions";
import { getCurrentUser } from "@/lib/bff/current-user";
import { getSupportUnreadCount } from "@/lib/services/support-service";

/** Admin guard; backend permission checks remain the authorization authority. */
export default async function AdminLayout({ children }: Readonly<{ children: ReactNode }>) {
  const user = await getCurrentUser();
  if (!user) redirect("/login?next=/admin");
  if (!ADMIN_PERMISSIONS.some((permission) => hasPermission(user, permission))) redirect("/");
  const supportUnreadCount = hasPermission(user, PERMISSION_HANDLE_SUPPORT) ? await getSupportUnreadCount().catch(() => 0) : 0;
  return <AdminShell supportUnreadCount={supportUnreadCount} user={user}>{children}</AdminShell>;
}
