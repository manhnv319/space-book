package com.velstrong.bookstore.infrastructure.adapter.in.rest.cart;

import com.velstrong.bookstore.application.command.cart.AddCartItemCommand;
import com.velstrong.bookstore.application.command.cart.DeleteCartItemCommand;
import com.velstrong.bookstore.application.command.cart.UpdateCartItemCommand;
import com.velstrong.bookstore.application.response.cart.CartResponse;
import com.velstrong.bookstore.domain.port.in.cart.AddCartItemUseCase;
import com.velstrong.bookstore.domain.port.in.cart.DeleteCartItemUseCase;
import com.velstrong.bookstore.domain.port.in.cart.GetCartUseCase;
import com.velstrong.bookstore.domain.port.in.cart.UpdateCartItemUseCase;
import com.velstrong.bookstore.infrastructure.adapter.in.rest.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final GetCartUseCase getCartUseCase;
    private final AddCartItemUseCase addCartItemUseCase;
    private final UpdateCartItemUseCase updateCartItemUseCase;
    private final DeleteCartItemUseCase deleteCartItemUseCase;

    public CartController(GetCartUseCase getCartUseCase,
                          AddCartItemUseCase addCartItemUseCase,
                          UpdateCartItemUseCase updateCartItemUseCase,
                          DeleteCartItemUseCase deleteCartItemUseCase) {
        this.getCartUseCase = getCartUseCase;
        this.addCartItemUseCase = addCartItemUseCase;
        this.updateCartItemUseCase = updateCartItemUseCase;
        this.deleteCartItemUseCase = deleteCartItemUseCase;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@RequestAttribute Long currentUserId) {
        return ResponseEntity.ok(ApiResponse.success(getCartUseCase.getByUserId(currentUserId)));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(@RequestAttribute Long currentUserId,
                                                              @Valid @RequestBody AddCartItemRequest request) {
        AddCartItemCommand command = request.toCommand(currentUserId);
        return ResponseEntity.ok(ApiResponse.success(addCartItemUseCase.addItem(command)));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(@RequestAttribute Long currentUserId,
                                                                 @PathVariable Long itemId,
                                                                 @Valid @RequestBody UpdateCartItemRequest request) {
        UpdateCartItemCommand command = new UpdateCartItemCommand(currentUserId, itemId, request.quantity());
        return ResponseEntity.ok(ApiResponse.success(updateCartItemUseCase.updateItem(command)));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@RequestAttribute Long currentUserId,
                                                         @PathVariable Long itemId) {
        deleteCartItemUseCase.deleteItem(new DeleteCartItemCommand(currentUserId, itemId));
        return ResponseEntity.ok(ApiResponse.success("Item removed", null));
    }
}
