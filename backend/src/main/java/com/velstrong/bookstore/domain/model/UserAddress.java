package com.velstrong.bookstore.domain.model;

public class UserAddress {

    private final Long id;
    private final Long userId;
    private String fullName;
    private String phone;
    private String province;
    private String district;
    private String ward;
    private String addressDetail;
    private Boolean isDefault;

    private UserAddress(Long id, Long userId, String fullName, String phone,
                        String province, String district, String ward,
                        String addressDetail, Boolean isDefault) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.addressDetail = addressDetail;
        this.isDefault = isDefault;
    }

    public static UserAddress create(Long userId, String fullName, String phone,
                                     String province, String district, String ward,
                                     String addressDetail, boolean isDefault) {
        return new UserAddress(null, userId, fullName, phone, province, district, ward, addressDetail, isDefault);
    }

    public static UserAddress reconstitute(Long id, Long userId, String fullName, String phone,
                                           String province, String district, String ward,
                                           String addressDetail, Boolean isDefault) {
        return new UserAddress(id, userId, fullName, phone, province, district, ward, addressDetail, isDefault);
    }

    public void update(String fullName, String phone, String province, String district,
                       String ward, String addressDetail) {
        this.fullName = fullName;
        this.phone = phone;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.addressDetail = addressDetail;
    }

    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getProvince() { return province; }
    public String getDistrict() { return district; }
    public String getWard() { return ward; }
    public String getAddressDetail() { return addressDetail; }
    public Boolean getIsDefault() { return isDefault; }
}
