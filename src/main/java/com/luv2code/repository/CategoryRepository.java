package com.luv2code.repository;

import com.luv2code.entity.Category;
import com.luv2code.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CategoryRepository  extends PagingAndSortingRepository<Category, Integer> {
    @Query("SELECT c FROM Category c WHERE CONCAT(c.id, ' ', c.name, ' ', c.alias )  LIKE %:keyword%")
    public Page<Category> findAll(@Param("keyword") String keyword, @Param("pageable") Pageable pageable);

    @Query("SELECT count(c.id) FROM Category c WHERE c.id = :id")
    public Long countById (@Param("id") Integer id);


    @Query("UPDATE Category c SET c.enabled = :enabled WHERE c.id = :id")
    @Modifying
    @Transactional
    public void updateEnableStatus(@Param("id") Integer id, @Param("enabled") boolean enabled);
}
