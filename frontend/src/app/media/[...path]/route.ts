import { NextResponse } from "next/server";

import { backendRequest } from "@/lib/bff/backend-request";

export async function GET(_request: Request, { params }: { params: Promise<{ path: string[] }> }) {
  const { path } = await params;
  const backendPath = `/media/${path.map((segment) => encodeURIComponent(segment)).join("/")}`;
  const response = await backendRequest(backendPath, { headers: { accept: "image/*" } });
  if (!response.ok || !response.body) return new NextResponse("Media not found", { status: response.status || 404 });

  const headers = new Headers();
  const contentType = response.headers.get("content-type");
  const cacheControl = response.headers.get("cache-control");
  if (contentType) headers.set("content-type", contentType);
  if (cacheControl) headers.set("cache-control", cacheControl);
  return new NextResponse(response.body, { status: 200, headers });
}
