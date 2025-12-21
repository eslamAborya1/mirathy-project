package com.NTG.mirathy.Repository;

import com.NTG.mirathy.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
