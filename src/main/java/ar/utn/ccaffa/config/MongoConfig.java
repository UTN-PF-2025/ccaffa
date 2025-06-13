package ar.utn.ccaffa.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

@Configuration
public class MongoConfig{

    @Bean
    public GridFsTemplate gridFsTemplate(
            org.springframework.data.mongodb.MongoDatabaseFactory dbFactory,
            org.springframework.data.mongodb.core.convert.MappingMongoConverter converter) {
        return new GridFsTemplate(dbFactory, converter);
    }
}
