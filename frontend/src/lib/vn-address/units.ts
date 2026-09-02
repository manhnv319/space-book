import "server-only";

import units from "@/lib/vn-address/data/vn-administrative-units.json";

/** Shape of the bundled dataset — see data/SOURCE.md. */
interface RawWard {
  Code: string;
  FullName: string;
  ProvinceCode: string;
}

interface RawProvince {
  Code: string;
  FullName: string;
  Wards: RawWard[];
}

export interface Province {
  code: string;
  name: string;
}

export interface Ward {
  code: string;
  name: string;
}

const DATA = units as RawProvince[];

const BY_CODE = new Map(DATA.map((province) => [province.Code, province]));

/**
 * 34 tỉnh/thành. Nhỏ (~1 KB) nên gửi thẳng xuống client được.
 *
 * Cả module này là `server-only`: file dữ liệu 220 KB không bao giờ được đóng
 * gói vào bundle trình duyệt — client chỉ nhận danh sách tỉnh, rồi xin phường/xã
 * của đúng tỉnh đã chọn (~100 mục).
 */
export function listProvinces(): Province[] {
  return DATA.map((province) => ({ code: province.Code, name: province.FullName }));
}

export function listWards(provinceCode: string): Ward[] {
  const province = BY_CODE.get(provinceCode);
  if (!province) return [];
  return province.Wards.map((ward) => ({ code: ward.Code, name: ward.FullName }));
}

/**
 * Đối chiếu tên do client gửi lên với dữ liệu thật.
 *
 * Form gửi lên *tên* chứ không phải mã, vì BE lưu địa chỉ dạng chữ. Nhưng tên
 * đến từ trình duyệt thì sửa được, nên không tin trực tiếp: tra lại theo mã và
 * trả về tên chuẩn, sai thì trả null để action từ chối.
 */
export function resolveNames(provinceCode: string, wardCode: string): { province: string; ward: string } | null {
  const province = BY_CODE.get(provinceCode);
  if (!province) return null;
  const ward = province.Wards.find((item) => item.Code === wardCode);
  if (!ward) return null;
  return { province: province.FullName, ward: ward.FullName };
}
