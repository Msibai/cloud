package com.msibai.cloud.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
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
  @Size(min = 1, max = 7, message = "Name length must be between 3 and 30 characters")
  @NotEmpty
  @Column(unique = true)
  private String folderName;

  @NonNull private UUID userId;
}
