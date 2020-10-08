package li.fyun.commons.core.criteria.plain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import li.fyun.commons.core.criteria.Criterion;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class PlainCriterionImpl implements Criterion<PlainExpressionImpl> {

    private List<PlainExpressionImpl> and;
    private List<PlainExpressionImpl> or;

}
