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
        String url = String.format("ldaps://%s:%s", ldapclient.getLdapserver(), ldapclient.getLdapport());
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

    public Boolean isOUExist(String ouname) {
        LdapTemplate ldapTemplate = initLDAPConnect();
//        LdapClient ldapclient = ldaprepository.findBySn(1);
//        String url = String.format("ldaps://%s:%s", ldapclient.getLdapserver(), ldapclient.getLdapport());
//        String basedn = ldapclient.getBasedn();
//        String rootdn = ldapclient.getRootdn();
//        String rootpassword = ldapclient.getPasswd();
//
//        LdapContextSource source = new LdapContextSource();
//        source.setUrl(url);
//        source.setBase(basedn);
//        source.setUserDn(rootdn);
//        source.setPassword(rootpassword);
//        source.afterPropertiesSet();
//
//        LdapTemplate ldapTemplate = new LdapTemplate(source);

        List<OrganizationalUnit> ous = ldapTemplate.search(
                query().where("objectclass").is("organizationalUnit")
                        .and("ou").is(ouname),

                new OUAttributesMapper());
        if (ous.size() != 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }



    public void createOu(String ouname) {

        LdapTemplate ldapTemplate = initLDAPConnect();
        Name dn = LdapNameBuilder
                .newInstance()
                .add("ou", ouname)
                .build();
        DirContextAdapter context = new DirContextAdapter(dn);
        List<String> objectClass = new ArrayList<>();
        objectClass.add("top");
        objectClass.add("organizationalUnit");
        context.setAttributeValues("objectclass", objectClass.toArray(new String[0]));

        ldapTemplate.bind(context);
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

        LdapTemplate ldaptemplate = new LdapTemplate(source);


        List<User> users = ldaptemplate.search(
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
        logger.info("loggin for:" + username);
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

        LdapTemplate ldaptemplate = new LdapTemplate(source);


        List<User> users = ldaptemplate.search(
                query().where("objectclass").is("person")
                        .and("uid").is(username),
                new PersonAttributesMapper());
        return users.get(0);


    }

    public void updateUserPassword(String username, String password, String role) {
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

        LdapTemplate ldaptemplate = new LdapTemplate(source);

//        List<User> users = ldaptemplate.search(
//                query().where("objectclass").is("person")
//                        .and("uid").is(username),
//                new PersonAttributesMapper());

        Name dn = LdapNameBuilder
                .newInstance()
                .add("ou", role)
                .add("uid", username)
                .build();

        ((LdapName) dn).getRdns().forEach(rdn -> logger.info(rdn.toString()));
        Attribute attr = new BasicAttribute("userPassword", digestSHA(password));
        ModificationItem item = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);

        ldaptemplate.modifyAttributes(dn, new ModificationItem[]{item});
    }

    public Boolean isRoleExist(String role) {
        LdapClient ldapclient = ldaprepository.findBySn(1);
        return ldapclient.getRoles().stream().anyMatch(r ->
                r.getOu().equals(role));
    }


    public void addUser(String username, String password, String role) {

        LdapClient ldapclient = ldaprepository.findBySn(1);

//        Boolean existRole = ldapclient.getRoles().stream().anyMatch(r ->
//                r.getOu().equals(role));
        Boolean isRoleExist = isRoleExist(role);

        logger.info(String.format("role exist? %s", isRoleExist));

        if (isRoleExist) {
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

            LdapTemplate ldaptemplate = new LdapTemplate(source);


            Role ou = ldapclient.getRoles().stream().filter(r -> r.getOu().equals(role))
                    .findFirst()
                    .get();


            Name dn = LdapNameBuilder
                    .newInstance()
                    .add("ou", ou.getOu())
                    .add("uid", username)
                    .build();

            DirContextAdapter context = new DirContextAdapter(dn);

            List<String> objectClass = new ArrayList<>();
            objectClass.add("top");
            objectClass.add("person");
            objectClass.add("organizationalPerson");
            objectClass.add("inetOrgPerson");
            objectClass.add("posixAccount");
            objectClass.add("shadowAccount");


            context.setAttributeValues("objectclass", objectClass.toArray(new String[0]));


            context.setAttributeValue("uid", username);
            context.setAttributeValue("cn", username);
            context.setAttributeValue("sn", username);
            context.setAttributeValue("userPassword", digestSHA(password));
            context.setAttributeValue("displayName", userloginservice.getUser().getName());


            ldaptemplate.bind(context);

            //update uidnumber
            ldaprepository.save(ldapclient);
        }


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

}
