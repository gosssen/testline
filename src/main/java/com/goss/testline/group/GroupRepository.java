package com.goss.testline.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author Kevin  on 2023/11/9
 */
@Repository
public interface GroupRepository extends JpaRepository<Group,Long> {
  List<Group> findByNameLikeAndLevel(String localName,Integer level);

  List<Group> findByLastNameAndNameLikeAndLevel(String lastName, String name, Integer level);

  List<Group> findByLastName(String localName);

  List<Group> findByLevel(Integer level);
}
