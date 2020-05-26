package li.fyun.commons.core.controller;

import com.google.common.collect.Maps;
import li.fyun.commons.core.jpa.AbstractPersistable;
import li.fyun.commons.core.jpa.BaseRepository;
import li.fyun.commons.core.jpa.RepositoryUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.io.Serializable;
import java.util.*;

@Slf4j
public abstract class RestResourceController<T extends AbstractPersistable, ID extends Serializable> extends BaseController {

    protected BaseRepository<T, ID> repository;

    public RestResourceController(BaseRepository<T, ID> repo) {
        this.repository = repo;
    }

    @GetMapping
    public Iterable<T> list(String page, String size) {
        if (StringUtils.isBlank(page)) {
            return this.repository.findAll();
        }
        return this.repository.findAll(RepositoryUtils.buildPageRequest(page, size));
    }

    @GetMapping(value = "/{id}")
    public T get(@PathVariable ID id) {
        return this.repository.findById(id).orElseThrow(() -> new EntityNotFoundException());
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public T create(@Valid @RequestBody T entity, HttpServletRequest request) {
        if (entity.getId() != null) {
            throw new ValidationException("新建数据不应指定ID.");
        }
        this.preSave(entity, null, request);
        log.debug("create() with body {} of type {}", entity, entity.getClass());
        entity = this.repository.save(entity);
        this.postDataChanged();
        return entity;
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public T update(@PathVariable ID id, @Valid @RequestBody T entity, HttpServletRequest request) {
        verifyBuildInData(id, false);
        return repository.findById(id)
                .map(target -> {
                    this.preSave(entity, target, request);
                    BeanUtils.copyProperties(entity, target, entity.getIgnoreProperties());
                    target = repository.save(target);
                    this.postDataChanged();
                    return target;
                })
                .orElseThrow(() -> new EntityNotFoundException());
    }

    protected void verifyBuildInData(@PathVariable ID id, boolean delete) {
        if (id instanceof Long && id != null &&
                ((Long) id).longValue() < RepositoryUtils.APP_INIT_ENTITY_ID_LIMIT) {
            throw new IllegalArgumentException("不可" + (delete ? "删除" : "编辑") + "系统内建数据.");
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable ID id) {
        verifyBuildInData(id, true);
        this.repository.deleteById(id);
        this.postDataChanged();
        return OK;
    }

    @DeleteMapping
    public Map<?, ?> delete(ID[] id) throws NoSuchMethodException {
        if (ArrayUtils.isEmpty(id)) {
            throw new IllegalArgumentException();
        }
        List<ID> success = new ArrayList<ID>();
        List<ID> failed = new ArrayList<ID>();
        for (ID eid : id) {
            try {
                verifyBuildInData(eid, true);
                this.repository.deleteById(eid);
                success.add(eid);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                failed.add(eid);
            }
        }
        this.postDataChanged();
        Map<String, Object> result = Maps.newLinkedHashMap();
        result.put("status", OK_STRING);
        result.put("success", success);
        result.put("failed", failed);
        return result;
    }

    protected void preSave(T input, T target, HttpServletRequest request) {
        // no default
    }

    protected void postDataChanged() {
        // no default
    }

}