package li.fyun.commons.messaging.sms.impl;

import li.fyun.commons.messaging.YunwenMessageException;
import com.aliyuncs.exceptions.ClientException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class AliDayuSenderTest {

    @Test
    public void send() {
        try {
            AliyunSmsService aliyunSmsService = new AliyunSmsService(
                    "LTAI4FdLGmyMh2A4p4heW1Wb",
                    "omFSACHzg6Ho5BPjkiTEizNOPUBNhO",
                    "10000", "慧盈科技");
            Map<String, String> params = new HashMap<>();
            params.put("code", RandomStringUtils.randomNumeric(6));
            aliyunSmsService.send("13923454259", "SMS_185600086", params);
        } catch (YunwenMessageException | ClientException e) {
            e.printStackTrace();
        }
    }
}
