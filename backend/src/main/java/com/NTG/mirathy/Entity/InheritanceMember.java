package com.NTG.mirathy.Entity;


import com.NTG.mirathy.Entity.Enum.FixedShare;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.Enum.ShareType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inheritance_member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InheritanceMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private HeirType memberType; // son, daughter, wife ...

    @Enumerated(EnumType.STRING)
    private ShareType shareFraction;

    @Enumerated(EnumType.STRING)
    private FixedShare fixedShare;

    private Double shareValue;


    private String description;

    private Integer memberCount;

    @ManyToOne
    @JoinColumn(name = "problem_id", nullable = false)
    private InheritanceProblem problem;
}
