import type { Metadata } from "next";
import type { ReactNode } from "react";

import "./globals.css";

export const metadata: Metadata = {
  title: { default: "VelstrongBook | Mua và thuê sách", template: "%s | VelstrongBook" },
  description: "Khám phá sách để mua sở hữu hoặc thuê linh hoạt cùng VelstrongBook.",
};

export default function RootLayout({ children }: Readonly<{ children: ReactNode }>) {
  return (
    <html lang="vi">
      <body>
        {children}
      </body>
    </html>
  );
}
