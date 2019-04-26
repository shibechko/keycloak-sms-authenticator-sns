package six.six.gateway.gsmgateway;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.logging.Logger;
import six.six.gateway.SMSService;
import six.six.keycloak.authenticator.PhoneAuthenticator;

import java.io.IOException;
import java.net.URLEncoder;

// Yeastar Neogate TG100 Gateway
public class VitebskGasGSMGateway implements SMSService{

    private static Logger logger = Logger.getLogger(PhoneAuthenticator.class);


    static String gw_ip = "192.168.153.4";
    static String gw_user = "apiuser";
    static String gw_password = "utytpbc";
    static String gw_port = "1";

    static String right_answer="Response:SuccessMessage:Commitsuccessfully!";

    @Override
    public boolean send(String phoneNumber, String message, String login, String pw) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String url = "http://"+gw_ip+"/cgi/WebCGI?1500101=account="+gw_user+
                "&password="+gw_password+
                "&port="+gw_port+
                "&destination="+URLEncoder.encode(phoneNumber, "UTF-8")+
                "&content="+URLEncoder.encode(message, "UTF-8");
        HttpGet request = new HttpGet(url);
        try {
            logger.info("Before send sms: "+url);
            HttpResponse response = httpClient.execute(request);
            logger.info("sms http resp: "+response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.close();
        }

        return false;
    }
}
