package six.six.keycloak.authenticator;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

public class PhoneAuthenticatorFactory implements AuthenticatorFactory {

    private static Logger logger = Logger.getLogger(KeycloakSmsAuthenticatorFactory.class);

    public static final String PROVIDER_ID = "phone-authentication";
    private static final PhoneAuthenticator SINGLETON = new PhoneAuthenticator();

    private static AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.DISABLED
    };


    @Override
    public Authenticator create(KeycloakSession keycloakSession) {
        return SINGLETON;
    }

    @Override
    public void init(Config.Scope scope) {
        logger.info("[PhoneAuthenticationFactory] init");
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        logger.info("[PhoneAuthenticationFactory] postInit");
    }

    @Override
    public void close() {
        logger.info("[PhoneAuthenticationFactory] close");
    }

    @Override
    public String getId() {
        logger.info("[PhoneAuthenticationFactory] getId");
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        logger.info("[PhoneAuthenticationFactory] getDisplayType");
        return "Phone number authentication";
    }

    @Override
    public String getReferenceCategory() {
        logger.info("[PhoneAuthenticationFactory] getReferenceCategory");
        return "phone-auth-code";
    }

    @Override
    public boolean isConfigurable() {

        logger.info("[PhoneAuthenticationFactory] isConfigurable");
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        logger.info("[PhoneAuthenticationFactory] getRequirementChoices");
        return REQUIREMENT_CHOICES;

    }

    @Override
    public boolean isUserSetupAllowed() {
        return true;
    }

    @Override
    public String getHelpText() {
        return "Validates user by sms code.\";";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {

        logger.info("[PhoneAuthenticationFactory] getConfigProperties");
        return null;
    }
}
