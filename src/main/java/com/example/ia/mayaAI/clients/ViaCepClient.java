package com.example.ia.mayaAI.clients;

import com.example.ia.mayaAI.responses.cep.CepAddressDetailsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "viaCepClient",
        url = "${spring.viacep.api-base-url}"
)
public interface ViaCepClient {

    @GetMapping("/{cep}/json/")
    CepAddressDetailsResponse getAddressDetails(@PathVariable ("cep") String cep);
}
