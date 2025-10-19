package com.example.ia.mayaAI.services.cep;

import com.example.ia.mayaAI.clients.DistanceMatrixClient;
import com.example.ia.mayaAI.clients.ViaCepClient;
import com.example.ia.mayaAI.responses.cep.CepAddressDetailsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CepService {

    private final ViaCepClient viaCepClient;
    private final DistanceMatrixClient distanceMatrixClient;
    private final String DISTANCEMATRIX_APIKEY;

    public CepService(
            ViaCepClient viaCepClient,
            DistanceMatrixClient distanceMatrixClient,
            @Value("${spring.distancematrix.apikey}") String apikey
    ) {
        this.viaCepClient = viaCepClient;
        this.distanceMatrixClient = distanceMatrixClient;
        this.DISTANCEMATRIX_APIKEY = apikey;
    }

    public CepAddressDetailsResponse getAddressDetails(String cep) {
        return viaCepClient.getAddressDetails(cep);
    }

    public Object getGeocodeAccurate(String address) {
        return distanceMatrixClient.getGeocodeAccurate(address, DISTANCEMATRIX_APIKEY);
    }
}
