package com.velstrong.bookstore.infrastructure.adapter.in.rest.address;

import com.velstrong.bookstore.application.command.address.CreateAddressCommand;
import com.velstrong.bookstore.application.command.address.UpdateAddressCommand;
import com.velstrong.bookstore.application.response.address.AddressResponse;
import com.velstrong.bookstore.domain.port.in.address.*;
import com.velstrong.bookstore.infrastructure.adapter.in.rest.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private final CreateAddressUseCase createAddressUseCase;
    private final UpdateAddressUseCase updateAddressUseCase;
    private final DeleteAddressUseCase deleteAddressUseCase;
    private final GetAllAddressesUseCase getAllAddressesUseCase;

    public AddressController(CreateAddressUseCase createAddressUseCase,
                              UpdateAddressUseCase updateAddressUseCase,
                              DeleteAddressUseCase deleteAddressUseCase,
                              GetAllAddressesUseCase getAllAddressesUseCase) {
        this.createAddressUseCase = createAddressUseCase;
        this.updateAddressUseCase = updateAddressUseCase;
        this.deleteAddressUseCase = deleteAddressUseCase;
        this.getAllAddressesUseCase = getAllAddressesUseCase;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAll(@RequestAttribute Long currentUserId) {
        return ResponseEntity.ok(ApiResponse.success(getAllAddressesUseCase.getAllByUserId(currentUserId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> create(@RequestAttribute Long currentUserId,
                                                                @Valid @RequestBody AddressRequest request) {
        CreateAddressCommand command = request.toCreateCommand(currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createAddressUseCase.create(command)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> update(@RequestAttribute Long currentUserId,
                                                                @PathVariable Long id,
                                                                @Valid @RequestBody AddressRequest request) {
        UpdateAddressCommand command = request.toUpdateCommand(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(updateAddressUseCase.update(command)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@RequestAttribute Long currentUserId,
                                                     @PathVariable Long id) {
        deleteAddressUseCase.delete(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Address deleted", null));
    }
}
