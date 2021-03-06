/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.headless.foundation.internal.resource.v1_0;

import com.liferay.headless.foundation.dto.v1_0.WebUrl;
import com.liferay.headless.foundation.resource.v1_0.WebUrlResource;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.service.WebsiteService;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/web-url.properties",
	scope = ServiceScope.PROTOTYPE, service = WebUrlResource.class
)
public class WebUrlResourceImpl extends BaseWebUrlResourceImpl {

	@Override
	public Page<WebUrl> getOrganizationWebUrlsPage(
			Long organizationId, Pagination pagination)
		throws Exception {

		Organization organization = _organizationService.getOrganization(
			organizationId);

		return Page.of(
			transform(
				_websiteService.getWebsites(
					organization.getModelClassName(),
					organization.getOrganizationId()),
				this::_toWebUrl));
	}

	@Override
	public Page<WebUrl> getUserAccountWebUrlsPage(
			Long userAccountId, Pagination pagination)
		throws Exception {

		User user = _userService.getUserById(userAccountId);

		return Page.of(
			transform(
				_websiteService.getWebsites(
					Contact.class.getName(), user.getContactId()),
				this::_toWebUrl));
	}

	@Override
	public WebUrl getWebUrl(Long webUrlId) throws Exception {
		return _toWebUrl(_websiteService.getWebsite(webUrlId));
	}

	private WebUrl _toWebUrl(Website website) throws PortalException {
		ListType listType = website.getType();

		return new WebUrl() {
			{
				id = website.getWebsiteId();
				url = website.getUrl();
				urlType = listType.getName();
			}
		};
	}

	@Reference
	private OrganizationService _organizationService;

	@Reference
	private UserService _userService;

	@Reference
	private WebsiteService _websiteService;

}