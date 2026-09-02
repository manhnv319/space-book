"use client";

import Link from "next/link";

export default function GlobalError() {
  return (
    <section className="page-section placeholder" role="alert">
      <p className="eyebrow">Có sự cố</p>
      <h1>Không thể tải trang.</h1>
      <p className="lead">Hãy thử lại sau hoặc quay về trang chủ.</p>
      <Link className="text-link" href="/">Về trang chủ</Link>
    </section>
  );
}
