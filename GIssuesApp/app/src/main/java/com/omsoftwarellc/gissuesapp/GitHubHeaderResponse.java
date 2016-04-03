package com.omsoftwarellc.gissuesapp;

/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/

import java.net.HttpURLConnection;

/**
 * GitHub API response class that provides the parsed response links
 * to the first, previous, next, and last responses.
 * Aquired from https://github.com/eclipse/egit-github/tree/master/org.eclipse.egit.github.core/src/org/eclipse/egit/github/core/client
 */
public class GitHubHeaderResponse {

    /**
     * HTTP response
     */
    protected final HttpURLConnection response;

    /**
     * Links to other pages
     */
    protected PageLinks links;

    /**
     * Create response
     *
     * @param response
     */
    public GitHubHeaderResponse(HttpURLConnection response) {
        this.response = response;
    }

    /**
     * Get header value
     *
     * @param name
     * @return value
     */
    public String getHeader(String name) {
        return response.getHeaderField(name);
    }

    /**
     * Get page links
     *
     * @return links
     */
    protected PageLinks getLinks() {
        if (links == null)
            links = new PageLinks(this);
        return links;
    }

    /**
     * Get link uri to first page
     *
     * @return possibly null uri
     */
    public String getFirst() {
        return getLinks().getFirst();
    }

    /**
     * Get link uri to previous page
     *
     * @return possibly null uri
     */
    public String getPrevious() {
        return getLinks().getPrev();
    }

    /**
     * Get link uri to next page
     *
     * @return possibly null uri
     */
    public String getNext() {
        return getLinks().getNext();
    }

    /**
     * Get link uri to last page
     *
     * @return possibly null uri
     */
    public String getLast() {
        return getLinks().getLast();
    }

}