package com.velstrong.bookstore.application.service.order;

import com.velstrong.bookstore.application.response.order.OrderResponse;
import com.velstrong.bookstore.domain.model.Book;
import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.OrderItem;
import com.velstrong.bookstore.domain.port.out.BookRepository;
import com.velstrong.bookstore.domain.port.out.OrderItemRepository;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Gắn vài sản phẩm đầu vào mỗi đơn trong danh sách.
 *
 * Đơn chỉ có mã và số tiền thì mọi dòng trông như nhau — mã đơn là ULID, khách
 * không đọc ra được gì. Cái người ta nhận ra là bìa sách và tên sách.
 *
 * Nạp theo lô: một trang đơn tốn hai truy vấn (sản phẩm của các đơn, rồi sách),
 * không phải hai truy vấn mỗi đơn.
 */
@Component
public class OrderItemPreviewLookup {

    /** Đủ để nhận ra đơn; nhiều hơn thì dòng dài mà không thêm thông tin. */
    private static final int PREVIEW_LIMIT = 3;

    private final OrderItemRepository orderItems;
    private final BookRepository books;

    public OrderItemPreviewLookup(OrderItemRepository orderItems, BookRepository books) {
        this.orderItems = orderItems;
        this.books = books;
    }

    public List<OrderResponse> enrich(List<Order> orders) {
        if (orders.isEmpty()) return List.of();

        Map<Long, List<OrderItem>> itemsByOrder = new LinkedHashMap<>();
        for (Order order : orders) {
            itemsByOrder.put(order.getId(), orderItems.findByOrderId(order.getId()));
        }

        List<Long> bookIds = itemsByOrder.values().stream()
                .flatMap(List::stream).map(OrderItem::getBookId).filter(Objects::nonNull).distinct().toList();
        Map<Long, Book> booksById = books.findByIds(bookIds).stream()
                .collect(Collectors.toMap(Book::getId, Function.identity(), (a, b) -> a));

        return orders.stream()
                .map(order -> OrderResponse.from(order, preview(itemsByOrder.get(order.getId()), booksById)))
                .toList();
    }

    private List<OrderResponse.ItemPreview> preview(List<OrderItem> items, Map<Long, Book> booksById) {
        if (items == null) return List.of();
        return items.stream().limit(PREVIEW_LIMIT).map(item -> {
            Book book = item.getBookId() == null ? null : booksById.get(item.getBookId());
            return new OrderResponse.ItemPreview(item.getBookId(),
                    book == null ? null : book.getTitle(),
                    book == null ? null : book.getImageUrl(),
                    item.getItemType(), item.getQuantity());
        }).toList();
    }
}
