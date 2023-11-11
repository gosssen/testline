package com.goss.testline.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author Kevin  on 2023/11/11
 */
@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo,String> {
}
