package com.codigo.ms_usuario.client;

import com.codigo.ms_usuario.aggregates.response.ReniecResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "reniec-client", url = "https://api.apis.net.pe/v2/reniec/")
public interface ReniecClient {
    @GetMapping("/dni")
    ReniecResponse getUsuarioReniec(@RequestParam("numero") String numero,
                                    @RequestHeader("Authorization") String authorization);
}
