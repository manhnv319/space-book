import Link from "next/link";
import { redirect } from "next/navigation";

import { AddressForm } from "@/components/account/address-form";
import { PasswordSecurityForm, PersonalProfileForm } from "@/components/account/account-profile-forms";
import { DeleteAddressButton } from "@/components/account/delete-address-button";
import { Badge } from "@/components/ui/badge";
import { getCurrentUser } from "@/lib/bff/current-user";
import { getAddresses } from "@/lib/services/checkout-service";
import type { Address } from "@/lib/types/checkout";
import { listProvinces } from "@/lib/vn-address/units";

export const metadata = { title: "Tài khoản" };

/** Địa chỉ cũ còn lưu cấp huyện thì vẫn hiện đủ; địa chỉ mới chỉ có hai cấp. */
function formatAddress(address: Address): string {
  return [address.addressDetail, address.ward, address.district, address.province]
    .filter((part) => part && part.trim())
    .join(", ");
}

const sections = [
  ["profile", "Thông tin cá nhân"], ["security", "Bảo mật"], ["addresses", "Địa chỉ"],
] as const;

export default async function AccountPage({ searchParams }: { searchParams: Promise<{ section?: string }> }) {
  const user = await getCurrentUser();
  if (!user) redirect("/login?next=%2Faccount");
  const { section } = await searchParams;
  const activeSection = sections.some(([key]) => key === section) ? section : "profile";

  const addresses = await getAddresses().catch(() => null);
  const provinces = listProvinces();

  return (
    <section className="account-page account-settings-layout">
      <aside className="account-sidebar"><div className="account-avatar">{(user.fullname ?? user.username ?? user.email).slice(0, 1).toUpperCase()}</div><strong>{user.fullname ?? user.username}</strong><span>{user.email}</span><nav aria-label="Cài đặt tài khoản" className="account-nav">{sections.map(([key, label]) => <Link className={activeSection === key ? "is-active" : ""} href={`/account?section=${key}`} key={key}>{label}</Link>)}<hr /><Link href="/account/don-hang">Đơn hàng của tôi</Link><Link href="/account/sach-thue">Sách đang thuê</Link></nav></aside>
      <div className="account-content-card">
        {activeSection === "profile" ? <><p className="eyebrow">Hồ sơ</p><h1>Thông tin cá nhân</h1><p className="section-subtitle">Cập nhật thông tin để nhà sách hỗ trợ bạn tốt hơn.</p><PersonalProfileForm user={user} /></> : null}
        {activeSection === "security" ? <><p className="eyebrow">Bảo mật</p><h1>Mật khẩu</h1><p className="section-subtitle">Dùng mật khẩu riêng và không chia sẻ với người khác.</p><PasswordSecurityForm /></> : null}
        {activeSection === "addresses" ? <><p className="eyebrow">Giao hàng</p><h1>Địa chỉ nhận hàng</h1><p className="section-subtitle">Quản lý địa chỉ dùng cho các đơn mua sách.</p>

        {addresses === null ? (
          <p className="form-status" role="alert">Không tải được danh sách địa chỉ.</p>
        ) : addresses.length === 0 ? (
          <p className="account-empty">Bạn chưa có địa chỉ nào. Thêm một địa chỉ bên dưới để đặt hàng.</p>
        ) : (
          <ul className="address-book">
            {addresses.map((address) => (
              <li key={address.id} className="address-card">
                <div className="address-card-body">
                  <p className="address-card-name">
                    <strong>{address.fullName}</strong> · {address.phone}
                    {address.isDefault && <Badge tone="muted">Mặc định</Badge>}
                  </p>
                  <p className="address-card-detail">{formatAddress(address)}</p>
                </div>
                <DeleteAddressButton addressId={address.id} label={address.fullName} />
              </li>
            ))}
          </ul>
        )}
        <AddressForm provinces={provinces} />
        <p className="section-subtitle address-form-note">
          Đơn vị hành chính theo cấu trúc hai cấp áp dụng từ 01/07/2025 — chọn tỉnh/thành rồi tới phường/xã,
          không còn cấp quận/huyện.
        </p>
        </> : null}
      </div>
    </section>
  );
}
