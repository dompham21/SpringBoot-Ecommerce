package com.luv2code.repository;

import com.luv2code.entity.Category;
import com.luv2code.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository  extends PagingAndSortingRepository<Category, Integer> {
    @Query("SELECT u FROM Category u WHERE CONCAT(u.id, ' ', u.name, ' ', u.alias )  LIKE %:keyword%")
    public Page<Category> findAll(@Param("keyword") String keyword, @Param("pageable") Pageable pageable);
}
