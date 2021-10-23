package com.luv2code.service;

import com.luv2code.entity.Category;
import com.luv2code.error.CategoryNotFoundException;
import com.luv2code.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public List<Category> listCategoriesUsedInForm() {
        List<Category> categoriesUsedInForm = new ArrayList<>();
        Iterable<Category> categoriesInDB = categoryRepo.findAll();
        for(Category category : categoriesInDB) {

            if(category.getParent() == null) {
                categoriesUsedInForm.add(Category.copyIdAndName(category.getId(),category.getName()));

                Set<Category> children = category.getChildren();
                for(Category subCategory : children) {
                    categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), "--" + subCategory.getName()));
                    listChildren(categoriesUsedInForm, subCategory, 1);
                }
            }
        }
        return  categoriesUsedInForm;
    }

    private void listChildren(List<Category> categoriesUsedInForm, Category parent, int subLevel ) {
        int newSubLevel = subLevel  + 1;
        Set<Category> children = parent.getChildren();

        for(Category subCategory : children) {
            String name = "";
            for(int i=0;i<newSubLevel;i++) {
                name += "--";
            }
            categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(),name + subCategory.getName()));
            listChildren(categoriesUsedInForm, subCategory, newSubLevel);
        }
    }

    public Category save(Category category) {
        return categoryRepo.save(category);
    }

    public Category findCategoryById(Integer id)  throws  CategoryNotFoundException{
        try {
            return categoryRepo.findById(id).get();
        }
        catch (NoSuchElementException ex) {
            throw new CategoryNotFoundException("Could not find any category with ID " + id);
        }

    }

    public void deleteCategoryById(Integer id) throws CategoryNotFoundException {
        Long countById = categoryRepo.countById(id);

        if (countById == null || countById == 0) {
            throw new CategoryNotFoundException("Could not find any category with ID " + id);
        }
        categoryRepo.deleteById(id);
    }

    public void updateCategoryEnabledStatus(Integer id, boolean enabled) {

        categoryRepo.updateEnableStatus(id, enabled);
    }
//    private SortedSet<Category> sortSubCategories(Set<Category> children) {
//        return sortSubCategories(children, "asc");
//    }
//
//    private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
//        SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
//            @Override
//            public int compare(Category cat1, Category cat2) {
//                if (sortDir.equals("asc")) {
//                    return cat1.getName().compareTo(cat2.getName());
//                } else {
//                    return cat2.getName().compareTo(cat1.getName());
//                }
//            }
//        });
//
//        sortedChildren.addAll(children);
//
//        return sortedChildren;
//    }
//
}
