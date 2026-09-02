"use client";

import { useId, useMemo, useRef, useState } from "react";

import { matchesQuery } from "@/lib/vn-address/normalize";

export interface SearchableOption {
  code: string;
  name: string;
}

interface SearchableSelectProps {
  /** Tên field gửi lên form — giá trị là `code`, không phải nhãn. */
  name: string;
  label: string;
  options: SearchableOption[];
  placeholder: string;
  disabled?: boolean;
  required?: boolean;
  onSelect?: (code: string) => void;
}

/**
 * Combobox gõ để lọc, cho danh sách dài như 3.321 phường/xã.
 *
 * Lọc bỏ dấu tiếng Việt (xem `normalize.ts`) nên gõ "ha noi" ra "Hà Nội", và
 * không cần gõ đúng tiền tố "Thành phố"/"Phường".
 *
 * Giá trị thật đi kèm form là `code` trong input ẩn. Ô chữ chỉ để tìm — người
 * dùng gõ gì cũng không tạo ra được một mã hợp lệ, và server vẫn tra lại mã đó
 * trong dữ liệu của mình trước khi lưu.
 */
export function SearchableSelect({
  name, label, options, placeholder, disabled, required, onSelect,
}: SearchableSelectProps) {
  const listId = useId();
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<SearchableOption | null>(null);
  const [open, setOpen] = useState(false);
  const [active, setActive] = useState(0);
  const blurTimer = useRef<number | undefined>(undefined);

  const matches = useMemo(() => {
    const filtered = options.filter((option) => matchesQuery(option.name, query));
    // Danh sách dài thì cắt bớt: không ai cuộn hết 3.321 mục, và render đủ
    // chừng đó node làm ô gõ giật.
    return filtered.slice(0, 50);
  }, [options, query]);

  function choose(option: SearchableOption) {
    setSelected(option);
    setQuery(option.name);
    setOpen(false);
    setActive(0);
    onSelect?.(option.code);
  }

  function onKeyDown(event: React.KeyboardEvent<HTMLInputElement>) {
    if (event.key === "ArrowDown" || event.key === "ArrowUp") {
      event.preventDefault();
      setOpen(true);
      setActive((current) => {
        const next = event.key === "ArrowDown" ? current + 1 : current - 1;
        return Math.max(0, Math.min(next, matches.length - 1));
      });
      return;
    }
    if (event.key === "Enter" && open && matches[active]) {
      event.preventDefault();
      choose(matches[active]);
      return;
    }
    if (event.key === "Escape") setOpen(false);
  }

  return (
    <div className="searchable-select">
      <label htmlFor={`${listId}-input`}>{label}</label>
      <input type="hidden" name={name} value={selected?.code ?? ""} required={required} />
      <input
        id={`${listId}-input`}
        type="text"
        role="combobox"
        aria-expanded={open}
        aria-controls={listId}
        aria-autocomplete="list"
        autoComplete="off"
        disabled={disabled}
        placeholder={placeholder}
        value={query}
        onChange={(event) => {
          setQuery(event.target.value);
          setSelected(null);
          setActive(0);
          setOpen(true);
          onSelect?.("");
        }}
        onFocus={() => setOpen(true)}
        // Chọn bằng chuột phải chạy trước khi danh sách đóng do blur.
        onBlur={() => { blurTimer.current = window.setTimeout(() => setOpen(false), 120); }}
        onKeyDown={onKeyDown}
      />
      {open && !disabled && (
        <ul className="searchable-list" id={listId} role="listbox" aria-label={label}>
          {matches.length === 0 ? (
            <li className="searchable-empty">Không tìm thấy</li>
          ) : (
            matches.map((option, index) => (
              <li key={option.code}>
                <button
                  type="button"
                  role="option"
                  aria-selected={selected?.code === option.code}
                  className={index === active ? "searchable-option is-active" : "searchable-option"}
                  onMouseEnter={() => setActive(index)}
                  onMouseDown={() => window.clearTimeout(blurTimer.current)}
                  onClick={() => choose(option)}
                >
                  {option.name}
                </button>
              </li>
            ))
          )}
        </ul>
      )}
    </div>
  );
}
