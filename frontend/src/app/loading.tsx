import { AuthBrand } from "@/components/auth-brand";

export default function Loading() {
  return (
    <main aria-busy="true" aria-live="polite" className="loading-screen">
      <div className="loading-card">
        <AuthBrand />
        <span aria-hidden="true" className="loading-spinner" />
        <p>Đang tải</p>
      </div>
    </main>
  );
}
