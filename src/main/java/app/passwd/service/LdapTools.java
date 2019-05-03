package app.passwd.service;


import app.passwd.ldap.model.OUAttributesMapper;
import app.passwd.ldap.model.OrganizationalUnit;
import app.passwd.ldap.model.PersonAttributesMapper;
import app.passwd.ldap.model.User;
import app.passwd.model.LdapClient;
import app.passwd.model.Role;
import app.passwd.repository.LdapRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;

import javax.naming.Name;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.LdapName;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Service
public class LdapTools {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    LdapRepository ldaprepository;

    @Autowired
    UserLoginService userloginservice;


    private LdapTemplate initLDAPConnect() {
        LdapClient ldapclient = ldaprepository.findBySn(1);
        String url = String.format("ldap://%s:%s", ldapclient.getLdapserver(), ldapclient.getLdapport());
        String basedn = ldapclient.getBasedn();
        String rootdn = ldapclient.getRootdn();
        String rootpassword = ldapclient.getPasswd();

        LdapContextSource source = new LdapContextSource();
        source.setUrl(url);
        source.setBase(basedn);
        source.setUserDn(rootdn);
        source.setPassword(rootpassword);
        source.afterPropertiesSet();

        LdapTemplate ldapTemplate = new LdapTemplate(source);
        ldapTemplate.setIgnorePartialResultException(true);
        return ldapTemplate;
    }


    public Boolean isUserExist(String username) {
        LdapClient ldapclient = ldaprepository.findBySn(1);

        String url = String.format("ldap://%s:%s", ldapclient.getLdapserver(), ldapclient.getLdapport());
        String basedn = ldapclient.getBasedn();
        String rootdn = ldapclient.getRootdn();
        String rootpassword = ldapclient.getPasswd();

        LdapContextSource source = new LdapContextSource();
        source.setUrl(url);
        source.setBase(basedn);
        source.setUserDn(rootdn);
        source.setPassword(rootpassword);
        source.afterPropertiesSet();

//        LdapTemplate ldaptemplate = new LdapTemplate(source);
        LdapTemplate ldapTemplate = initLDAPConnect();


        List<User> users = ldapTemplate.search(
                query().where("objectclass").is("person")
                        .and("uid").is(username),
                new PersonAttributesMapper());
        if (users.size() == 0) {

            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }


    public User findByUid(String username) {
        logger.info("loggin user:" + username);

        LdapTemplate ldapTemplate = initLDAPConnect();
        return ldapTemplate.findOne(query().where("uid").is(username), User.class);

    }

    public void updateUserPassword(String username, String password, String role) {


        LdapTemplate ldaptemplate = initLDAPConnect();

        //find user dn
        User user = findByUid(username);


        Attribute attr = new BasicAttribute("userPassword", digestSHA(password));
        ModificationItem item = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);

        ldaptemplate.modifyAttributes(user.getDn(), new ModificationItem[]{item});
    }

