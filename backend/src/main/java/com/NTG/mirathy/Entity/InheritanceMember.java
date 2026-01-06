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
    private HeirType memberType;

    @Enumerated(EnumType.STRING)
    private ShareType shareType;

    @Enumerated(EnumType.STRING)
    private FixedShare fixedShare;

    private Double amountPerPerson;
    private Double totalAmount;

    private Integer memberCount;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private InheritanceProblem problem;
}
