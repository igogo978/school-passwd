package app.passwd.service;


import app.passwd.ldap.model.ADUser;
import app.passwd.ldap.model.OUAttributesMapper;
import app.passwd.ldap.model.OrganizationalUnit;
import app.passwd.ldap.model.PersonAttributesMapper;

import app.passwd.model.LdapClient;
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

import java.io.UnsupportedEncodingException;
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
//
        LdapNameBuilder ldapNameBuilder =  LdapNameBuilder.newInstance().newInstance();
        rdns.forEach(rdn->ldapNameBuilder.add(rdn));
        ldapNameBuilder.build();

        Name dn = ldapNameBuilder.build();
        for (int i = 0; i < dn.size(); i++) {
            System.out.println(dn.get(i));
        }
//        Name dn = LdapNameBuilder
//                .newInstance()
//                .add("cn", aduser.getCn())
//                .build();

//
        String passwdUnicodePwdFormat = String.format("\"%s\"", userPassword);
        byte[] passwd = passwdUnicodePwdFormat.getBytes("UTF-16LE");
//        context.setAttributeValue("unicodePwd", password);

//        ((LdapName) dn).getRdns().forEach(rdn -> logger.info(rdn.toString()));
        Attribute attr = new BasicAttribute("UnicodePwd", passwd);
        ModificationItem item = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);

        ldapTemplate.modifyAttributes(dn, new ModificationItem[]{item});
    }

    public Boolean isRoleExist(String role) {
        LdapClient ldapclient = ldaprepository.findBySn(1);
        return ldapclient.getRoles().stream().anyMatch(r ->
                r.getOu().equals(role));
    }


    public void addUser(User user, String userPassword) throws UnsupportedEncodingException {
        LdapTemplate ldapTemplate = initLDAPConnect();
        Name dn = LdapNameBuilder
                .newInstance()
                .add("ou", user.getRole())
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
        String upn = String.format("%s@%s", user.getUsername(), ldaprepository.findBySn(1).getUpnSuffix());
        context.setAttributeValue("userPrincipalName", upn);
        String passwdUnicodePwdFormat = String.format("\"%s\"", userPassword);
        byte[] passwd = passwdUnicodePwdFormat.getBytes("UTF-16LE");
        context.setAttributeValue("unicodePwd", passwd);
        ldapTemplate.bind(context);


    }

    public ADUser findByCn(String username) {
        LdapTemplate ldapTemplate = initLDAPConnect();
        List<ADUser> users = ldapTemplate.search(
                query().where("objectclass").is("person")
                        .and("cn").is(username),
                new PersonAttributesMapper());

        return users.get(0);
    }

}
