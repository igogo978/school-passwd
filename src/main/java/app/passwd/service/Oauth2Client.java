package app.passwd.service;

import app.passwd.model.SystemConfig;
import app.passwd.repository.SystemConfigRepository;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Oauth2Client {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SystemConfigRepository repository;

    private String accesstoken;
    private String state;
    private String accesstoken_endpoint;
    private String clientid;
    private String secret;

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(SystemConfig sysconfig) throws OAuthSystemException, OAuthProblemException {
//        this.accesstoken = accesstoken;


        accesstoken_endpoint = sysconfig.getAccesstoken_endpoint();
        clientid = sysconfig.getClientid();
        secret = sysconfig.getSecret();

        logger.info("4. 進行client_credientials flow 取得token.");
        OAuthClient client = new OAuthClient(new URLConnectionClient());
        OAuthClientRequest request =
                OAuthClientRequest.tokenLocation(accesstoken_endpoint)
                        .setGrantType(GrantType.CLIENT_CREDENTIALS)
                        .setClientId(clientid)
                        .setClientSecret(secret)
                        .buildBodyMessage();

        this.accesstoken = client.accessToken(request, "POST", OAuthJSONAccessTokenResponse.class)
                .getAccessToken();

    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
