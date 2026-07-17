package com.careerbridge.product.controller;

import com.careerbridge.global.security.AuthenticatedUser;
import com.careerbridge.product.dto.ProductCreateRequest;
import com.careerbridge.product.dto.ProductResponse;
import com.careerbridge.product.dto.ProductSearchRequest;
import com.careerbridge.product.dto.ProductUpdateRequest;
import com.careerbridge.product.entity.Product;
import com.careerbridge.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/api/products")
    public ResponseEntity<List<ProductResponse>> searchProduct(
            @RequestParam(required = false) Long mentorId,
            @RequestParam(required = false) String keyword
            ){
        ProductSearchRequest request = new ProductSearchRequest(mentorId, keyword);

        List<ProductResponse> response = productService
                .searchProducts(request.mentorId(), request.keyword())
                .stream()
                .map(ProductResponse::from).collect(Collectors.toUnmodifiableList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/products")
    public ResponseEntity<ProductResponse> createProduct(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestBody ProductCreateRequest request
            ){
        Product saved = productService.create(
                authenticatedUser,
                request.title(),
                request.description(),
                request.price(),
                request.duration()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProductResponse.from(saved));
    }

    @PutMapping("/api/products/{productId}")
    public ResponseEntity<Void> updateProduct(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long productId,
            @RequestBody ProductUpdateRequest request
            ){
        productService.update(authenticatedUser,
                productId,
                request.title(),
                request.description(),
                request.price(),
                request.duration(),
                request.productStatus());

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/products/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long productId
    ){
        productService.delete(authenticatedUser,productId);

        return ResponseEntity.noContent().build();
    }
}
