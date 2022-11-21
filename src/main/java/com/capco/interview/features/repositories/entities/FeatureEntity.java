package com.capco.interview.features.repositories.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "feature")
@Data
@EqualsAndHashCode(exclude = "users")
public class FeatureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long featureId;

    private String guid;

    private String name;

    private Boolean globallyEnabled = false;

    @ManyToMany
    @JoinTable(
            name = "user_feature",
            joinColumns = {@JoinColumn(name = "feature_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private List<UserEntity> users = new ArrayList<>();
}
