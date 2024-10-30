package org.gots.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergei on 29/10/2024
 */
@Getter
@Setter
public class ApiControllerInfo implements Comparable<ApiControllerInfo> {

    private String className;
    private String fileContent;
    private List<ApiMethodInfo> methods = new ArrayList<>();

    @Override
    public int compareTo(ApiControllerInfo o) {
        return className.compareTo(o.className);
    }
}
