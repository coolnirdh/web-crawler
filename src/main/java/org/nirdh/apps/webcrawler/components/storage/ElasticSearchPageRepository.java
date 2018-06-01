package org.nirdh.apps.webcrawler.components.storage;

import org.nirdh.apps.webcrawler.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.GetQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Repository;

/**
 * A distributed, scalable, and persistent data store that persists Page information to ES.
 */
@Repository
public class ElasticSearchPageRepository implements PageRepository {

    private final ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    public ElasticSearchPageRepository(ElasticsearchTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public void store(Page page) {
        elasticsearchTemplate.index(new IndexQueryBuilder()
                .withId(page.getUrl())
                .withObject(page)
                .build()
        );
    }

    @Override
    public void store(String url) {
        this.store(new Page(url, null));
    }

    @Override
    public Page findByUrl(String url) {
        GetQuery getQuery = new GetQuery();
        getQuery.setId(url);
        return elasticsearchTemplate.queryForObject(getQuery, Page.class);
    }

    @Override
    public boolean contains(String url) {
        Page page = this.findByUrl(url);
        return page != null;
    }
}