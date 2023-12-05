package com.msibai.cloud.mappers.impl;

import com.msibai.cloud.dtos.FolderDto;
import com.msibai.cloud.entities.Folder;
import com.msibai.cloud.mappers.Mapper;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class FolderMapperImpl implements Mapper<Folder, FolderDto> {

  private final ModelMapper modelMapper;

  @Override
  public FolderDto mapTo(Folder folder) {
    return modelMapper.map(folder, FolderDto.class);
  }

  @Override
  public Folder mapFrom(FolderDto folderDto) {
    return modelMapper.map(folderDto, Folder.class);
  }
}
