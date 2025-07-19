package com.gustavoventieri.framework.useCase.utils;

import java.util.concurrent.ThreadLocalRandom;

import org.gustavoventieri.domain.utils.GenerateCodeUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Utilitário para geração de códigos numéricos aleatórios de 6 dígitos.
 */
@Component
@Slf4j
public class GenerateCodeUtilsImpl implements GenerateCodeUtils {

    /**
     * Gera um código numérico aleatório de 6 dígitos.
     *
     * @return código gerado como String
     */
    @Override
    public String generateCode() {
        int code = ThreadLocalRandom.current().nextInt(100000, 1000000);
        String codeStr = String.valueOf(code);
        log.debug("Código gerado: {}", codeStr);
        return codeStr;
    }
}
