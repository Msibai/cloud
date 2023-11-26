package com.msibai.cloud.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import java.util.UUID;
import lombok.*;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "folders")
public class Folder {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NonNull
  @NotEmpty
  @Column(unique = true)
  private String folderName;

  @NonNull private UUID userId;
}
