"use server";

import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";

import { executeAuthenticatedMutation } from "@/app/actions/authenticated-mutation";
import { BackendError } from "@/lib/bff/backend-error";

export type ActionResult = { status: "ok" | "error"; message: string };

type BlogPostInput = {
  title: string;
  content: string;
  slug?: string;
  excerpt?: string;
  coverImageUrl?: string;
  bookId?: number;
};

function revalidateBlogViews(): void {
  revalidatePath("/admin/bai-viet");
  // A publish/create/update can flip what the public feed shows — always
  // revalidate it too, see phase constraint "bài publish phải hiện ngay".
  revalidatePath("/bai-viet");
}

function blogMutationError(error: unknown): string {
  if (error instanceof BackendError) {
    // sessionVersion mismatch also surfaces as 403 with no authority set —
    // both cases are indistinguishable to the client, so unify the message.
    if (error.status === 401 || error.status === 403) {
      return "Phiên đăng nhập đã hết hạn hoặc không đủ quyền, vui lòng đăng nhập lại.";
    }
    if (error.status === 404) return "Bài viết không tồn tại.";
  }
  return "Không thể lưu bài viết lúc này.";
}

function parseBlogPostInput(data: FormData): { input: BlogPostInput } | { error: string } {
  const title = String(data.get("title") ?? "").trim();
  const content = String(data.get("content") ?? "").trim();
  if (!title) return { error: "Vui lòng nhập tiêu đề." };
  if (!content) return { error: "Vui lòng nhập nội dung." };

  const slug = String(data.get("slug") ?? "").trim();
  const excerpt = String(data.get("excerpt") ?? "").trim();
  const coverImageUrl = String(data.get("coverImageUrl") ?? "").trim();
  if (coverImageUrl && !/^https?:\/\//.test(coverImageUrl)) {
    return { error: "Ảnh bìa phải là URL bắt đầu bằng http:// hoặc https://." };
  }

  const bookIdRaw = String(data.get("bookId") ?? "").trim();
  let bookId: number | undefined;
  if (bookIdRaw) {
    bookId = Number(bookIdRaw);
    if (!Number.isInteger(bookId) || bookId <= 0) return { error: "Mã sách không hợp lệ." };
  }

  return {
    input: {
      title,
      content,
      ...(slug ? { slug } : {}),
      ...(excerpt ? { excerpt } : {}),
      ...(coverImageUrl ? { coverImageUrl } : {}),
      ...(bookId !== undefined ? { bookId } : {}),
    },
  };
}

/**
 * Handles both create (no `id` field) and update (hidden `id` field) — the
 * form (`<BlogPostForm>`) is shared for `/admin/bai-viet/new` and `/[id]`.
 * `authorId` is never sent: BE derives it from `currentUserId` server-side.
 */
export async function saveBlogPostAction(_prev: ActionResult, data: FormData): Promise<ActionResult> {
  const parsed = parseBlogPostInput(data);
  if ("error" in parsed) return { status: "error", message: parsed.error };

  const idRaw = String(data.get("id") ?? "").trim();
  const id = idRaw ? Number(idRaw) : undefined;

  try {
    if (id !== undefined) {
      await executeAuthenticatedMutation(`/api/v1/blog-posts/${id}`, {
        method: "PUT",
        headers: { "content-type": "application/json" },
        body: JSON.stringify(parsed.input),
      });
    } else {
      await executeAuthenticatedMutation("/api/v1/blog-posts", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify(parsed.input),
      });
    }
  } catch (error) {
    return { status: "error", message: blogMutationError(error) };
  }

  revalidateBlogViews();
  redirect("/admin/bai-viet");
}

/**
 * Zero-JS `<form action={...}>` actions below — no client feedback loop by
 * design (see phase report "Quyết định ngoài spec"). Failures are logged
 * server-side and the action no-ops; the admin can retry from the table.
 */
export async function deleteBlogPostAction(data: FormData): Promise<void> {
  const id = Number(data.get("id"));
  if (!Number.isInteger(id) || id <= 0) return;
  try {
    await executeAuthenticatedMutation(`/api/v1/blog-posts/${id}`, { method: "DELETE" });
  } catch (error) {
    console.error("Failed to delete blog post", { id, error: error instanceof Error ? error.message : String(error) });
    return;
  }
  revalidateBlogViews();
}

export async function publishBlogPostAction(data: FormData): Promise<void> {
  const id = Number(data.get("id"));
  if (!Number.isInteger(id) || id <= 0) return;
  try {
    await executeAuthenticatedMutation(`/api/v1/blog-posts/${id}/publish`, { method: "POST" });
  } catch (error) {
    console.error("Failed to publish blog post", { id, error: error instanceof Error ? error.message : String(error) });
    return;
  }
  revalidateBlogViews();
}

export async function unpublishBlogPostAction(data: FormData): Promise<void> {
  const id = Number(data.get("id"));
  if (!Number.isInteger(id) || id <= 0) return;
  try {
    await executeAuthenticatedMutation(`/api/v1/blog-posts/${id}/unpublish`, { method: "POST" });
  } catch (error) {
    console.error("Failed to unpublish blog post", { id, error: error instanceof Error ? error.message : String(error) });
    return;
  }
  revalidateBlogViews();
}
