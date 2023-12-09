package com.msibai.cloud.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;

/** Entity class representing a folder stored in the system. */
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "folders")
public class Folder {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id; // Unique identifier for the folder

  @NonNull
  @NotEmpty(message = "Folder must have a name")
  @Pattern(
      regexp = "^([a-zA-Z0-9][^*/><?\\\\|:]*)$",
      message =
          "Folder name is not valid. It should start with letters or numbers "
              + "and can contain any characters except '*', '/', '>', '<', '?', '|', or ':'.")
  private String folderName; // Name of the folder

  private UUID parentFolderId; // ID of the parent folder

  @NonNull private UUID userId; // ID of the user associated with the folder

  @Column(nullable = false)
  private boolean isRootFolder; // Flag indicating if this folder is the root folder

  @NonNull private LocalDate creationDate; // Date when the folder was created
}
