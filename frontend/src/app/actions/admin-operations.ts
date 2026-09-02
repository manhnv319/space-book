"use server";

import { revalidatePath } from "next/cache";

import { executeAuthenticatedMutation } from "@/app/actions/authenticated-mutation";

export async function updateAdminOrderStatusAction(data: FormData): Promise<void> {
  const orderId = Number(data.get("orderId"));
  const newStatus = String(data.get("newStatus") ?? "");
  if (!Number.isInteger(orderId) || orderId <= 0 || !newStatus) return;
  try {
    await executeAuthenticatedMutation(`/api/v1/orders/${orderId}/status`, {
      method: "PUT", headers: { "content-type": "application/json" }, body: JSON.stringify({ newStatus }),
    });
    revalidatePath("/admin/don-hang");
    revalidatePath("/admin");
  } catch (error) {
    console.error("Failed to update order status", { orderId, error: error instanceof Error ? error.message : String(error) });
  }
}

export async function forceReturnRentalAction(data: FormData): Promise<void> {
  const rentalId = Number(data.get("rentalId"));
  if (!Number.isInteger(rentalId) || rentalId <= 0) return;
  try {
    await executeAuthenticatedMutation(`/api/v1/rentals/${rentalId}/force-return`, {
      method: "POST", headers: { "content-type": "application/json" }, body: JSON.stringify({ damageFeeAmount: 0, notes: "Trả sách tại quầy" }),
    });
    revalidatePath("/admin/thue-sach");
    revalidatePath("/admin");
  } catch (error) {
    console.error("Failed to force return rental", { rentalId, error: error instanceof Error ? error.message : String(error) });
  }
}

export async function resolveUnmatchedTransferAction(data: FormData): Promise<void> {
  const transferId = Number(data.get("transferId")); const orderId = Number(data.get("orderId"));
  if (!Number.isInteger(transferId) || !Number.isInteger(orderId) || transferId <= 0 || orderId <= 0) return;
  try {
    await executeAuthenticatedMutation(`/api/v1/bank-transfers/unmatched/${transferId}/resolve`, { method: "POST", headers: { "content-type": "application/json" }, body: JSON.stringify({ orderId }) });
    revalidatePath("/admin/doi-soat"); revalidatePath("/admin/don-hang"); revalidatePath("/admin");
  } catch (error) { console.error("Failed to resolve unmatched transfer", { transferId, error: error instanceof Error ? error.message : String(error) }); }
}
