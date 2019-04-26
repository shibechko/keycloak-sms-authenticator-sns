package six.six.keycloak.authenticator;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AbstractFormAuthenticator;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import six.six.gateway.SMSService;
import six.six.gateway.aws.snsclient.SnsNotificationService;
import six.six.gateway.gsmgateway.VitebskGasGSMGateway;
import six.six.keycloak.KeycloakSmsConstants;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static six.six.keycloak.authenticator.KeycloakSmsAuthenticatorUtil.createMessage;
import static six.six.keycloak.authenticator.KeycloakSmsAuthenticatorUtil.getMessage;


public class PhoneAuthenticator extends AbstractFormAuthenticator {

    private static Logger logger = Logger.getLogger(PhoneAuthenticator.class);
    public static String FORM_PHONENUMBER = "mobile_number";
    public static String FORM_SMSCODE = "smsCode";

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        logger.info("[PhoneAuthenticator] Call authenticate");
        Response challenge = context.form().createForm("sms-validation-mobile-number.ftl");
        context.challenge(challenge);

    }

    @Override
    public void action(AuthenticationFlowContext context) {
        logger.info("[SmsAuthenticator] Call action");

        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        logger.info("[SmsAuthenticator] Call action2");
        if (formData.containsKey("cancel")) {
            context.cancelLogin();
            return;
        }
        if(formData.containsKey(FORM_PHONENUMBER)) {
            logger.info("[SmsAuthenticator] Call action: has phone");
            String phoneNumber = formData.getFirst(FORM_PHONENUMBER);
            if(!checkMobileNumber(phoneNumber)) {
                logger.info("[SmsAuthenticator] Call action: invalid phone");
                Response chalenge = context.form().setError("Invalid phone number").createForm("sms-validation-mobile-number.ftl");
                context.challenge(chalenge);
            } else {
                //Lookup user with phone exists
                UserModel user = findUser(context.getSession(), phoneNumber);
                if(user != null) {
                    //Generate and store code
                    String code = KeycloakSmsAuthenticatorUtil.getSmsCode(5);
                    context.getAuthenticationSession().setAuthNote("sms_code", code);
                    logger.info("[SmsAuthenticator] Call action: generate code " + code);

                    //Send code by sms;
                    sendSmsCode(phoneNumber, code, context);
//                    sendSmsCode("375297105879", code, context);

                    logger.info("[SmsAuthenticator] Call action: validate code form");
                    Response chalenge = context.form().createForm("sms-validation.ftl");
                    context.challenge(chalenge);
                } else {
                    logger.info("[SmsAuthenticator] Call action: user not found");
                    Response chalenge = context.form().setError("User not found").createForm("sms-validation-mobile-number.ftl");
                    context.challenge(chalenge);
                }
            }
            return;
        }
        if(formData.containsKey(FORM_SMSCODE)) {
            logger.info("[SmsAuthenticator] Call action: validate code");
            String code = context.getAuthenticationSession().getAuthNote("sms_code");
            logger.info("[SmsAuthenticator] Call action: validate code "+ code);
            String ucode = formData.getFirst(FORM_SMSCODE);
            if(ucode.equals(code)) {
                logger.info("[SmsAuthenticator] Call action: valid code");
                UserModel user = KeycloakModelUtils.findUserByNameOrEmail(context.getSession(), context.getRealm(), "test");
                context.setUser(user);
                context.success();
            } else {
                logger.info("[SmsAuthenticator] Call action: invalidate code");
                Response chalenge = context.form().setError("Invalid code, try again.").createForm("sms-validation.ftl");
                context.challenge(chalenge);
            }
            return;
        }
        logger.info("[SmsAuthenticator] Call action: cancel login");
        context.cancelLogin();
    }

    private UserModel findUser(KeycloakSession session, String phone) {
        UserModel user = null;
        List<UserModel> users = session.users().getUsers(session.realms().getRealmByName("GAS"), true);
        logger.info("USERS COUNT: " + users.size());
        Iterator<UserModel> usersIterator = users.iterator();
        while(usersIterator.hasNext()) {
            UserModel u = usersIterator.next();
            Collection<String> attrs = KeycloakModelUtils.resolveAttribute(u, "mobile_number", true);
            Iterator<String> attrsIterator = attrs.iterator();
            while(attrsIterator.hasNext()) {
                String item = attrsIterator.next();
                if(phone.equals(item)) {
                    user = u;
                    break;
                }
            }
            if(user != null) break;
        }
        if(user != null) {
            logger.info("Find User: " + user.getUsername());
        }
        return user;
    }

    public static Boolean checkMobileNumber(String mobileNumber) {

        Boolean result = false;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber phone = phoneUtil.parse(mobileNumber, null);
            mobileNumber = phoneUtil.format(phone,
                    PhoneNumberUtil.PhoneNumberFormat.E164);
            result = true;
        } catch (NumberParseException e) {
            logger.error("Invalid phone number " + mobileNumber, e);
        }

        return result;
    }

    static boolean sendSmsCode(String mobileNumber, String code, AuthenticationFlowContext context) {

        // Send an SMS
        logger.debug("Sending " + code + "  to mobileNumber " + mobileNumber);

//        String smsUsr = EnvSubstitutor.envSubstitutor.replace(getConfigString(config, KeycloakSmsConstants.CONF_PRP_SMS_CLIENTTOKEN));
        String smsUsr = "AKIAUBL3LWJICWDJ4WZH";
//        String smsPwd = EnvSubstitutor.envSubstitutor.replace(getConfigString(config, KeycloakSmsConstants.CONF_PRP_SMS_CLIENTSECRET));
        String smsPwd = "GH6YVTpMP25YB7xkj5pUlcyIcy+RM/tZGL1PP8YP";
//        String gateway = getConfigString(config, KeycloakSmsConstants.CONF_PRP_SMS_GATEWAY);

        // Create the SMS message body
        String template = getMessage(context, KeycloakSmsConstants.CONF_PRP_SMS_TEXT);
        String smsText = createMessage(template, code, mobileNumber);

        boolean result;
        SMSService smsService;
        try {
//            smsService = new SnsNotificationService();
//            result=smsService.send(mobileNumber, smsText, smsUsr, smsPwd);
//            return result;
            smsService = new VitebskGasGSMGateway();
            result = smsService.send(mobileNumber, smsText, "", "");
            return result;
        } catch(Exception e) {
            logger.error("Fail to send SMS " ,e );
            return false;
        }
    }


    @Override
    public boolean requiresUser() {
        logger.info("[SmsAuthenticator] Call requiredUser");
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        // never called
        return true;

    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        // never called
    }

    @Override
    public void close() {
        logger.info("[SmsAuthenticator] close");
    }
}
