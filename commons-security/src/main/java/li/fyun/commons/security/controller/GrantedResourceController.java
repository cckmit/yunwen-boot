package li.fyun.commons.security.controller;

import li.fyun.commons.security.entity.GrantedResource;
import li.fyun.commons.core.controller.RestResourceController;
import li.fyun.commons.security.repository.GrantedResourceRepository;
import li.fyun.commons.security.service.ResourceSecurityMetadataSource;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/security/resources")
public class GrantedResourceController extends RestResourceController<GrantedResource, Long> {

    @Resource
    private ResourceSecurityMetadataSource resourceSecurityMetadataSource;

    public GrantedResourceController(@Autowired GrantedResourceRepository repository) {
        super(repository);
    }

    @GetMapping("/search")
    public List<GrantedResource> search(String word) {
        return ((GrantedResourceRepository) repository).findByUrlLike("%" + word + "%");
    }

    @Override
    protected void preSave(GrantedResource input, GrantedResource target, HttpServletRequest request) {
        if (StringUtils.isNotBlank(input.getMethod())
                && !ArrayUtils.contains(GrantedResource.ALLOW_METHODS, input.getMethod())) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    protected void postDataChanged() {
        resourceSecurityMetadataSource.loadGrantedResources();
    }

    @Override
    @DeleteMapping
    public Map<?, ?> delete(Long[] id) throws NoSuchMethodException {
        throw new NoSuchMethodException();
    }

}
