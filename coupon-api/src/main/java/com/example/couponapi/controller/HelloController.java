package com.example.couponapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

  @GetMapping("/hello")
  public String hello() throws InterruptedException {
    Thread.sleep(500);
    return "hello!";
  } // 초당 2건을 처리 * N  (톰캣 기본 maxThread = 200)
}
