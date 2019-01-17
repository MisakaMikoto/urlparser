package com.misakamikoto.urlparser.parser.controller;

import com.misakamikoto.urlparser.parser.ParserApplication;
import name.falgout.jeffrey.testing.junit5.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(value = {SpringExtension.class, MockitoExtension.class})
@SpringBootTest(classes = ParserApplication.class)
@AutoConfigureMockMvc
class ParserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testParse() throws Exception {
        String url = "https://www.naver.com";
        String type = "html";
        String dividingNumber = "10";

        this.mockMvc.perform(get("/parse")
                .param("url", url)
                .param("type", type)
                .param("dividingNumber", dividingNumber)

        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
    }
}