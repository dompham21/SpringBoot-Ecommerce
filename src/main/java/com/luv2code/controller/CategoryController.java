package com.luv2code.controller;

import com.luv2code.entity.Category;
import com.luv2code.entity.Role;
import com.luv2code.entity.User;
import com.luv2code.error.CategoryNotFoundException;
import com.luv2code.error.UserNotFoundException;
import com.luv2code.service.CategoryService;
import com.luv2code.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
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

    @GetMapping("/categories/new")
    public String newCategory(Model model) {
        Category category = new Category();
        List<Category> listCategories =  service.listCategoriesUsedInForm();

        model.addAttribute("pageTitle", "Create New Category");
        model.addAttribute("category", category);
        model.addAttribute("listCategories", listCategories);

        return "category_form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(Category category,
                               @RequestParam("fileImage") MultipartFile multipartFile,
                               RedirectAttributes ra) throws IOException {

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());


                category.setImage(fileName);

            Category savedCategory = service.save(category);
            String uploadDir = "../category-images/" + savedCategory.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            service.save(category);
        }

        ra.addFlashAttribute("messageSuccess", "The category has been saved successfully.");
        return "redirect:/categories";
    }


    @GetMapping("/categories/edit/{id}")
    public String editCategory (@PathVariable(name = "id") Integer id,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        try {
            Category category = service.findCategoryById(id);
            List<Category> listCategories =  service.listCategoriesUsedInForm();

            model.addAttribute("category", category);
            model.addAttribute("pageTitle", "Edit Category ID("+ id +")");
            model.addAttribute("listCategories", listCategories);


            return "category_form";
        }
        catch (CategoryNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message",ex.getMessage());
            return "redirect:/categories";

        }

    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable(name = "id") Integer id,
                             RedirectAttributes redirectAttributes) {

        try {
            service.deleteCategoryById(id);
            redirectAttributes.addFlashAttribute("message",
                    "The category ID " + id + " has been deleted successfully");
        }
        catch (CategoryNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message",ex.getMessage());

        }
        return "redirect:/categories";

    }

    @GetMapping("/categories/enabled/{id}/{status}")
    public String updateCategoriesEnableStatus(@PathVariable(name = "id") Integer id,
                                         @PathVariable(name = "status") boolean enabled,
                                         RedirectAttributes redirectAttributes) {

        service.updateCategoryEnabledStatus(id, enabled);
        redirectAttributes.addFlashAttribute("message",
                "The category ID " + id + " has been updated enabled status successfully");

        return "redirect:/categories";

    }
}
