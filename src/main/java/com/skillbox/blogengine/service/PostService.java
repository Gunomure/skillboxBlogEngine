package com.skillbox.blogengine.service;

import com.skillbox.blogengine.dto.ModeType;
import com.skillbox.blogengine.model.custom.PostUserCounts;
import com.skillbox.blogengine.orm.PostRepository;
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
public class PostService {

    final static Logger LOGGER = LogManager.getLogger(PostService.class);
    private final PostRepository postRepository;

    private static StandardServiceRegistry standardServiceRegistry;
    private static SessionFactory sessionFactory;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
        init();
    }

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

    public List<PostUserCounts> selectByParameters(int offset, int limit, ModeType mode) {
        List<PostUserCounts> posts = new ArrayList<>();
        String select = "select p.id AS id" +
                "     , UNIX_TIMESTAMP(p.time)            AS timestamp" +
                "     , a.id                              AS userId" +
                "     , a.name                            AS userName" +
                "     , p.title                           AS title" +
                "     , p.text                            AS announce" +
                "     , COUNT(CASE pv.value WHEN 1 THEN 1 ELSE NULL END)  AS likeCount" +
                "     , COUNT(CASE pv.value WHEN -1 THEN 1 ELSE NULL END) AS dislikeCount" +
                "     , COUNT(pc.id)                 AS commentCount" +
                "     , p.viewCount                      AS viewCount" +
                " FROM Post p" +
                " LEFT JOIN p.comments pc" +
                " LEFT JOIN p.author a" +
                " LEFT JOIN p.postVotes pv" +
                " WHERE p.isActive = TRUE AND p.moderationStatus = 'ACCEPTED' AND now() >= p.time" +
                " GROUP BY p.id, pc.post, pv.post";
        switch (mode) {
            case popular:
                select += " order by commentCount desc";
                break;
            case best:
                select += " order by likeCount desc";
                break;
            case early:
                select += " order by timestamp desc";
                break;
            default:
                // recent тоже считается дефолтным
                select += " order by timestamp asc";
                break;
        }
        try (Session session = sessionFactory.openSession()) {
            Query query = session.createQuery(select).setResultTransformer(Transformers.aliasToBean(PostUserCounts.class));
            posts = query.list();
            posts = posts.subList(offset, Math.min(offset + limit, posts.size()));
        } catch (Exception e) {
            LOGGER.error("Got error while selecting posts information, query: \n{}", select, e);
        }

        return posts;
    }

    public long count() {
        return postRepository.count();
    }
}
