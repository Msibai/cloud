package com.msibai.cloud.utilities;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a paginated response containing a list of elements along with pagination metadata.
 *
 * @param <T> The type of elements in the response.
 */
@Data
@AllArgsConstructor
public class PagedResponse<T> {
  private List<T> content; // List of elements in the current page
  private int page; // Current page number
  private int size; // Number of elements in the page
  private long totalElements; // Total number of elements across all pages
  private int totalPages; // Total number of pages
}
