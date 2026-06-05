package com.company.gstbilling.service.impl;

import com.company.gstbilling.dto.request.ProductRequest;
import com.company.gstbilling.dto.response.ProductResponse;
import com.company.gstbilling.entity.Product;
import com.company.gstbilling.exception.ResourceNotFoundException;
import com.company.gstbilling.repository.ProductRepository;
import com.company.gstbilling.service.ProductService;
import com.company.gstbilling.util.GSTCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.debug("Creating product: {}", request.getProductName());

        // Validate GST rate
        GSTCalculator.validateGSTPercentage(request.getGstPercentage());

        Product product = Product.builder()
                .productName(request.getProductName())
                .price(request.getPrice())
                .gstPercentage(request.getGstPercentage())
                .build();

        Product saved = productRepository.save(product);
        log.info("Product created with id: {}", saved.getId());
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return mapToResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.debug("Updating product id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        // Validate GST rate
        GSTCalculator.validateGSTPercentage(request.getGstPercentage());

        product.setProductName(request.getProductName());
        product.setPrice(request.getPrice());
        product.setGstPercentage(request.getGstPercentage());

        Product updated = productRepository.save(product);
        log.info("Product updated id: {}", updated.getId());
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        }
        productRepository.deleteById(id);
        log.info("Product deleted id: {}", id);
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .price(product.getPrice())
                .gstPercentage(product.getGstPercentage())
                .createdAt(product.getCreatedAt())
                .build();
    }
}
