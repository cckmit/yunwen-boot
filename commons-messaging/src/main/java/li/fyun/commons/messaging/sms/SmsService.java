package li.fyun.commons.messaging.sms;

import li.fyun.commons.messaging.YunwenMessageException;

import java.util.Map;

public interface SmsService {

    void send(String mobile, String template, Map<String, String> params) throws YunwenMessageException;

}
