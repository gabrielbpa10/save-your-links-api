package com.save.link.api.model.repository;

import com.save.link.api.model.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {

    Optional<List<Link>> findByDescURLContaining(String filter);

    boolean existsByDescURL(String descURL);
}
