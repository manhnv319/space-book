import { NextResponse } from "next/server";

import { apiRead } from "@/lib/bff/server-fetch";

export async function GET() {
  return NextResponse.json(await apiRead<string>("/api/v1/notifications/push/public-key"));
}
