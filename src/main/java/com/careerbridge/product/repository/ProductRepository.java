package com.careerbridge.product.repository;

import com.careerbridge.product.entity.Product;
import com.careerbridge.product.entity.ProductStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository {
    @Query("""
        select p
        from Product p
        join p.mentor m
        where p.productStatus = :status
          and (:mentorId is null or m.id = :mentorId)
          and (
            :keyword is null
            or lower(p.title) like lower(concat('%', :keyword, '%'))
            or lower(m.mentorName) like lower(concat('%', :keyword, '%'))
          )
        order by p.createdAt desc
    """)
    List<Product> searchProducts(
            @Param("mentorId") Long mentorId,
            @Param("keyword") String keyword,
            @Param("status") ProductStatus status
    );

    Product save(Product product);

    Optional<Product> findById(Long id);

    void delete(Product product);
}
