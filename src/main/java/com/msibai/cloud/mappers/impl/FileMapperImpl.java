package com.msibai.cloud.mappers.impl;

import com.msibai.cloud.dtos.FileDto;
import com.msibai.cloud.dtos.SlimFileDto;
import com.msibai.cloud.entities.File;
import com.msibai.cloud.mappers.Mapper;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FileMapperImpl implements Mapper<File, FileDto> {

  private final ModelMapper modelMapper;

  @Override
  public FileDto mapTo(File file) {
    return modelMapper.map(file, FileDto.class);
  }

  @Override
  public File mapFrom(FileDto fileDto) {
    return modelMapper.map(fileDto, File.class);
  }

  public SlimFileDto mapToSlim(File file) {
    return modelMapper.map(file, SlimFileDto.class);
  }
}
