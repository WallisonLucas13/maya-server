package com.example.ia.mayaAI.tools;

import com.example.ia.mayaAI.services.cep.CepService;
import com.example.ia.mayaAI.models.Tool;
import com.example.ia.mayaAI.tools.cep.CepTools;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Log4j2
@Service
public class ToolsFactory {

    private final CepService cepService;
    private final CepTools cepTools;
    private final Map<String, ToolFunctionPair> tools;

    public ToolsFactory(CepService cepService, CepTools cepTools) {
        this.cepService = cepService;
        this.cepTools = cepTools;
        this.tools = registerTools();
    }

    private Map<String, ToolFunctionPair> registerTools(){
        return Map.of(
                cepTools.getAddressDetailsTool().getName(), new ToolFunctionPair(
                        cepTools.getAddressDetailsTool(),
                        (tool, params) -> {
                            String cep = (String) params.get("cep");
                            log.info("Executando ferramenta: {} com parâmetro cep={}", tool.getName(), cep);
                            return cepService.getAddressDetails(cep);
                        }
                ),
                cepTools.getGeocodeAccurateTool().getName(), new ToolFunctionPair(
                        cepTools.getGeocodeAccurateTool(),
                        (tool, params) -> {
                            String address = (String) params.get("address");
                            log.info("Executando ferramenta: {} com parâmetro address={}", tool.getName(), address);
                            return cepService.getGeocodeAccurate(address);
                        }
                )
        );
    }

    public List<Tool> getAllTools() {
        return tools.values().stream()
                .map(ToolFunctionPair::tool)
                .toList();
    }

    public Tool getToolByName(String name) {
        ToolFunctionPair pair = tools.get(name);
        if (pair == null) {
            throw new IllegalArgumentException("Tool not found: " + name);
        }
        return pair.tool();
    }

    public Object executeToolFunction(String name, Map<String, Object> params) {
        ToolFunctionPair pair = tools.get(name);
        if (pair == null) {
            throw new IllegalArgumentException("Tool not found: " + name);
        }
        return pair.function().apply(pair.tool(), params);
    }

    private record ToolFunctionPair(Tool tool, BiFunction<Tool, Map<String, Object>, Object> function) {}
}
