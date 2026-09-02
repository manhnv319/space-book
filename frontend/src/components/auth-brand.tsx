import Image from "next/image";

export function AuthBrand() {
  return (
    <div aria-label="Sách Nhà" className="auth-brand" role="img">
      <Image alt="" aria-hidden="true" className="auth-logo" height={144} priority src="/brand/sach-nha-logo.png" width={144} />
    </div>
  );
}
