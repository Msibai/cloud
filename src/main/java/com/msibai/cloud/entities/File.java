package com.msibai.cloud.entities;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "files")
public class File {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NonNull
  @Column(unique = true)
  private String name;

  @NonNull private String contentType;

  private byte @NonNull [] content;

  private long size;

  @NonNull private UUID userId;

  private UUID folderId;
}
