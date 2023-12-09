package com.msibai.cloud.entities;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

/** Entity class representing a file stored in the system. */
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "files")
public class File {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id; // Unique identifier for the file

  @NonNull
  @Column(unique = true)
  private String name; // Name of the file

  @NonNull private String contentType; // MIME type of the file content

  private byte @NonNull [] content; // Actual content of the file

  private long size; // Size of the file in bytes

  @NonNull private UUID userId; // ID of the user associated with the file

  @NonNull private UUID folderId; // ID of the folder where the file is stored

  @NonNull private String encryptionKey; // Encryption key used for the file

  @NonNull private String iv; // Initialization Vector (IV) for encryption
}
