package app.passwd.service;


import app.passwd.ldap.model.ADUser;
import app.passwd.ldap.model.OUAttributesMapper;
import app.passwd.ldap.model.OrganizationalUnit;
import app.passwd.ldap.model.PersonAttributesMapper;
import app.passwd.model.LdapClient;
import app.passwd.model.Role;
import app.passwd.model.SchoolUser;
import app.passwd.model.User;
import app.passwd.repository.LdapRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;

import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.LdapName;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Service
public class LdapTools {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    LdapRepository ldapRepository;

//    @Autowired
//    AccountService accountService;

    @Autowired
    UserLoginService userloginservice;

    private LdapTemplate initLDAPConnect() {
        LdapClient ldapclient = ldapRepository.findBySn(1);
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

//    public Boolean isOuExist(String ouname) {
//        LdapTemplate ldapTemplate = initLDAPConnect();
//
//
//        List<OrganizationalUnit> ous = ldapTemplate.search(
//                query().where("objectclass").is("organizationalUnit")
//                        .and("ou").is(ouname),
//
//                new OUAttributesMapper());
//        if (ous.size() != 0) {
//            return Boolean.TRUE;
//        }
//        return Boolean.FALSE;
//    }

    public Boolean isOuExist(List<String> ous) {

        LdapTemplate ldapTemplate = initLDAPConnect();
        if (ous.size() == 1) {
            List<OrganizationalUnit> results = ldapTemplate.search(
                    query()
                            .where("objectclass").is("organizationalUnit")
                            .and("ou").is(ous.get(0)),

                    new OUAttributesMapper());
            if (results.size() != 0) {
                return Boolean.TRUE;
            }
        } else {
            List<OrganizationalUnit> results = ldapTemplate.search(
                    query()
                            .base(String.format("ou=%s", ous.get(0)))
                            .where("objectclass").is("organizationalUnit")
                            .and("ou").is(ous.get(1)),

                    new OUAttributesMapper());
            if (results.size() != 0) {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
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


    public Boolean isUserExist(String username) {
        LdapTemplate ldapTemplate = initLDAPConnect();


        List<ADUser> ADUsers = ldapTemplate.search(
                query().where("objectclass").is("person")
                        .and("cn").is(username),
                new PersonAttributesMapper());
        if (ADUsers.size() == 0) {

            return Boolean.FALSE;
        }
        return Boolean.TRUE;

    }


    public void updateUserPassword(ADUser aduser, String userPassword) throws UnsupportedEncodingException, InvalidNameException {
        LdapName ldapName = new LdapName(aduser.getDistinguishedName());


        List<String> rdns = new ArrayList<>();
        ldapName.getRdns().forEach(rdn -> {
            if (rdn.getType().equalsIgnoreCase("OU") || rdn.getType().equalsIgnoreCase("CN")) {
                rdns.add(rdn.toString());
            }
        });
        LdapTemplate ldapTemplate = initLDAPConnect();


        String passwdUnicodePwdFormat = String.format("\"%s\"", userPassword);
        byte[] passwd = passwdUnicodePwdFormat.getBytes(StandardCharsets.UTF_16LE);
//        context.setAttributeValue("unicodePwd", password);

//        ((LdapName) dn).getRdns().forEach(rdn -> logger.info(rdn.toString()));
        Attribute attr = new BasicAttribute("UnicodePwd", passwd);
        ModificationItem item = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);

        ldapTemplate.modifyAttributes(aduser.getDn(), new ModificationItem[]{item});
    }

//    public Boolean isRoleExist(String role) {
//        LdapClient ldapclient = ldaprepository.findBySn(1);
//        return ldapclient.getRoles().stream().anyMatch(r ->
//                r.getOu().equals(role));
//    }

    public void addStuUser(User user, String userPassword) throws UnsupportedEncodingException {
        Role role = ldapRepository.findBySn(1).getRoles().stream().filter(r -> r.getRole().contains("student")).findFirst().orElse(null);

        LdapTemplate ldapTemplate = initLDAPConnect();
        List<String> rdns = new ArrayList<>();
        String year = user.getUsername().split("-")[0];

        //ou=Students, ou=104
        rdns.add(role.getOu());
        rdns.add(year);

        if (!isOuExist(rdns)) {
            createOu(rdns);
        }

        LdapNameBuilder ldapNameBuilder = LdapNameBuilder
                .newInstance();
        rdns.forEach(rdn -> ldapNameBuilder.add("ou", rdn));
        ldapNameBuilder.add("cn", user.getAdusername());
        Name dn = ldapNameBuilder.build();


        DirContextAdapter context = new DirContextAdapter(dn);

        List<String> objectClass = new ArrayList<>();
        objectClass.add("top");
        objectClass.add("person");
        objectClass.add("organizationalPerson");
        objectClass.add("user");
        context.setAttributeValues("objectclass", objectClass.toArray(new String[0]));

        context.setAttributeValue("cn", user.getAdusername());
        context.setAttributeValue("displayName", user.getName());
        context.setAttributeValue("userAccountControl", "512");
        context.setAttributeValue("sAMAccountName", user.getAdusername());
        context.addAttributeValue("description", String.format("%s", user.getName()));

        context.setAttributeValue("pwdLastSet", "-1");
        String upn = String.format("%s@%s", user.getAdusername(), ldapRepository.findBySn(1).getUpnSuffix());
        context.setAttributeValue("userPrincipalName", upn);
        String passwdUnicodePwdFormat = String.format("\"%s\"", userPassword);
        byte[] passwd = passwdUnicodePwdFormat.getBytes(StandardCharsets.UTF_16LE);
        context.setAttributeValue("unicodePwd", passwd);

//        logger.info("建立學生帳號:" + user.getAdusername());
        ldapTemplate.bind(context);

    }


    public void addUser(List<SchoolUser> users, String ou) throws IOException {

        Role role = ldapRepository.findBySn(1).getRoles().stream().filter(r -> r.getRole().contains(ou)).findFirst().orElse(null);

        users.forEach(user -> {
            LdapTemplate ldapTemplate = initLDAPConnect();
            Name dn = LdapNameBuilder
                    .newInstance()
                    .add("ou", role.getOu())
                    .add("cn", user.getUsername())
                    .build();
            DirContextAdapter context = new DirContextAdapter(dn);

            List<String> objectClass = new ArrayList<>();
            objectClass.add("top");
            objectClass.add("person");
            objectClass.add("organizationalPerson");
            objectClass.add("user");
            context.setAttributeValues("objectclass", objectClass.toArray(new String[0]));

            context.setAttributeValue("cn", user.getUsername());
            context.setAttributeValue("displayName", user.getName());
            context.setAttributeValue("userAccountControl", "512");
            context.setAttributeValue("sAMAccountName", user.getUsername());
            context.setAttributeValue("pwdLastSet", "-1");
            context.addAttributeValue("description", user.getName());
            context.addAttributeValue("physicalDeliveryOfficeName", user.getPhysicalDeliveryOfficeName() + user.getPersonalTitle());

            String upn = String.format("%s@%s", user.getUsername(), ldapRepository.findBySn(1).getUpnSuffix());
            context.setAttributeValue("userPrincipalName", upn);
            String passwdUnicodePwdFormat = String.format("\"%s\"", "demo1234");
            byte[] passwd = passwdUnicodePwdFormat.getBytes(StandardCharsets.UTF_16LE);
            context.setAttributeValue("unicodePwd", passwd);
            ldapTemplate.bind(context);

        });


    }

    public void addUser(User user, String userPassword, String ou, SchoolUser schoolUser) throws IOException {

        Role role = ldapRepository.findBySn(1).getRoles().stream().filter(r -> r.getRole().contains(ou)).findFirst().orElse(null);

//        SchoolUser schoolUser = accountService.getStaffUser(user.getUsername());
        logger.info("get staff data:" + schoolUser.getPassword());
        LdapTemplate ldapTemplate = initLDAPConnect();
        Name dn = LdapNameBuilder
                .newInstance()
                .add("ou", role.getOu())
                .add("cn", user.getUsername())
                .build();
        DirContextAdapter context = new DirContextAdapter(dn);

        List<String> objectClass = new ArrayList<>();
        objectClass.add("top");
        objectClass.add("person");
        objectClass.add("organizationalPerson");
        objectClass.add("user");
        context.setAttributeValues("objectclass", objectClass.toArray(new String[0]));

        context.setAttributeValue("cn", user.getUsername());
        context.setAttributeValue("displayName", user.getName());
        context.setAttributeValue("userAccountControl", "512");
        context.setAttributeValue("sAMAccountName", user.getUsername());
        context.setAttributeValue("pwdLastSet", "-1");
        context.addAttributeValue("description", user.getName());
        context.addAttributeValue("physicalDeliveryOfficeName", schoolUser.getPhysicalDeliveryOfficeName() + schoolUser.getPersonalTitle()
        );

        String upn = String.format("%s@%s", user.getUsername(), ldapRepository.findBySn(1).getUpnSuffix());
        context.setAttributeValue("userPrincipalName", upn);
        String passwdUnicodePwdFormat = String.format("\"%s\"", userPassword);
        byte[] passwd = passwdUnicodePwdFormat.getBytes(StandardCharsets.UTF_16LE);
        context.setAttributeValue("unicodePwd", passwd);
        ldapTemplate.bind(context);

    }

    public List<ADUser> findAll() {

        LdapTemplate ldapTemplate = initLDAPConnect();

        List<ADUser> users = ldapTemplate.find(
                query().where("cn").isPresent(), ADUser.class);
        return users;
    }


    public ADUser findByCn(String username) {
        LdapTemplate ldapTemplate = initLDAPConnect();

        return ldapTemplate.findOne(
                query().where("cn").is(username), ADUser.class);

    }

}
