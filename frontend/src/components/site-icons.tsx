type IconProps = { label?: string };

function iconA11y(label?: string) {
  return label ? { "aria-label": label, role: "img" } : { "aria-hidden": true };
}

export function HeartIcon({ label }: IconProps) {
  return <svg {...iconA11y(label)} viewBox="0 0 24 24"><path d="M20.8 8.6c0 5.4-8.8 10.3-8.8 10.3S3.2 14 3.2 8.6A4.4 4.4 0 0 1 12 6.5a4.4 4.4 0 0 1 8.8 2.1Z" fill="none" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.8" /></svg>;
}

export function CartIcon({ label }: IconProps) {
  return <svg {...iconA11y(label)} viewBox="0 0 24 24"><path d="M3 4h2l2.1 10.1a2 2 0 0 0 2 1.6h7.8a2 2 0 0 0 1.9-1.4L20 8H6" fill="none" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.8" /><circle cx="10" cy="19" r="1.1" fill="currentColor" /><circle cx="18" cy="19" r="1.1" fill="currentColor" /></svg>;
}

export function UserIcon({ label }: IconProps) {
  return <svg {...iconA11y(label)} viewBox="0 0 24 24"><circle cx="12" cy="7" r="3.2" fill="none" stroke="currentColor" strokeWidth="1.8" /><path d="M5.5 20c.5-3.4 2.7-5.2 6.5-5.2s6 1.8 6.5 5.2" fill="none" stroke="currentColor" strokeLinecap="round" strokeWidth="1.8" /></svg>;
}

export function BellIcon({ label }: IconProps) {
  return <svg {...iconA11y(label)} viewBox="0 0 24 24"><path d="M18 10.2a6 6 0 0 0-12 0c0 6-2.2 6.6-2.2 7.8h16.4c0-1.2-2.2-1.8-2.2-7.8ZM9.5 21h5" fill="none" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.8" /></svg>;
}

export function AdminIcon({ label }: IconProps) {
  return <svg {...iconA11y(label)} viewBox="0 0 24 24"><rect height="14" rx="2" width="16" x="4" y="5" fill="none" stroke="currentColor" strokeWidth="1.8" /><path d="M9 5V3h6v2M4 10h16M9 15h6" fill="none" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.8" /></svg>;
}

export function SearchIcon({ label }: IconProps) {
  return <svg {...iconA11y(label)} viewBox="0 0 24 24"><circle cx="10.8" cy="10.8" r="6.5" fill="none" stroke="currentColor" strokeWidth="1.8" /><path d="m16 16 4.5 4.5" fill="none" stroke="currentColor" strokeLinecap="round" strokeWidth="1.8" /></svg>;
}

export function MenuIcon({ label }: IconProps) {
  return <svg {...iconA11y(label)} viewBox="0 0 24 24"><path d="M4 6h16M4 12h16M4 18h16" fill="none" stroke="currentColor" strokeLinecap="round" strokeWidth="1.8" /></svg>;
}

export function TruckIcon({ label }: IconProps) {
  return <svg {...iconA11y(label)} viewBox="0 0 24 24"><path d="M3 6.5h11v8H3zM14 9h3.4l2.6 3v2.5h-6z" fill="none" stroke="currentColor" strokeLinejoin="round" strokeWidth="1.8" /><circle cx="7" cy="17" r="1.7" fill="none" stroke="currentColor" strokeWidth="1.8" /><circle cx="17" cy="17" r="1.7" fill="none" stroke="currentColor" strokeWidth="1.8" /></svg>;
}

export function ChevronLeftIcon() {
  return <svg aria-hidden="true" viewBox="0 0 24 24"><path d="m14.5 5-7 7 7 7" fill="none" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.8" /></svg>;
}

export function ChevronRightIcon() {
  return <svg aria-hidden="true" viewBox="0 0 24 24"><path d="m9.5 5 7 7-7 7" fill="none" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.8" /></svg>;
}

export function ChevronsLeftIcon() {
  return <svg aria-hidden="true" viewBox="0 0 24 24"><path d="m11 5-7 7 7 7M20 5l-7 7 7 7" fill="none" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.8" /></svg>;
}

export function ChevronsRightIcon() {
  return <svg aria-hidden="true" viewBox="0 0 24 24"><path d="m13 5 7 7-7 7M4 5l7 7-7 7" fill="none" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.8" /></svg>;
}

export function StorefrontIcon() {
  return <svg aria-hidden="true" viewBox="0 0 24 24"><path d="M4 10.5V20h16v-9.5M3 6l1.2-3h15.6L21 6v3.2a2.2 2.2 0 0 1-4.1 1.1 2.2 2.2 0 0 1-3.8 0 2.2 2.2 0 0 1-3.8 0A2.2 2.2 0 0 1 5 9.2V6Z" fill="none" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.8" /><path d="M9 20v-5h6v5" fill="none" stroke="currentColor" strokeLinejoin="round" strokeWidth="1.8" /></svg>;
}

export function LogoutIcon() {
  return <svg aria-hidden="true" viewBox="0 0 24 24"><path d="M10 5H5v14h5M14 8l4 4-4 4M18 12H9" fill="none" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.8" /></svg>;
}
