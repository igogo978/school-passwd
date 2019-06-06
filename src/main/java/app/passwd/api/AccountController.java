package app.passwd.api;

import app.passwd.model.StudentUser;
import app.passwd.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
public class AccountController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AccountService accountService;

    @RequestMapping(value = "/passwd/admin/api/account/student/empty", method = RequestMethod.GET)
    public List<StudentUser> getEmptyAccount() throws IOException {
        return accountService.getEmptyStudentUser();
    }

    @RequestMapping(value = "/admin/api/account/student/empty", method = RequestMethod.GET)
    public List<StudentUser> getEmptyAccountProxyPass() throws IOException {
        return accountService.getEmptyStudentUser();
    }

    @RequestMapping(value = "/passwd/admin/api/account/student", method = RequestMethod.PUT)
    public List<StudentUser> updateAccount(@RequestBody List<StudentUser> accounts) throws IOException {
        logger.info(String.valueOf(accounts.size()));
        //只單向建立WinAd上的學生帳號
        accountService.createStudentAccounts(accounts);
        return accounts;
    }

    @RequestMapping(value = "/admin/api/account/student", method = RequestMethod.PUT)
    public List<StudentUser> updateAccountProxyPass(@RequestBody List<StudentUser> accounts) throws IOException {
        logger.info(String.valueOf(accounts.size()));
        accountService.createStudentAccounts(accounts);
        return accounts;
    }


}
