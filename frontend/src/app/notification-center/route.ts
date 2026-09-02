import { NextResponse } from "next/server";

import { apiRead } from "@/lib/bff/server-fetch";
import type { NotificationPage } from "@/lib/types/notification";

export async function GET(request: Request) {
  const url = new URL(request.url);
  const page = url.searchParams.get("page") ?? "0";
  const size = url.searchParams.get("size") ?? "12";
  const result = await apiRead<NotificationPage>(`/api/v1/notifications?page=${encodeURIComponent(page)}&size=${encodeURIComponent(size)}`);
  return NextResponse.json(result);
}
