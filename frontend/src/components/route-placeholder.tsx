import Link from "next/link";

type RoutePlaceholderProps = {
  title: string;
  description: string;
  nextPhase: string;
};

export function RoutePlaceholder({ title, description, nextPhase }: RoutePlaceholderProps) {
  return (
    <section className="page-section placeholder" aria-labelledby="page-title">
      <p className="eyebrow">Nền tảng mới</p>
      <h1 id="page-title">{title}</h1>
      <p className="lead">{description}</p>
      <p className="status-note">
        Chức năng này chưa được triển khai. Dự kiến thực hiện ở {nextPhase}.
      </p>
      <Link className="text-link" href="/">Về trang chủ</Link>
    </section>
  );
}
