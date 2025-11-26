package com.web.eventos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.web.eventos.entities.Banner;

public interface BannerRepository extends JpaRepository<Banner, Integer> {
}