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

package com.liferay.dynamic.data.mapping.service;

import aQute.bnd.annotation.ProviderType;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Provides the remote service utility for DDMStructureLayout. This utility wraps
 * <code>com.liferay.dynamic.data.mapping.service.impl.DDMStructureLayoutServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see DDMStructureLayoutService
 * @generated
 */
@ProviderType
public class DDMStructureLayoutServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.dynamic.data.mapping.service.impl.DDMStructureLayoutServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. Always use <code>DDMStructureLayoutServiceUtil</code> to access the ddm structure layout remote service.
	 */
	public static java.util.List
		<com.liferay.dynamic.data.mapping.model.DDMStructureLayout>
				getStructureLayouts(long groupId, int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getStructureLayouts(groupId, start, end);
	}

	public static int getStructureLayoutsCount(long groupId) {
		return getService().getStructureLayoutsCount(groupId);
	}

	public static DDMStructureLayoutService getService() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker
		<DDMStructureLayoutService, DDMStructureLayoutService> _serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			DDMStructureLayoutService.class);

		ServiceTracker<DDMStructureLayoutService, DDMStructureLayoutService>
			serviceTracker =
				new ServiceTracker
					<DDMStructureLayoutService, DDMStructureLayoutService>(
						bundle.getBundleContext(),
						DDMStructureLayoutService.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}

}