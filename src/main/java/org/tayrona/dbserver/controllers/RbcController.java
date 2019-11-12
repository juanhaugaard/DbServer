package org.tayrona.dbserver.controllers;

import io.micrometer.core.instrument.util.StringUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.tayrona.dbserver.services.ClickToView;

@Slf4j
@RestController
@RequestMapping("rbc")
public class RbcController {
    @Autowired
    private ClickToView clickToView;

    @RequestMapping(value = "/ClickToView", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    @ApiOperation(value = "Post to RBC Click to View", response = String.class)
    public ResponseEntity<String> postClickToView() {
        String result = clickToView.PostToRBC();
        if (StringUtils.isBlank(result)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(result);
        }
    }
}
