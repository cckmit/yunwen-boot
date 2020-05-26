package li.fyun.commons.messaging.sms.impl;

import li.fyun.commons.messaging.sms.SmsService;
import li.fyun.commons.messaging.YunwenMessageException;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import li.fyun.commons.core.utils.SnowflakeSequence;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Getter
@Setter
@Slf4j
public class AliyunSmsService implements SmsService {

    private static final Logger logger = LoggerFactory.getLogger(AliyunSmsService.class);

    private IAcsClient acsClient;
    private ObjectMapper objectMapper = new ObjectMapper();
    private String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
    private SnowflakeSequence sequence = new SnowflakeSequence();
    private String signName;

    @Autowired
    public AliyunSmsService(@Value("${app.alidayu.app-key}") String appKey,
                            @Value("${app.alidayu.secret-key}") String secretKey,
                            @Value("${app.alidayu.timeout}") String timeout,
                            @Value("${app.alidayu.sign-name}") String signName) throws ClientException {
        System.setProperty("sun.net.client.defaultConnectTimeout", timeout);
        System.setProperty("sun.net.client.defaultReadTimeout", timeout);
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", appKey, secretKey);
        DefaultProfile.addEndpoint("cn-hangzhou", product, "dysmsapi.aliyuncs.com");
        acsClient = new DefaultAcsClient(profile);
        this.signName = signName;
        log.debug("smsProperties.getAppKey() {}, smsProperties.getSecretKey() {}, signName {}",
                appKey, secretKey, this.signName);
    }

    public void send(String mobile, String template, Map<String, String> params) throws YunwenMessageException {
        try {
            SendSmsRequest request = new SendSmsRequest();
            request.setSysMethod(MethodType.POST);
            request.setSignName(signName);
            request.setPhoneNumbers(mobile);
            request.setTemplateCode(template);
            String templateParam = objectMapper.writeValueAsString(params);
            request.setTemplateParam(templateParam);
            request.setOutId(sequence.nextId() + "");
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            logger.info("手机 {}, 模版 {}, 参数 {}, 响应: {}",
                    mobile, template, templateParam, objectMapper.writeValueAsString(sendSmsResponse));
            if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
                //
            } else {
                throw new RuntimeException(sendSmsResponse.getMessage() != null ? sendSmsResponse.getMessage() : "短信发送失败");
            }
        } catch (JsonProcessingException | ClientException ex) {
            throw new YunwenMessageException(ex.getMessage());
        }
    }

}