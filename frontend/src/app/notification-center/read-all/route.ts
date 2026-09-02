import { NextResponse } from "next/server";

import { executeAtActionBoundary } from "@/lib/bff/action-mutation-boundary";

export async function PATCH() {
  await executeAtActionBoundary<unknown>("/api/v1/notifications/read-all", { method: "PATCH" });
  return new NextResponse(null, { status: 204 });
}
