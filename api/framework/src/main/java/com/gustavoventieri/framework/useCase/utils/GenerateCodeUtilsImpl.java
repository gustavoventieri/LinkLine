package com.gustavoventieri.framework.useCase.utils;

import java.util.concurrent.ThreadLocalRandom;

import org.gustavoventieri.domain.utils.GenerateCodeUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility for generating random 6-digit numeric codes.
 */
@Component
@Slf4j
public class GenerateCodeUtilsImpl implements GenerateCodeUtils {

    /**
     * Generates a random 6-digit numeric code.
     *
     * @return generated code as a String
     */
    @Override
    public String generateCode() {
        int code = ThreadLocalRandom.current().nextInt(100000, 1000000);
        String codeStr = String.valueOf(code);
        log.debug("Generated code: {}", codeStr);
        return codeStr;
    }
}
