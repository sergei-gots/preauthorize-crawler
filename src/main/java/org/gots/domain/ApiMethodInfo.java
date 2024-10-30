package org.gots.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by sergei on 29/10/2024
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class ApiMethodInfo {

    String name;
    List<String> authorities;

    public void printLog() {
        log.info("Method '{}'. Authorities:  {}", name, authorities);
    }
}
