package li.fyun.commons.core.utils;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.util.StdDateFormat;
import org.apache.commons.lang3.StringUtils;

public class DateEditor extends PropertyEditorSupport {

    public static StdDateFormat STD_DATE_FORMAT = new StdDateFormat();
    private boolean emptyAsNull;

    static {
        STD_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
    }

    public DateEditor(boolean emptyAsNull) {
        this.emptyAsNull = emptyAsNull;
    }

    @Override
    public String getAsText() {
        Date value = (Date) getValue();
        return value != null ? STD_DATE_FORMAT.format(value) : "";
    }

    @Override
    public void setAsText(String text) {
        if (text == null) {
            setValue(null);
        } else {
            String value = text.trim();
            if (emptyAsNull && "".equals(value)) {
                setValue(null);
            } else {
                try {
                    value = StringUtils.replace(value, " ", "T");
                    Date time = STD_DATE_FORMAT.parse(value);
                    setValue(time);
                } catch (ParseException e) {
                    setValue(null);
                }
            }
        }
    }

}