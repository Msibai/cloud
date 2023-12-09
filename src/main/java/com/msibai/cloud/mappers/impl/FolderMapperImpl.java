package com.msibai.cloud.mappers.impl;

import com.msibai.cloud.dtos.FolderDto;
import com.msibai.cloud.entities.Folder;
import com.msibai.cloud.mappers.Mapper;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/** Implementation of Mapper interface for mapping between Folder entity and FolderDto DTO. */
@AllArgsConstructor
@Component
public class FolderMapperImpl implements Mapper<Folder, FolderDto> {

  private final ModelMapper modelMapper;

  /**
   * Maps a Folder entity to a FolderDto.
   *
   * @param folder The Folder entity to be mapped.
   * @return The corresponding FolderDto.
   */
  @Override
  public FolderDto mapTo(Folder folder) {
    return modelMapper.map(folder, FolderDto.class);
  }

  /**
   * Maps a FolderDto to a Folder entity.
   *
   * @param folderDto The FolderDto to be mapped.
   * @return The corresponding Folder entity.
   */
  @Override
  public Folder mapFrom(FolderDto folderDto) {
    return modelMapper.map(folderDto, Folder.class);
  }
}
