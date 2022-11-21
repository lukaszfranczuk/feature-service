package com.capco.interview.features.services.model;

import lombok.Data;

@Data
public class Feature {

    private String guid;
    private String name;
    private Boolean globallyEnabled = false;
}
