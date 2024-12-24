package com.sspu.nsrank.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "sentences")
public class Sentence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String source;
}
