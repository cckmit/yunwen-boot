package li.fyun.commons.rule;

import li.fyun.commons.core.utils.YunwenException;

public class RuleException extends YunwenException {

    public RuleException() {
        super();
    }

    public RuleException(String message) {
        super(message);
    }

    public RuleException(String message, Throwable cause) {
        super(message, cause);
    }

}
