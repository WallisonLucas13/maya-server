package com.example.ia.mayaAI.tools.cep;

import com.example.ia.mayaAI.models.Tool;
import com.example.ia.mayaAI.models.Tool.Parameters;
import com.example.ia.mayaAI.models.Tool.Parameters.Property;
import com.example.ia.mayaAI.services.cep.CepService;
import com.example.ia.mayaAI.tools.ToolDefinition;
import com.example.ia.mayaAI.tools.ToolProvider;
import com.example.ia.mayaAI.tools.ToolsFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CepTools implements ToolProvider {

    private final CepService cepService;

    public CepTools(CepService cepService) {
        this.cepService = cepService;
    }

    @Override
    public List<ToolDefinition> getToolDefinitions() {
        return List.of(
                new ToolDefinition(getAddressDetailsTool(), (tool, params) -> {
                    String cep = (String) params.get("cep");
                    return cepService.getAddressDetails(cep);
                }),
                new ToolDefinition(getGeocodeAccurateTool(), (tool, params) -> {
                    String address = (String) params.get("address");
                    return cepService.getGeocodeAccurate(address);
                })
        );
    }

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
