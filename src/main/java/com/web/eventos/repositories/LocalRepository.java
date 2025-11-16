package com.web.eventos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.web.eventos.entities.Local;

public interface LocalRepository extends JpaRepository<Local, Integer> {
}
