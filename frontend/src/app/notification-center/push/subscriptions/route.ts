import { NextResponse } from "next/server";

import { executeAtActionBoundary } from "@/lib/bff/action-mutation-boundary";

export async function POST(request: Request) {
  await executeAtActionBoundary<unknown>("/api/v1/notifications/push/subscriptions", { method: "POST", headers: { "content-type": "application/json" }, body: await request.text() });
  return new NextResponse(null, { status: 204 });
}

export async function DELETE(request: Request) {
  await executeAtActionBoundary<unknown>("/api/v1/notifications/push/subscriptions", { method: "DELETE", headers: { "content-type": "application/json" }, body: await request.text() });
  return new NextResponse(null, { status: 204 });
}
