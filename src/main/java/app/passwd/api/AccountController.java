package app.passwd.api;

import app.passwd.model.SchoolUser;
import app.passwd.repository.LdapRepository;
import app.passwd.service.AccountService;
import app.passwd.service.LdapTools;
import app.passwd.service.UserLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AccountController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AccountService accountService;

    @Autowired
    UserLoginService userLoginService;

    @Autowired
    LdapRepository ldapRepository;

    @Autowired
    LdapTools ldapTools;

    @RequestMapping(value = "/api/staffusers", method = RequestMethod.GET)
    public List<SchoolUser> getAllStaffUsers() throws IOException {
        return accountService.getAllStaffUsers();
    }


//    @RequestMapping(value = "/passwd/admin/api/account/all", method = RequestMethod.GET)
//    public String getRawData() throws IOException {
//        return accountService.getRawData();
//    }

    @RequestMapping(value = "/passwd/api/account/student", method = RequestMethod.GET)
    public List<SchoolUser> getEmptyAccount() throws IOException {
        //empty account on win ad
        if (userLoginService.getUser().getUsername().equals(ldapRepository.findBySn(1).getAccountManager())) {
            return accountService.getEmptyStudentUser();

        }
        logger.info("login user: " + userLoginService.getUser().getUsername());

        return new ArrayList<SchoolUser>();
    }

    @RequestMapping(value = "/api/account/student", method = RequestMethod.GET)
    public List<SchoolUser> getEmptyAccountProxyPass() throws IOException {
        //empty account on win ad
        if (userLoginService.getUser().getUsername().equals(ldapRepository.findBySn(1).getAccountManager())) {
            return accountService.getEmptyStudentUser();

        }
        logger.info("login user: " + userLoginService.getUser().getUsername());
        return new ArrayList<SchoolUser>();
    }

    @RequestMapping(value = "/passwd/api/account/student", method = RequestMethod.PUT)
    public List<SchoolUser> updateAccount(@RequestBody List<SchoolUser> accounts) throws IOException {
        logger.info(String.valueOf(accounts.size()));
        //只單向建立WinAd上的學生帳號
        accountService.createStudentAccounts(accounts);
        return accounts;
    }

    @RequestMapping(value = "/api/account/student", method = RequestMethod.PUT)
    public List<SchoolUser> updateAccountProxyPass(@RequestBody List<SchoolUser> accounts) throws IOException {
        logger.info(String.valueOf(accounts.size()));
        accountService.createStudentAccounts(accounts);
        return accounts;
    }

    @RequestMapping(value = "/passwd/api/account/staff", method = RequestMethod.GET)
    public List<SchoolUser> getEmptyStaffAccount() throws IOException {
        return getEmptyStaffAccountProxyPass();
    }

    @RequestMapping(value = "/api/account/staff", method = RequestMethod.GET)
    public List<SchoolUser> getEmptyStaffAccountProxyPass() throws IOException {
        //empty account on win ad
        logger.info("login user: " + userLoginService.getUser().getUsername());
        if (userLoginService.getUser().getUsername().equals(ldapRepository.findBySn(1).getAccountManager())) {
            return accountService.getEmptyUsers("staff");

        }
        return new ArrayList<SchoolUser>();
    }

    @RequestMapping(value = "/passwd/api/account/staff", method = RequestMethod.PUT)
    public List<SchoolUser> updateStaffAccount(@RequestBody List<SchoolUser> accounts) throws IOException {
        logger.info(String.valueOf(accounts.size()));
        accountService.createAccounts(accounts);
        return accounts;
    }


    @RequestMapping(value = "/api/account/staff", method = RequestMethod.PUT)
    public List<SchoolUser> updateStaffAccountProxyPass(@RequestBody List<SchoolUser> accounts) throws IOException {
        logger.info(String.valueOf(accounts.size()));
        accountService.createAccounts(accounts);
        return accounts;
    }


}
