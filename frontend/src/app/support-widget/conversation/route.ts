import { NextResponse } from "next/server";

import { getMyConversation } from "@/lib/services/support-service";

/** Browser-safe BFF endpoint: the browser never receives a bearer token. */
export async function GET() {
  const conversation = await getMyConversation();
  if (!conversation) return NextResponse.json({ message: "Không tải được hội thoại." }, { status: 401 });
  return NextResponse.json(conversation, { headers: { "cache-control": "no-store" } });
}
