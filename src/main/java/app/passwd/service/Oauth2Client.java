package app.passwd.service;

import app.passwd.model.SystemConfig;
import app.passwd.repository.SystemConfigRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Oauth2Client {

    @Autowired
    SystemConfigRepository repository;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private String accesstoken;
    private String state;
    private String accesstoken_endpoint;
    private String clientid;
    private String secret;

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(SystemConfig sysconfig) throws   IOException, URISyntaxException {

        ObjectMapper mapper = new ObjectMapper();

        accesstoken_endpoint = sysconfig.getAccesstoken_endpoint();
        clientid = sysconfig.getClientid();
        secret = sysconfig.getSecret();

        logger.info("4. 進行client_credientials flow 取得token.");

        // Create a new HTTP client
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType mediaType = MediaType.get("application/x-www-form-urlencoded");
        String parameters = String.format("grant_type=client_credentials&client_id=%s&client_secret=%s", clientid, secret);
        RequestBody body = RequestBody.create(parameters, mediaType);

        Request okHttpRequest = new Request.Builder()
                .url(accesstoken_endpoint)
                .post(body)
                .build();

        Response response = okHttpClient.newCall(okHttpRequest).execute();

        this.accesstoken = mapper.readTree(response.body().string()).get("access_token").asText();


    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}

// deprecated : use oltu to get access token
//        OAuthClient client = new OAuthClient(new URLConnectionClient());
//        OAuthClientRequest request =
//                OAuthClientRequest.tokenLocation(accesstoken_endpoint)
//                        .setGrantType(GrantType.CLIENT_CREDENTIALS)
//                        .setClientId(clientid)
//                        .setClientSecret(secret)
//                        .buildBodyMessage();
//
////        grant_type=client_credentials&client_secret=%s&client_id=%s
//        logger.info("parameters");
//        logger.info(request.getBody());
//
//        this.accesstoken = client.accessToken(request, "POST", OAuthJSONAccessTokenResponse.class)
//                .getAccessToken();
