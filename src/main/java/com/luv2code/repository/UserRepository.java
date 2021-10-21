package com.luv2code.repository;

import com.luv2code.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.email = :email" )
    public User getUserByEmail(@Param("email") String email);

    @Query("SELECT count(u.id) FROM User u WHERE u.id = :id")
    public Long  countUserById (@Param("id") Integer id);

    @Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ', u.firstName, ' ', u.lastName,' ', u.email )  LIKE %:keyword%")
    public Page<User> findAll(@Param("keyword") String keyword, @Param("pageable")Pageable pageable);

    @Query("UPDATE User u SET u.enabled = :enabled WHERE u.id = :id")
    @Modifying
    public void updateEnableStatus(@Param("id") Integer id, @Param("enabled") boolean enabled);
}
