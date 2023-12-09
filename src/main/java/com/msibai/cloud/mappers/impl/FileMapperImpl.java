package com.msibai.cloud.mappers.impl;

import com.msibai.cloud.dtos.FileDto;
import com.msibai.cloud.dtos.SlimFileDto;
import com.msibai.cloud.entities.File;
import com.msibai.cloud.mappers.Mapper;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Implementation of Mapper interface for mapping between File entity and FileDto/SlimFileDto DTOs.
 */
@Component
@AllArgsConstructor
public class FileMapperImpl implements Mapper<File, FileDto> {

  private final ModelMapper modelMapper;

  /**
   * Maps a File entity to a FileDto.
   *
   * @param file The File entity to be mapped.
   * @return The corresponding FileDto.
   */
  @Override
  public FileDto mapTo(File file) {
    return modelMapper.map(file, FileDto.class);
  }

  /**
   * Maps a FileDto to a File entity.
   *
   * @param fileDto The FileDto to be mapped.
   * @return The corresponding File entity.
   */
  @Override
  public File mapFrom(FileDto fileDto) {
    return modelMapper.map(fileDto, File.class);
  }

  /**
   * Maps a File entity to a SlimFileDto.
   *
   * @param file The File entity to be mapped.
   * @return The corresponding SlimFileDto.
   */
  public SlimFileDto mapToSlim(File file) {
    return modelMapper.map(file, SlimFileDto.class);
  }
}
