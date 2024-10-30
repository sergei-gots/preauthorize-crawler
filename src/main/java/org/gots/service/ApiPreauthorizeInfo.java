package org.gots.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gots.domain.ApiControllerInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergei on 29/10/2024
 */
@Getter
@Setter
@NoArgsConstructor
public class ApiPreauthorizeInfo {

    private String baseSwaggerUrl;

    private final List<String> foundAuthorities = new ArrayList<>();

    List<ApiControllerInfo> controllerInfos = new ArrayList<>();
}
