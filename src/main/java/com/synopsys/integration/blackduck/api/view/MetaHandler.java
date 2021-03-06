/**
 * hub-common
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.blackduck.api.view;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.blackduck.api.core.HubView;
import com.synopsys.integration.blackduck.api.core.ResourceLink;
import com.synopsys.integration.blackduck.api.core.ResourceMetadata;
import com.synopsys.integration.blackduck.exception.HubIntegrationException;
import com.synopsys.integration.log.IntLogger;

public class MetaHandler {
    private final IntLogger logger;

    public MetaHandler(final IntLogger logger) {
        this.logger = logger;
    }

    public boolean hasLink(final HubView view, final String linkKey) throws HubIntegrationException {
        final ResourceMetadata meta = view._meta;
        if (meta == null) {
            return false;
        }
        final List<ResourceLink> links = meta.links;
        if (links == null) {
            return false;
        }
        for (final ResourceLink link : links) {
            if (link.rel.equals(linkKey)) {
                return true;
            }
        }
        return false;
    }

    public String getFirstLink(final HubView view, final String linkKey) throws HubIntegrationException {
        final List<ResourceLink> links = getLinkViews(view);
        final StringBuilder linksAvailable = new StringBuilder();
        linksAvailable.append("Could not find the link '" + linkKey + "', these are the available links : ");
        int i = 0;
        for (final ResourceLink link : links) {
            if (link.rel.equals(linkKey)) {
                return link.href;
            }
            if (i > 0) {
                linksAvailable.append(", ");
            }
            linksAvailable.append("'" + link.rel + "'");
            i++;
        }
        linksAvailable.append(". For View : " + view._meta.href);
        throw new HubIntegrationException(linksAvailable.toString());
    }

    public String getFirstLinkSafely(final HubView view, final String linkKey) {
        try {
            final String link = getFirstLink(view, linkKey);
            return link;
        } catch (final HubIntegrationException e) {
            logger.debug("Link '" + linkKey + "' not found on view : " + view.json, e);
            return null;
        }
    }

    public List<String> getLinks(final HubView view, final String linkKey) throws HubIntegrationException {
        final List<ResourceLink> links = getLinkViews(view);
        final List<String> linkHrefs = new ArrayList<>();
        final StringBuilder linksAvailable = new StringBuilder();
        linksAvailable.append("Could not find the link '" + linkKey + "', these are the available links : ");
        int i = 0;
        for (final ResourceLink link : links) {
            if (link.rel.equals(linkKey)) {
                linkHrefs.add(link.href);
            }
            if (i > 0) {
                linksAvailable.append(", ");
            }
            linksAvailable.append("'" + link.rel + "'");
            i++;
        }
        if (!linkHrefs.isEmpty()) {
            return linkHrefs;
        }
        linksAvailable.append(". For View : " + view._meta.href);
        throw new HubIntegrationException(linksAvailable.toString());
    }

    public ResourceMetadata getMetaView(final HubView view) throws HubIntegrationException {
        final ResourceMetadata meta = view._meta;
        if (meta == null) {
            throw new HubIntegrationException("Could not find meta information for this view : " + view.json);
        }
        return meta;
    }

    public List<ResourceLink> getLinkViews(final HubView view) throws HubIntegrationException {
        final ResourceMetadata meta = getMetaView(view);
        final List<ResourceLink> links = meta.links;
        if (links == null) {
            throw new HubIntegrationException("Could not find any links for this view : " + view.json);
        }
        return links;
    }

    public List<String> getAllowedMethods(final HubView view) throws HubIntegrationException {
        final ResourceMetadata meta = getMetaView(view);
        return meta.allow;
    }

    public String getHref(final HubView view) throws HubIntegrationException {
        final ResourceMetadata meta = getMetaView(view);
        final String href = meta.href;
        if (href == null) {
            if (logger != null) {
                logger.error("Hub View has no href : " + view.json);
            }
            throw new HubIntegrationException("This Hub view does not have any href information.");
        }
        return href;
    }

}
