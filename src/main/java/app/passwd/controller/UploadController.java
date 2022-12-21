package app.passwd.controller;

import app.passwd.service.UserAudioitemService;
import app.passwd.service.UserLoginService;
import app.passwd.service.UseritemService;
import app.passwd.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Locale;

@Controller
public class UploadController {
    private final StorageService storageService;
    private final Logger logger = LoggerFactory.getLogger(UserhomeController.class);

    @Autowired
    UserLoginService userloginservice;

    @Autowired
    UseritemService useritemService;

    @Autowired
    UserAudioitemService userAudioitemService;
    @Autowired
    GridFsTemplate gridFsTemplate;


    @Autowired
    public UploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/useritem/upload")
    public String uploadPage() {
        return "upload";
    }


    @PostMapping("/useraudioitem/upload")
    public String handleAudioFileUpload(@RequestParam(value = "file", required = true) MultipartFile file,
                                        RedirectAttributes redirectAttributes) throws IOException, InterruptedException {

        if (userloginservice.getUser() == null) {
            return "redirect:/useraudio";
        }
//        logger.info(file.getContentType().toLowerCase(Locale.ROOT));
        logger.info(file.getOriginalFilename() + ":" + file.getSize());
        String username = userloginservice.getUser().getUsername();
        if (file.getContentType().toLowerCase(Locale.ROOT).equals("audio/mpeg") && file.getSize() < 11000000) {
            userAudioitemService.saveAudio(username, file);
        } else {

            logger.info(file.getOriginalFilename() + ":" + file.getSize());
            return "redirect:/useraudio?code=99";
        }

        return "redirect:/useraudio";
    }


    @PostMapping("/useritem/upload2")
    public String handleFileUpload(@RequestParam(value = "file", required = true) MultipartFile file,
                                   @RequestParam(defaultValue = "3") int days, RedirectAttributes redirectAttributes) throws IOException {

        if (userloginservice.getUser() == null) {
            return "redirect:/userhome2";
        }

        //video/mp4,  image/jpeg, image/png
        String username = userloginservice.getUser().getUsername();
        logger.info(file.getContentType().toLowerCase(Locale.ROOT));
        if (file.getContentType().toLowerCase(Locale.ROOT).equals("video/mp4")) {
            useritemService.saveVideo(username, file, days);
        }
        if (file.getContentType().toLowerCase(Locale.ROOT).equals("image/jpeg") || file.getContentType().toLowerCase(Locale.ROOT).equals("image/png")) {
            useritemService.saveImage(username, file, days);
        }

        logger.info(("upload item sucessfully"));

        return "redirect:/userhome2";
    }

    @PostMapping("/useritem/upload")
    public String handleFileUpload(@RequestParam(value = "file", required = true) MultipartFile file,
                                   RedirectAttributes redirectAttributes) throws IOException {

        if (userloginservice.getUser() == null) {
            return "redirect:/userhome";
        }
        //video/mp4,  image/jpeg, image/png
        String username = userloginservice.getUser().getUsername();
        logger.info(file.getContentType().toLowerCase(Locale.ROOT));
        if (file.getContentType().toLowerCase(Locale.ROOT).equals("video/mp4")) {
            useritemService.saveVideo(username, file);
        }
        if (file.getContentType().toLowerCase(Locale.ROOT).equals("image/jpeg") || file.getContentType().toLowerCase(Locale.ROOT).equals("image/png")) {
            useritemService.saveImage(username, file);
        }

        logger.info(("upload item sucessfully"));

        return "redirect:/userhome";
    }




}
