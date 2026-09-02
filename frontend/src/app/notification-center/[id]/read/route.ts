import { NextResponse } from "next/server";

import { executeAtActionBoundary } from "@/lib/bff/action-mutation-boundary";
import type { UserNotification } from "@/lib/types/notification";

export async function PATCH(_: Request, { params }: { params: Promise<{ id: string }> }) {
  const { id } = await params;
  if (!/^\d+$/.test(id)) return NextResponse.json({ message: "Thông báo không hợp lệ." }, { status: 400 });
  return NextResponse.json(await executeAtActionBoundary<UserNotification>(`/api/v1/notifications/${id}/read`, { method: "PATCH" }));
}
