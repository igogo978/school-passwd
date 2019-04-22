package app.passwd.controller;

import app.passwd.repository.SystemConfigRepository;
import app.passwd.service.Oauth2Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.ExecutionException;

@RestController
public class IndexController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Oauth2Client client;

    @Autowired
    SystemConfigRepository repository;


    private String clientid;
    private String secret;
    private String authorize_endpoint;


    @GetMapping("/")
    //導到app頁面
    public RedirectView index() throws URISyntaxException, IOException, ParseException, ExecutionException, InterruptedException {
        return new RedirectView("/passwd/");
    }

    @GetMapping("/passwd")
    public RedirectView indexPasswd(RedirectAttributes attributes) throws URISyntaxException, IOException, ParseException, ExecutionException, InterruptedException, NoSuchAlgorithmException {

        clientid = repository.findBySn(1).getClientid();
        authorize_endpoint = repository.findBySn(1).getAuthorize_endpoint();

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(String.valueOf(new Random().nextInt(9999999)).getBytes(StandardCharsets.UTF_8));
        String encoded  = Base64.getEncoder().encodeToString(hash);

        client.setState(String.valueOf(encoded));

        attributes.addAttribute("client_id", clientid);
        attributes.addAttribute("response_type", "code");
        attributes.addAttribute("state", client.getState());

        logger.info(String.format("1.需請求取得認證授權,轉到 %s", authorize_endpoint));


        return new RedirectView(authorize_endpoint);


    }


}
