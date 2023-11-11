package com.goss.testline.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Nationalized;

/**
 * @Author Kevin  on 2023/11/11
 */
@Entity
@Table(name = "user_info")
@Data
public class UserInfo {
  @Id
  @Column(name = "line_id")
  private String lineId;
  @Nationalized
  @Column(name = "ask_local_name")
  private String askLocalName;
  @Column(name = "count")
  private Integer count;
}
