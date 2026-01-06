package com.garbigo.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "dashboard-service")
public interface OtherServiceClient {

    @GetMapping("/dashboard/{role}")
    String getDashboardUrl(@PathVariable("role") String role);
}