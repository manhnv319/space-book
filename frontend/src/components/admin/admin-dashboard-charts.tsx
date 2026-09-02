type DashboardChartValue = {
  label: string;
  value: number | null;
};

function DashboardBarChart({ values }: Readonly<{ values: DashboardChartValue[] }>) {
  const availableValues = values.map(({ value }) => value).filter((value): value is number => value !== null);
  const max = Math.max(...availableValues, 1);

  return <div aria-label="Biểu đồ cột số liệu quản trị" className="admin-bar-chart" role="img">
    {values.map(({ label, value }) => <div className="admin-bar-row" key={label}>
      <div className="admin-bar-label"><span>{label}</span><strong>{value ?? "—"}</strong></div>
      <div aria-hidden="true" className="admin-bar-track"><span className="admin-bar-fill" style={{ width: value === null ? "0%" : `${Math.max((value / max) * 100, value > 0 ? 4 : 0)}%` }} /></div>
    </div>)}
  </div>;
}

export function AdminDashboardCharts({
  draftCount,
  publishedCount,
  featuredCount,
  bestsellerCount,
  overdueCount,
  transferCount,
  supportCount,
}: Readonly<{
  draftCount: number | null;
  publishedCount: number | null;
  featuredCount: number | null;
  bestsellerCount: number | null;
  overdueCount: number | null;
  transferCount: number | null;
  supportCount: number | null;
}>) {
  return <div className="admin-chart-grid">
    <section className="admin-chart-card">
      <div className="admin-chart-header"><div><p className="eyebrow">Nội dung & trưng bày</p><h2>Phân bổ hiện tại</h2></div><span className="admin-chart-caption">Số lượng</span></div>
      <DashboardBarChart values={[{ label: "Bài viết nháp", value: draftCount }, { label: "Bài viết đã đăng", value: publishedCount }, { label: "Sách nổi bật", value: featuredCount }, { label: "Sách bán chạy", value: bestsellerCount }]} />
    </section>
    <section className="admin-chart-card">
      <div className="admin-chart-header"><div><p className="eyebrow">Vận hành</p><h2>Việc cần xử lý</h2></div><span className="admin-chart-caption">Số lượng</span></div>
      <DashboardBarChart values={[{ label: "Giao dịch cần đối soát", value: transferCount }, { label: "Phiếu thuê quá hạn", value: overdueCount }, { label: "Hội thoại hỗ trợ", value: supportCount }]} />
    </section>
  </div>;
}
