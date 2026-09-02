import type { ReactNode } from "react";
import { SiteShell } from "@/components/site-shell";

export default function AuthLayout({ children }: Readonly<{ children: ReactNode }>) {
  return <SiteShell>{children}</SiteShell>;
}
