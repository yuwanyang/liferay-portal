<%--
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
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

String modelResource = ParamUtil.getString(request, "modelResource");
String modelResourceName = ResourceActionsUtil.getModelResource(request, modelResource);

long columnId = ParamUtil.getLong(request, "columnId");

ExpandoColumn column = null;

if (columnId > 0) {
	column = ExpandoColumnServiceUtil.fetchExpandoColumn(columnId);
}

int type = ParamUtil.getInteger(request, "type", 0);

if (column != null) {
	type = column.getType();
}

ExpandoBridge expandoBridge = ExpandoBridgeFactoryUtil.getExpandoBridge(company.getCompanyId(), modelResource);

UnicodeProperties properties = new UnicodeProperties(true);
Serializable defaultValue = null;

if (column != null) {
	properties = expandoBridge.getAttributeProperties(column.getName());
	defaultValue = expandoBridge.getAttributeDefault(column.getName());
}

boolean propertyHidden = GetterUtil.getBoolean(properties.get(ExpandoColumnConstants.PROPERTY_HIDDEN));
boolean propertyVisibleWithUpdatePermission = GetterUtil.getBoolean(properties.get(ExpandoColumnConstants.PROPERTY_VISIBLE_WITH_UPDATE_PERMISSION));
int propertyIndexType = GetterUtil.getInteger(properties.get(ExpandoColumnConstants.INDEX_TYPE));
boolean propertySecret = GetterUtil.getBoolean(properties.get(ExpandoColumnConstants.PROPERTY_SECRET));
int propertyHeight = GetterUtil.getInteger(properties.get(ExpandoColumnConstants.PROPERTY_HEIGHT), ExpandoColumnConstants.PROPERTY_HEIGHT_DEFAULT);
int propertyWidth = GetterUtil.getInteger(properties.get(ExpandoColumnConstants.PROPERTY_WIDTH));

String propertyDisplayType = ParamUtil.getString(request, "displayType", ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_FIELD);

if (column != null) {
	propertyDisplayType = GetterUtil.getString(properties.get(ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE));

	if (Validator.isNull(propertyDisplayType)) {
		propertyDisplayType = ExpandoColumnConstants.getDefaultDisplayTypeProperty(type, properties);
	}
}

PortletURL portletURL = renderResponse.createRenderURL();

portletURL.setParameter("mvcPath", "/view_attributes.jsp");
portletURL.setParameter("redirect", redirect);
portletURL.setParameter("modelResource", modelResource);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

renderResponse.setTitle(modelResourceName + ": " + ((column == null) ? LanguageUtil.get(request, "new-custom-field") : column.getName()));

PortletURL customFieldURL = renderResponse.createRenderURL();

customFieldURL.setParameter("mvcPath", "/view.jsp");
customFieldURL.setParameter("redirect", redirect);

PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "custom-field"), customFieldURL.toString());

PortletURL viewAttributesURL = renderResponse.createRenderURL();

viewAttributesURL.setParameter("mvcPath", "/view_attributes.jsp");
viewAttributesURL.setParameter("redirect", redirect);
viewAttributesURL.setParameter("modelResource", modelResource);

PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "view-attributes"), viewAttributesURL.toString());

PortletURL newCustomFieldURL = renderResponse.createRenderURL();

newCustomFieldURL.setParameter("mvcPath", "/edit/select_field_type.jsp");
newCustomFieldURL.setParameter("redirect", redirect);
newCustomFieldURL.setParameter("modelResource", modelResource);

PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "new-custom-field"), newCustomFieldURL.toString());

String displayType = LanguageUtil.get(request, propertyDisplayType);

if (column != null) {
	String editAttributeBreadcrumb = LanguageUtil.format(request, "edit-x", new Object[] {column.getName()}, false);

	PortalUtil.addPortletBreadcrumbEntry(request, editAttributeBreadcrumb, null);
}
else {
	String newAttributeBreadcrumb = LanguageUtil.format(request, "new-x", new Object[] {displayType}, false);

	PortalUtil.addPortletBreadcrumbEntry(request, newAttributeBreadcrumb, null);
}
%>

<liferay-ui:error exception="<%= ColumnNameException.class %>" message="please-enter-a-valid-name" />
<liferay-ui:error exception="<%= ColumnTypeException.class %>" message="please-select-a-valid-type" />
<liferay-ui:error exception="<%= DuplicateColumnNameException.class %>" message="please-enter-a-unique-name" />
<liferay-ui:error exception="<%= ValueDataException.class %>" message="please-enter-a-valid-value" />

<portlet:actionURL name='<%= (column == null) ? "addExpando" : "updateExpando" %>' var="editExpandoURL">
	<portlet:param name="mvcPath" value="/edit/expando.jsp" />
</portlet:actionURL>

<div class="container-fluid container-fluid-max-xl">
	<liferay-ui:breadcrumb
		showCurrentGroup="<%= false %>"
		showGuestGroup="<%= false %>"
		showLayout="<%= false %>"
		showPortletBreadcrumb="<%= true %>"
	/>
</div>

<liferay-frontend:edit-form
	action="<%= editExpandoURL %>"
>
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="columnId" type="hidden" value="<%= columnId %>" />
	<aui:input name="modelResource" type="hidden" value="<%= modelResource %>" />
	<aui:input name="type" type="hidden" value="<%= type %>" />

	<aui:input name="Property--display-type--" type="hidden" value="<%= propertyDisplayType %>" />

	<liferay-frontend:edit-form-body>
		<h2 class="sheet-title">

			<%
			String displayTypeLabel = LanguageUtil.get(request, propertyDisplayType);
			%>

			<%= LanguageUtil.format(request, column != null ? "edit-x" : "new-x", new Object[] {displayTypeLabel}, false) %>
		</h2>

		<liferay-frontend:fieldset-group>
			<aui:field-wrapper cssClass="form-group lfr-input-text-container">
				<c:choose>
					<c:when test="<%= column != null %>">
						<aui:input name="name" type="hidden" value="<%= column.getName() %>" />

						<aui:input label="field-name" name="key" type="resource" value="<%= column.getName() %>" />
					</c:when>
					<c:otherwise>
						<aui:input autoFocus="<%= windowState.equals(WindowState.MAXIMIZED) %>" label="field-name" name="name" required="<%= true %>" />
					</c:otherwise>
				</c:choose>

				<div class="form-text">
					<liferay-ui:message key="custom-field-key-help" />
				</div>
			</aui:field-wrapper>

			<%@ include file="/edit/default_value_input.jspf" %>

			<%@ include file="/edit/advanced_properties.jspf" %>
		</liferay-frontend:fieldset-group>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<aui:button type="submit" />

		<aui:button href="<%= redirect %>" type="cancel" />
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>

<%
PortalUtil.addPortletBreadcrumbEntry(request, modelResourceName, portletURL.toString());

if (column != null) {
	PortalUtil.addPortletBreadcrumbEntry(request, column.getName(), null);
}

PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, ((column == null) ? "add-attribute" : "edit")), currentURL);
%>