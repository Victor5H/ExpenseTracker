package com.github.Victor5H.ExpenseTracker.be;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "tag")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tag", nullable = false, unique = true)
    private String tag;

}

