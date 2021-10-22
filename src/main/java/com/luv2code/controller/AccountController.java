package com.luv2code.controller;

import com.luv2code.entity.User;
import com.luv2code.security.WebUserDetails;
import com.luv2code.service.UserService;
import com.luv2code.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class AccountController {
    @Autowired
    private UserService service;

    @GetMapping("/account")
    public String viewDetail(@AuthenticationPrincipal WebUserDetails loggedUser,
                             Model model) {
        String email = loggedUser.getUsername();
        User user = service.getUserByEmail(email);

        model.addAttribute("user", user);

        return "account_detail";
    }

    @PostMapping("/account/update")
    public String saveDetail(User user, RedirectAttributes redirectAttributes,
                           @AuthenticationPrincipal WebUserDetails loggedUser,
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


            service.updateAccount(user);

        }
        else {
            service.updateAccount(user);

        }

        loggedUser.setFirstName(user.getFirstName());
        loggedUser.setLastName(user.getLastName());
        redirectAttributes.addFlashAttribute("message","The user has been updated successfully!");

        return "redirect:/account";
    }


}
