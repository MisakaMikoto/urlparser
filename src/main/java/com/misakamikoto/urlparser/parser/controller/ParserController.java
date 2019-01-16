package com.misakamikoto.urlparser.parser.controller;

import com.misakamikoto.urlparser.parser.dto.Response;
import com.misakamikoto.urlparser.parser.service.ParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class ParserController {

    @Autowired
    ParserService parserService;

    @GetMapping("/parse")
    public Response parse(@RequestParam("url") String url,
                          @RequestParam("type") String type,
                          @RequestParam("dividingNumber") int dividingNumber) throws IOException {
//        String surl = "https://comic.naver.com/webtoon/detail.nhn?titleId=183559&no=418&weekday=mon";
        return this.parserService.getResponseText(url, type, dividingNumber);
    }
}