    public Boolean isOuExist(String ou) {
        LdapTemplate ldapTemplate = initLDAPConnect();
        List<OrganizationalUnit> ous = ldapTemplate.search(
                query().where("objectclass").is("organizationalUnit")
                        .and("ou").is(ou),

                new OUAttributesMapper());
        if (ous.size() != 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }


    public void createOu(String rdn) {
        LdapTemplate ldapTemplate = initLDAPConnect();
        LdapNameBuilder ldapNameBuilder = LdapNameBuilder
                .newInstance();

        ldapNameBuilder.add("ou", rdn);
        Name dn = ldapNameBuilder.build();
        DirContextAdapter context = new DirContextAdapter(dn);
        List<String> objectClass = new ArrayList<>();
        objectClass.add("top");
        objectClass.add("organizationalUnit");
        context.setAttributeValues("objectclass", objectClass.toArray(new String[0]));

        ldapTemplate.bind(context);
    }


    public void createOu(List<String> rdns) {

        LdapTemplate ldapTemplate = initLDAPConnect();
        LdapNameBuilder ldapNameBuilder = LdapNameBuilder
                .newInstance();

        rdns.forEach(rdn -> ldapNameBuilder.add("ou", rdn));
        Name dn = ldapNameBuilder.build();
        DirContextAdapter context = new DirContextAdapter(dn);
        List<String> objectClass = new ArrayList<>();
        objectClass.add("top");
        objectClass.add("organizationalUnit");
        context.setAttributeValues("objectclass", objectClass.toArray(new String[0]));

        ldapTemplate.bind(context);
    }


    public Boolean isRoleUpdate(String role) {
        LdapClient ldapclient = ldaprepository.findBySn(1);
        return ldapclient.getRoles().stream().anyMatch(r ->
                r.getRole().equals(role));
    }


    public void addUser(String username, String password, String role) {

        logger.info("add a new user:" + username);

        LdapClient ldapclient = ldaprepository.findBySn(1);


        Role ldapRole = ldapclient.getRoles().stream().filter(r -> r.getRole().equals(role))
                .findFirst()
                .get();

        Integer uidNumber = ldapclient.getUidNumber() + 1;

        LdapNameBuilder ldapNameBuilder = LdapNameBuilder
                .newInstance();


        //ou exists?
        if (role.equals("student")) {
            List<String> rdns = new ArrayList<>();
            rdns.add("student");
            rdns.add(username.split("-")[0]);
            if (!isOuExist(username.split("-")[0])) {
                createOu(rdns);
            }
        }


        Name dn = buildDN(username,ldapRole,role);


        DirContextAdapter context = new DirContextAdapter(dn);

        List<String> objectClass = new ArrayList<>();
        objectClass.add("top");
        objectClass.add("person");
        objectClass.add("organizationalPerson");
        objectClass.add("inetOrgPerson");
        objectClass.add("posixAccount");
        objectClass.add("shadowAccount");


        if (ldapclient.getObjectclass().equals("sambaSamAccount")) {
            objectClass.add("sambaSamAccount");
            context.setAttributeValue("sambaSID", String.format("%s-%s", ldapclient.getSid(), uidNumber));

        }

        context.setAttributeValues("objectclass", objectClass.toArray(new String[0]));


        context.setAttributeValue("uid", username);
        context.setAttributeValue("cn", username);
        context.setAttributeValue("sn", username);
        context.setAttributeValue("userPassword", digestSHA(password));
        context.setAttributeValue("displayName", userloginservice.getUser().getName());
        context.setAttributeValue("uidNumber", String.valueOf(uidNumber));
        context.setAttributeValue("gidNumber", String.valueOf(ldapRole.getGid()));

        if (role.equals("student")) {
//                logger.info(username.split("-")[0]);
            context.setAttributeValue("homeDirectory", String.format("%s/%s/%s", ldapRole.getHome(), username.split("-")[0], username));

        } else {
            context.setAttributeValue("homeDirectory", String.format("%s/%s", ldapRole.getHome(), username));


        }

        LdapTemplate ldaptemplate = initLDAPConnect();
        ldaptemplate.bind(context);

        //update uidnumber
        ldapclient.setUidNumber(uidNumber);
        ldaprepository.save(ldapclient);


    }

    private String digestSHA(final String password) {
        String base64;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA");
            digest.update(password.getBytes());
            base64 = Base64
                    .getEncoder()
                    .encodeToString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return "{SHA}" + base64;
    }

    private Name buildDN(String username, Role ldapRole, String role) {

        LdapNameBuilder ldapNameBuilder = LdapNameBuilder
                .newInstance();
        Name dn = ldapNameBuilder.build();
        if (role.equals("teacher")) {
            ldapNameBuilder.add("ou", ldapRole.getOu());
            ldapNameBuilder.add("uid", username);

        }

        if (role.equals("student")) {
            List<String> rdns = new ArrayList<>();
            rdns.add("student");
            rdns.add(username.split("-")[0]);

            rdns.forEach(rdn -> ldapNameBuilder.add("ou", rdn));
            ldapNameBuilder.add("uid", username);
        }

        return ldapNameBuilder.build();

    }


}
