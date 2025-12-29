package com.NTG.mirathy.Repository;

import com.NTG.mirathy.Entity.ActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivationTokenRepo extends JpaRepository<ActivationToken, Integer> {
    Optional<ActivationToken> findByToken(String token);
}
