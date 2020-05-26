package li.fyun.commons.core.controller;

import org.springframework.http.ResponseEntity;

import java.util.Collections;

public abstract class BaseController {

    public static final String OK_STRING = "OK";

    protected static ResponseEntity OK = ResponseEntity.ok(Collections.singletonMap("status", OK_STRING));

}
