import type { Metadata } from "next";
import type { ReactNode } from "react";

import "./globals.css";

export const metadata: Metadata = {
  title: { default: "Sách Nhà | Mua và thuê sách", template: "%s | Sách Nhà" },
  description: "Không gian sách để mua sở hữu hoặc thuê linh hoạt theo nhịp đọc của bạn.",
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
