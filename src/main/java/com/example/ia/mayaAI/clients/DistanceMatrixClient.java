package com.example.ia.mayaAI.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "distanceMatrixClient",
        url = "${spring.distancematrix.api-base-url}"
)
public interface DistanceMatrixClient {

    @GetMapping("/geocode/json")
    Object getGeocodeAccurate(@RequestParam String address, @RequestParam String key);
}
