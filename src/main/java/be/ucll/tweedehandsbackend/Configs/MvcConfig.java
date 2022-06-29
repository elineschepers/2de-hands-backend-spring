package be.ucll.tweedehandsbackend.Configs;

import be.ucll.tweedehandsbackend.DTOs.*;
import be.ucll.tweedehandsbackend.Jobs.ConvertMediaImages;
import be.ucll.tweedehandsbackend.Jobs.DeleteMediaFiles;
import be.ucll.tweedehandsbackend.Models.*;
import org.jobrunr.jobs.mappers.JobMapper;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.jobrunr.storage.StorageProvider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${storage.location}")
    private String StoragePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/storage/**")
            .addResourceLocations("file:" + StoragePath);
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Map Course model
        modelMapper.typeMap(Course.class, CourseDTO.class).addMappings(mapper -> {
            mapper.map(Course::getUuid, CourseDTO::setUuid);
        });
        // Map Offer model
        modelMapper.typeMap(Offer.class, OfferDTO.class).addMappings(mapper -> {
            mapper.map(Offer::getUuid, OfferDTO::setUuid);
        });

        modelMapper.typeMap(Offer.class, OfferAuthDTO.class).addMappings(mapper -> {
            mapper.map(Offer::getUuid, OfferAuthDTO::setUuid);
        });

        // Map Program model
        modelMapper.typeMap(Program.class, ProgramDTO.class).addMappings(mapper -> {
            mapper.map(Program::getUuid, ProgramDTO::setUuid);
        });

        // Map User model
        modelMapper.typeMap(User.class, UserDTO.class).addMappings(mapper -> {
            mapper.map(User::getUuid, UserDTO::setUuid);
        });

        modelMapper.typeMap(User.class, UserOfferDTO.class).addMappings(mapper -> {
            mapper.map(User::getUuid, UserOfferDTO::setUuid);
        });

        // Map Role model
        modelMapper.typeMap(Role.class, RoleDTO.class).addMappings(mapper -> {
            mapper.map(Role::getMap, RoleDTO::setMap);
        });

        // Mapper for MediaDTO to Media
        modelMapper.typeMap(Media.class, MediaDTO.class).addMappings(mapper -> {
            mapper.map(Media::getUuid, MediaDTO::setUuid);
            mapper.map(Media::getResponsiveImages, MediaDTO::setSrcSet);
        });

        // Mapper for MediaDTO Preview to Media
        modelMapper.typeMap(Media.class, PreviewMediaDTO.class).addMappings(mapper -> {
            mapper.map(Media::getUuid, PreviewMediaDTO::setUuid);
            mapper.map(Media::getResponsiveImages, PreviewMediaDTO::setSrcSet);
        });

        return modelMapper;
    }

    @Bean
    public StorageProvider storageProvider(JobMapper jobMapper) {
        InMemoryStorageProvider storageProvider = new InMemoryStorageProvider();
        storageProvider.setJobMapper(jobMapper);
        return storageProvider;
    }

    @Bean
    public ConvertMediaImages convertMediaImages() {
        return new ConvertMediaImages();
    }

    @Bean
    public DeleteMediaFiles deleteMediaFiles() {
        return new DeleteMediaFiles();
    }
}
