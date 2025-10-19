package com.example.ia.mayaAI.tools.cep;

import com.example.ia.mayaAI.models.Tool;
import com.example.ia.mayaAI.models.Tool.Parameters;
import com.example.ia.mayaAI.models.Tool.Parameters.Property;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CepTools{
    public Tool getAddressDetailsTool() {
        return Tool.builder()
                .name("cep_address_details")
                .description("Busca detalhes de endereço a partir do CEP fornecido.")
                .parameters(Parameters.builder()
                        .properties(Map.of(
                                "cep", Property.builder()
                                        .type("string")
                                        .description("Cep no formato 00000-000")
                                        .build()
                        ))
                        .required(new String[]{"cep"})
                        .build())
                .build();
    }

    public Tool getGeocodeAccurateTool() {
        return Tool.builder()
                .name("get_geocode_accurate")
                .description("Busca coordenadas geográficas precisas a partir do endereço fornecido.")
                .parameters(Parameters.builder()
                        .properties(Map.of(
                                "address", Property.builder()
                                        .type("string")
                                        .description("Endereço completo no formato, por exemplo, 'Rua ABC' ou 'Av ABC'. Não é permitido informar um CEP.")
                                        .build()
                        ))
                        .required(new String[]{"address"})
                        .build())
                .build();
    }
}
