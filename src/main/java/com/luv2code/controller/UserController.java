package com.luv2code.controller;

import com.luv2code.entity.Role;
import com.luv2code.entity.User;
import com.luv2code.error.UserNotFoundException;
import com.luv2code.service.UserService;
import com.luv2code.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService service;

    @GetMapping("/users")
    public String listFirstPage(Model model) {
        return listByPage(1, model, "lastName", "asc", null);
    }

    @GetMapping("/users/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") Integer pageNum, Model model,
                  @Param("sortField") String sortField, @Param("sortDir") String sortDir,
                  @Param("keyword") String keyword) {
        Page<User> page = service.listByPage(pageNum, sortField, sortDir, keyword);
        List<User> listUsers = page.getContent();

        long startCount = (pageNum - 1) * service.USER_PER_PAGE + 1;
        long endCount = startCount +  service.USER_PER_PAGE - 1;

        if(endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("listUsers", listUsers);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("moduleURL", "/users");



        return "users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        User user = new User();
        List<Role> listRoles = service.listRoles();
        model.addAttribute("pageTitle", "Create New User");
        model.addAttribute("user", user);
        model.addAttribute("listRoles", listRoles);
        return "user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes,
                           @RequestParam("image") MultipartFile multipartFile) throws IOException {

        if(user.getRoles().isEmpty()) {
            user.setRoles(null);
        }
        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);

            String uploadDir = "src/main/resources/static/images/user-photos/" + user.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);


            service.saveUser(user);

        }
        else {
            user.setPhotos(null);
            service.saveUser(user);

        }

        redirectAttributes.addFlashAttribute("message","The user has been create successfully!");

        return "redirect:/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        try {
            User user = service.getUserById(id);
            List<Role> listRoles = service.listRoles();

            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Edit User ID("+ id +")");
            model.addAttribute("listRoles", listRoles);

            return "user_form";
        }
        catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message",ex.getMessage());
            return "redirect:/users";

        }

    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id,
                RedirectAttributes redirectAttributes) {

        try {
            service.deleteUserById(id);
            redirectAttributes.addFlashAttribute("message",
                    "The user ID " + id + " has been deleted successfully");
        }
        catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message",ex.getMessage());

        }
        return "redirect:/users";

    }

    @GetMapping("/users/enabled/{id}/{status}")
    public String updateUserEnableStatus(@PathVariable(name = "id") Integer id,
                                         @PathVariable(name = "status") boolean enabled,
                             RedirectAttributes redirectAttributes) {

            service.updateUserEnabledStatus(id, enabled);
            redirectAttributes.addFlashAttribute("message",
                    "The user ID " + id + " has been updated enabled status successfully");

        return "redirect:/users";

    }
}
