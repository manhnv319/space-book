package com.velstrong.bookstore.infrastructure.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.module.SimpleModule;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Gửi mọi mốc thời gian ra ngoài kèm múi giờ.
 *
 * Máy chủ chạy UTC còn người đọc ở +07. Một {@code LocalDateTime} serialize mặc
 * định thành "2026-07-27T05:42:00" — không mang múi giờ, nên trình duyệt hiểu đó
 * là giờ địa phương và mọi mốc lệch đi đúng bằng chênh lệch múi giờ. Đó chính là
 * lỗi từng làm đơn vừa tạo đã báo quá hạn chuyển khoản.
 *
 * Sửa ở một chỗ thay vì đổi kiểu từng trường trong mười DTO: một DTO thêm sau
 * này cũng tự đúng, không phụ thuộc vào việc người viết có nhớ hay không.
 *
 * <p><b>Chỉ đổi chiều ghi ra.</b> Chiều đọc vào giữ nguyên, nên request còn gửi
 * {@code LocalDateTime} không mang múi giờ (hiệu lực voucher) vẫn hoạt động.
 *
 * <p><b>Không đụng tới {@link java.time.LocalDate}.</b> Ngày hẹn trả sách hay
 * ngày hết hạn gói là một *ngày trên lịch*, không phải một thời điểm — gắn múi
 * giờ vào đó sẽ đẩy nó sang ngày khác với người ở múi giờ lệch.
 */
@Configuration
public class JsonTimeConfig {

    @Bean
    public JsonMapperBuilderCustomizer serializeLocalDateTimeWithZone() {
        SimpleModule module = new SimpleModule("LocalDateTimeAsInstant");
        module.addSerializer(LocalDateTime.class, new ValueSerializer<LocalDateTime>() {
            @Override
            public void serialize(LocalDateTime value, JsonGenerator generator, SerializationContext context) {
                generator.writeString(value.atZone(ZoneId.systemDefault()).toInstant().toString());
            }
        });
        return builder -> builder.addModule(module);
    }
}
