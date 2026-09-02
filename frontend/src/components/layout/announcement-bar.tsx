import { ANNOUNCEMENTS } from "@/lib/home/home-content-config";

export function AnnouncementBar() {
  return <aside className="announcement-bar" aria-label="Thông báo"><p>{ANNOUNCEMENTS[0]}</p></aside>;
}
