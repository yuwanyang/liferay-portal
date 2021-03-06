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

package com.liferay.portal.vulcan.internal.jaxrs.exception.mapper;

import com.liferay.portal.kernel.exception.NoSuchModelException;

import java.lang.reflect.Method;

import javax.ws.rs.DELETE;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Converts any {@code NoSuchModelException} to a {@code 404} error.
 *
 * <p>In the case of a DELETE request a {@code 204} (NO-CONTENT) response is
 * returned so the request is idempotent.
 *
 * @author Alejandro Hernández
 * @review
 */
public class NoSuchModelExceptionMapper
	implements ExceptionMapper<NoSuchModelException> {

	@Override
	public Response toResponse(NoSuchModelException nsme) {
		Method resourceMethod = resourceInfo.getResourceMethod();

		if (resourceMethod.isAnnotationPresent(DELETE.class)) {
			return Response.status(
				Response.Status.NO_CONTENT
			).build();
		}

		return Response.status(
			Response.Status.NOT_FOUND
		).type(
			MediaType.TEXT_PLAIN
		).entity(
			nsme.getMessage()
		).build();
	}

	@Context
	protected ResourceInfo resourceInfo;

}