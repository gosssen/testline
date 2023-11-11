package com.goss.testline.group;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import org.hibernate.annotations.Nationalized;

import java.util.List;

/**
 * @Author Kevin  on 2023/11/9
 */
@Entity
@Table(name = "group_info")
@Data
public class Group {
  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  @Column(name = "group_id")
  private Long id;
  @Column(name = "name")
  @Nationalized
  private String name;
  @Column(name = "url")
  private String url;
  @Column(name = "level")
  private Integer level;
  @Column(name = "last_name")
  private String lastName;
  @Transient
  private List<Group> nextGroup;

  public boolean hasNext() {
    return this.nextGroup != null && !this.nextGroup.isEmpty();
  }
}
