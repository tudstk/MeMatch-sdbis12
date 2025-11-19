package org.example.mematch.infrastructure.persistence.jpa;

import org.example.mematch.domain.entities.Meme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemeRepository extends JpaRepository<Meme, Long> {}
