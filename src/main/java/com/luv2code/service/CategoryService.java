package com.luv2code.service;

import com.luv2code.entity.Category;
import com.luv2code.entity.User;
import com.luv2code.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    public static final Integer CATEGORY_PER_PAGE = 4;
    @Autowired
    private CategoryRepository categoryRepo;

    public Page<Category> listByPage(Integer pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, CATEGORY_PER_PAGE, sort);

        if(keyword != null) {
            return categoryRepo.findAll(keyword, pageable);
        }

        return categoryRepo.findAll(pageable);
    }
}
