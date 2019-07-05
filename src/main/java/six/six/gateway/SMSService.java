package six.six.gateway;

import java.io.IOException;

/**
 * SMS provder interface
 */
public interface SMSService {
    boolean send(String phoneNumber, String message, String login, String pw) throws IOException;
}
