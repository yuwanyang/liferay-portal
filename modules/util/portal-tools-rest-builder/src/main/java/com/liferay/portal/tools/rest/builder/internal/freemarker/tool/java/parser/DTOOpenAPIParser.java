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

package com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java.parser;

import com.liferay.portal.kernel.util.CamelCaseUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java.parser.util.OpenAPIParserUtil;
import com.liferay.portal.tools.rest.builder.internal.freemarker.util.OpenAPIUtil;
import com.liferay.portal.vulcan.yaml.config.ConfigYAML;
import com.liferay.portal.vulcan.yaml.openapi.Items;
import com.liferay.portal.vulcan.yaml.openapi.OpenAPIYAML;
import com.liferay.portal.vulcan.yaml.openapi.Schema;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Peter Shin
 */
public class DTOOpenAPIParser {

	public static Map<String, String> getProperties(
		ConfigYAML configYAML, OpenAPIYAML openAPIYAML, Schema schema) {

		Map<String, String> javaDataTypeMap =
			OpenAPIParserUtil.getJavaDataTypeMap(configYAML, openAPIYAML);
		Map<String, String> properties = new TreeMap<>();

		Map<String, Schema> propertySchemas = _getPropertySchemas(schema);

		for (Map.Entry<String, Schema> entry : propertySchemas.entrySet()) {
			String propertySchemaName = entry.getKey();
			Schema propertySchema = entry.getValue();

			String javaDataType = _getJavaDataType(
				javaDataTypeMap, propertySchema, propertySchemaName);
			String propertyName = _getPropertyName(
				propertySchema, propertySchemaName);

			properties.put(propertyName, javaDataType);
		}

		return properties;
	}

	public static Map<String, String> getProperties(
		ConfigYAML configYAML, OpenAPIYAML openAPIYAML, String schemaName) {

		Map<String, Schema> schemas = OpenAPIUtil.getAllSchemas(openAPIYAML);

		return getProperties(configYAML, openAPIYAML, schemas.get(schemaName));
	}

	public static Schema getPropertySchema(String propertyName, Schema schema) {
		Map<String, Schema> propertySchemas = _getPropertySchemas(schema);

		for (Map.Entry<String, Schema> entry : propertySchemas.entrySet()) {
			String propertySchemaName = entry.getKey();
			Schema propertySchema = entry.getValue();

			String curPropertyName = _getPropertyName(
				propertySchema, propertySchemaName);

			if (StringUtil.equalsIgnoreCase(curPropertyName, propertyName)) {
				return propertySchema;
			}
		}

		return null;
	}

	private static String _getJavaDataType(
		Map<String, String> javaDataTypeMap, Schema propertySchema,
		String propertySchemaName) {

		List<String> enumValues = propertySchema.getEnumValues();

		if ((enumValues != null) && !enumValues.isEmpty()) {
			return StringUtil.upperCaseFirstLetter(propertySchemaName);
		}

		Items items = propertySchema.getItems();
		String type = propertySchema.getType();

		if (StringUtil.equals(type, "array") && (items != null) &&
			StringUtil.equalsIgnoreCase(items.getType(), "object")) {

			String name = StringUtil.upperCaseFirstLetter(propertySchemaName);

			if (items != null) {
				name = OpenAPIUtil.formatSingular(name);
			}

			if (javaDataTypeMap.containsKey(name)) {
				return OpenAPIParserUtil.getArrayClassName(name);
			}
		}

		if (StringUtil.equalsIgnoreCase(type, "object")) {
			String name = StringUtil.upperCaseFirstLetter(propertySchemaName);

			if (items != null) {
				name = OpenAPIUtil.formatSingular(name);
			}

			if (javaDataTypeMap.containsKey(name)) {
				return name;
			}
		}

		return OpenAPIParserUtil.getJavaDataType(
			javaDataTypeMap, propertySchema);
	}

	private static String _getPropertyName(
		Schema propertySchema, String propertySchemaName) {

		String name = CamelCaseUtil.toCamelCase(propertySchemaName);

		if (StringUtil.equalsIgnoreCase(propertySchema.getType(), "object")) {
			if (propertySchema.getItems() != null) {
				return OpenAPIUtil.formatSingular(name);
			}
		}

		return name;
	}

	private static Map<String, Schema> _getPropertySchemas(Schema schema) {
		Map<String, Schema> propertySchemas = null;

		Items items = schema.getItems();

		if (items != null) {
			propertySchemas = items.getPropertySchemas();
		}
		else if (schema.getAllOfSchemas() != null) {
			propertySchemas = OpenAPIParserUtil.getAllOfPropertySchemas(schema);
		}
		else {
			propertySchemas = schema.getPropertySchemas();
		}

		if (propertySchemas == null) {
			return Collections.emptyMap();
		}

		return propertySchemas;
	}

}