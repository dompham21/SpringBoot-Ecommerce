package com.luv2code.controller;

import com.luv2code.entity.Category;
import com.luv2code.entity.User;
import com.luv2code.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class CategoryController {
    @Autowired
    private CategoryService service;


    @GetMapping("/categories")
    public String listFirstPage(Model model) {
        return listByPage(1, model, "name", "asc", null  );
    }


    @GetMapping("/categories/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") Integer pageNum, Model model,
                             @Param("sortField") String sortField, @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword) {
        Page<Category> page = service.listByPage(pageNum, sortField, sortDir, keyword);
        List<Category> listCategories = page.getContent();

        long startCount = (pageNum - 1) * service.CATEGORY_PER_PAGE + 1;
        long endCount = startCount +  service.CATEGORY_PER_PAGE - 1;

        if(endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("moduleURL", "/categories");


        return "categories";
    }

}
