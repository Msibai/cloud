package com.msibai.cloud.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuration class for creating and configuring the ModelMapper bean. */
@Configuration
public class MapperConfig {

  /**
   * Creates a bean for ModelMapper.
   *
   * @return The configured ModelMapper bean.
   */
  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }
}
