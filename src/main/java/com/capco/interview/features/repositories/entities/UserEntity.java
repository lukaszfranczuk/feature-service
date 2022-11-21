package com.capco.interview.features.repositories.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "app_user")
@Data
@EqualsAndHashCode(exclude = "features")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String guid;
    private String username;
    private String password;

    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    private List<RoleEntity> roles;

    @ManyToMany
    @JoinTable(
            name = "user_feature",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "feature_id")}
    )
    private List<FeatureEntity> features = new ArrayList<>();
}
