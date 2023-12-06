package com.msibai.cloud.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
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
  @NotEmpty(message = "Folder must have name")
  @Pattern(
      regexp = "^([a-zA-Z0-9][^*/><?\\\\|:]*)$",
      message =
          "Folder name is not valid. It should start with letters or numbers "
              + "and can contain any characters except '*', '/', '>', '<', '?', '|', or ':'.")
  private String folderName;

  private UUID parentFolderId;
  @NonNull private UUID userId;

  @Column(nullable = false)
  private boolean isRootFolder;

  @NonNull private LocalDate creationDate;
}
