package com.goss.testline.cctv;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CctvRepository extends JpaRepository<Cctv, Long> {
    List<Cctv> findByLabel(String label);

    List<Cctv> findByLabelContaining(String keyword);
}
