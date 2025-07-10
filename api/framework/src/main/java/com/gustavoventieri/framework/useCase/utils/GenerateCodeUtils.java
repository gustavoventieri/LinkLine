package com.gustavoventieri.framework.useCase.utils;

import java.util.concurrent.ThreadLocalRandom;


import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Utilitário para geração de códigos numéricos aleatórios de 6 dígitos.
 */
@Service
@Slf4j
public class GenerateCodeUtils {

    /**
     * Gera um código numérico aleatório de 6 dígitos.
     *
     * @return código gerado como String
     */
    public static String generateCode() {
        int code = ThreadLocalRandom.current().nextInt(100000, 1000000);
        String codeStr = String.valueOf(code);
        log.debug("Código gerado: {}", codeStr);
        return codeStr;
    }
}
