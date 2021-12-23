package app.passwd.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Map;

@Entity
public class SystemConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;


    private Integer sn;
    private String clientid;
    private String secret;

    private String authorize_endpoint;
    private String changepasswd_endpoint;
    private String accesstoken_endpoint;
    private String semesterdata_endpoint;
    private String cwd;

//    private boolean isLearningAccount;
    private boolean isSyncLdap;


    public Integer getSn() {
        return sn;
    }

    public void setSn(Integer sn) {
        this.sn = sn;
    }

    public boolean isSyncLdap() {
        return isSyncLdap;
    }

    public void setSyncLdap(boolean syncLdap) {
        isSyncLdap = syncLdap;
    }

//    public boolean isLearningAccount() {
//        return isLearningAccount;
//    }

//    public void setLearningAccount(boolean learningAccount) {
//        isLearningAccount = learningAccount;
//    }

    public String getSemesterdata_endpoint() {
        return semesterdata_endpoint;
    }

    public void setSemesterdata_endpoint(String semesterdata_endpoint) {
        this.semesterdata_endpoint = semesterdata_endpoint;
    }

    public String getAccesstoken_endpoint() {
        return accesstoken_endpoint;
    }

    public void setAccesstoken_endpoint(String accesstoken_endpoint) {
        this.accesstoken_endpoint = accesstoken_endpoint;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }


    public String getAuthorize_endpoint() {
        return authorize_endpoint;
    }

    public void setAuthorize_endpoint(String authorize_endpoint) {
        this.authorize_endpoint = authorize_endpoint;
    }

    public String getChangepasswd_endpoint() {
        return changepasswd_endpoint;
    }

    public void setChangepasswd_endpoint(String changepasswd_endpoint) {
        this.changepasswd_endpoint = changepasswd_endpoint;
    }

    public String getCwd() {
        return cwd;
    }

    public void setCwd(String cwd) {
        this.cwd = cwd;
    }
}
