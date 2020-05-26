package li.fyun.commons.core.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

@JsonComponent
public class JsonDateDeserializer extends JsonDeserializer<Date> {

    public JsonDateDeserializer() {
        super();
    }

    @Override
    public Date deserialize(JsonParser jsonparser, DeserializationContext deserializationcontext)
            throws IOException, JsonProcessingException {
        try {
            return DateEditor.STD_DATE_FORMAT.parse(
                    StringUtils.replace(jsonparser.getText(), " ", "T")
            );
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        }
    }

}
