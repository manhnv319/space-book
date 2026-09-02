import { redirect } from "next/navigation";

export const metadata = { title: "Hỗ trợ" };

export default async function SupportPage() {
  redirect("/#support");
}
