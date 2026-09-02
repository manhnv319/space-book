import Image from "next/image";

export function AuthBrand() {
  return (
    <div aria-label="VelstrongBook" className="auth-brand" role="img">
      <Image alt="" aria-hidden="true" className="auth-wordmark" height={26} priority src="/brand/velstrongbook-wordmark.png" width={160} />
    </div>
  );
}
