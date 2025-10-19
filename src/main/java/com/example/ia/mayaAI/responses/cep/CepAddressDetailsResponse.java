package com.example.ia.mayaAI.responses.cep;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CepAddressDetailsResponse {
    private String cep;
    private String logradouro;
    private String complemento;
    private String unidade;
    private String bairro;
    private String localidade;
    private String uf;
    private String estado;
    private String regiao;
    private String ibge;
    private String gia;
    private String ddd;
    private String siafi;
}
