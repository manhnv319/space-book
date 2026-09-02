import { SearchIcon } from "@/components/site-icons";

export function SiteSearchForm() {
  return <form action="/sach" className="site-search" role="search"><label className="sr-only" htmlFor="site-search">Tìm sách</label><SearchIcon /><input aria-label="Tìm sách" id="site-search" name="q" placeholder="Tìm tên sách, tác giả, ISBN…" /><button aria-label="Tìm kiếm" type="submit"><SearchIcon /></button></form>;
}
