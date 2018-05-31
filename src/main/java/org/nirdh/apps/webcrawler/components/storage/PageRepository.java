package org.nirdh.apps.webcrawler.components.storage;

import org.nirdh.apps.webcrawler.domain.Page;

/**
 * A contract definition for services provided by PageRepository implementations.
 */
public interface PageRepository {

    void store(Page page);

    void store(String url);

    Page findByUrl(String url);

    boolean contains(String url);
}
