package app.passwd.controller;

import app.passwd.service.UserLoginService;
import app.passwd.service.UseritemService;
import app.passwd.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class UploadController {
    private final StorageService storageService;
    private final Logger logger = LoggerFactory.getLogger(UserhomeController.class);

    @Autowired
    UserLoginService userloginservice;

    @Autowired
    UseritemService useritemService;

    @Autowired
    public UploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/useritem/upload")
    public String uploadPage() {
        return "upload";
    }

    @PostMapping("/useritem/upload")
    public String handleFileUpload(@RequestParam(value = "file", required = true) MultipartFile file,
                                   RedirectAttributes redirectAttributes) throws IOException {


        String username = userloginservice.getUser().getUsername();
        useritemService.save(username, file);

        logger.info(("upload image sucessfully"));


        return "redirect:/userhome";
    }

}
