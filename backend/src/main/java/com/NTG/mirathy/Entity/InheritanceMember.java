package com.NTG.mirathy.Entity;


import com.NTG.mirathy.Entity.Enum.FixedShare;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.Enum.ShareType;
import com.NTG.mirathy.Entity.Enum.TaaasibRule;
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


    private String memberType; // son, daughter, wife ...


    @Column(name = "share_description")
    private String shareDescription;

    private String reason;

    private Integer memberCount;

    @Column(name = "individual_share_fraction")
    private String individualShareFraction;

    private Double shareValue;


    @ManyToOne
    @JoinColumn(name = "problem_id", nullable = false)
    private InheritanceProblem problem;
}
