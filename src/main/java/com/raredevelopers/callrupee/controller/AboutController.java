package com.raredevelopers.callrupee.controller;

import com.raredevelopers.callrupee.ConfigUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class AboutController {

    @Autowired
    private ConfigUtil configUtil;

    @GetMapping
    public ResponseEntity<String> getAbout() {
        String about = configUtil.getProperty("about");
        return new ResponseEntity<>(about, HttpStatus.OK);
    }
}
