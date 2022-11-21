package com.capco.interview.features.repositories.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "role")
@Data
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;
    private String name;
}
