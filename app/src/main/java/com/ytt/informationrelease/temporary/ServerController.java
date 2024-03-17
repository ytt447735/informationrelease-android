package com.ytt.informationrelease.temporary;

import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ServerController {
    @GetMapping("/text")
    public String text() {
        return "Hello Word";
    }
}
