package com.skillbox.blogengine.service;

import com.skillbox.blogengine.model.custom.TagUsageStatistics;
import com.skillbox.blogengine.orm.PostRepository;
import com.skillbox.blogengine.orm.TagRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagService {
    final static Logger LOGGER = LogManager.getLogger(PostService.class);
    private final TagRepository tagRepository;
    private final PostRepository postRepository;


    public TagService(TagRepository tagRepository, PostRepository postRepository) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
        init();
    }

    private static StandardServiceRegistry standardServiceRegistry;
    private static SessionFactory sessionFactory;

    private void init() {
        if (sessionFactory == null) try {
            standardServiceRegistry = new StandardServiceRegistryBuilder().configure().build();
            MetadataSources metadataSources = new MetadataSources(standardServiceRegistry);
            Metadata metadata = metadataSources.getMetadataBuilder().build();
            sessionFactory = metadata.getSessionFactoryBuilder().build();
        } catch (Exception e) {
            LOGGER.error("Got error while initializing sessionFactory for PostService", e);
            if (standardServiceRegistry != null) {
                StandardServiceRegistryBuilder.destroy(standardServiceRegistry);
            }
        }
    }

    public List<TagUsageStatistics> selectTagsStatistics(String tagPartName) {
        LOGGER.debug("Select tags statistics for name: {}", tagPartName);
        List<TagUsageStatistics> tagUsageStatistics = new ArrayList<>();
        String select = "SELECT t.name AS name, COUNT(p.id) AS useInPostsCount" +
                " FROM Post p" +
                "         join p.tagToPost t2p" +
                "         join t2p.tag t" +
                " WHERE t.name LIKE CONCAT(:tagPartName, '%')" +
                " group by t.name" +
                " ORDER BY useInPostsCount DESC";
//        long totalPostsCount = postRepository.count();
//        LOGGER.info("Total posts count: {}", totalPostsCount);

        try (Session session = sessionFactory.openSession()) {
            Query query = session.createQuery(select).setParameter("tagPartName", tagPartName).setResultTransformer(Transformers.aliasToBean(TagUsageStatistics.class));
            tagUsageStatistics = query.list();

        } catch (Exception e) {
            LOGGER.error("Got error while selecting posts information, query: \n{}", select, e);
        }
        LOGGER.info("Tags statistics got: {}", tagUsageStatistics.toString());
        return tagUsageStatistics;
    }
}
