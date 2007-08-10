/**
 * Copyright (c) 2000-2007 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.portlet.journal.action;

import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.Constants;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.WebKeys;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portlet.journal.util.JournalUtil;
import com.liferay.portlet.journal.util.comparator.ArticleDisplayDateComparator;
import com.liferay.portlet.journal.util.comparator.ArticleModifiedDateComparator;
import com.liferay.util.DateUtil;
import com.liferay.util.GetterUtil;
import com.liferay.util.ParamUtil;
import com.liferay.util.StringUtil;
import com.liferay.util.Validator;
import com.liferay.util.dao.DAOParamUtil;
import com.liferay.util.servlet.ServletResponseUtil;

import java.io.StringReader;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * <a href="GetArticlesAction.java.html"><b><i>View Source</i></b></a>
 *
 * @author Raymond Augé
 * @author Brian Wing Shun Chan
 *
 */
public class GetArticlesAction extends Action {

	public ActionForward execute(
			ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res)
		throws Exception {

		try {
			List articles = getArticles(req);

			String fileName = null;
			byte[] byteArray = getContent(req, articles);

			ServletResponseUtil.sendFile(
				res, fileName, byteArray,
				Constants.TEXT_XML + "; charset=UTF-8");

			return null;
		}
		catch (Exception e) {
			req.setAttribute(PageContext.EXCEPTION, e);

			return mapping.findForward(Constants.COMMON_ERROR);
		}
	}

	protected List getArticles(HttpServletRequest req) throws Exception {
		long companyId = PortalUtil.getCompanyId(req);
		long groupId = DAOParamUtil.getLong(req, "groupId");
		String articleId = null;
		Double version = null;
		String title = null;
		String description = null;
		String content = null;
		String type = DAOParamUtil.getString(req, "type");
		String[] structureIds = StringUtil.split(
				DAOParamUtil.getString(req, "structureId"));
		String templateId = DAOParamUtil.getString(req, "templateId");

		Date displayDateGT = null;
		
		if (structureIds.length == 0) {
			structureIds = new String[]{null};
		}
		
		String displayDateGTParam = ParamUtil.getString(req, "displayDateGT");

		if (Validator.isNotNull(displayDateGTParam)) {
			DateFormat displayDateGTFormat = DateUtil.getISOFormat(
				displayDateGTParam);

			displayDateGT = GetterUtil.getDate(
				displayDateGTParam, displayDateGTFormat);
		}

		if (_log.isDebugEnabled()) {
			_log.debug("displayDateGT is " + displayDateGT);
		}

		Date displayDateLT = null;

		String displayDateLTParam = ParamUtil.getString(req, "displayDateLT");

		if (Validator.isNotNull(displayDateLTParam)) {
			DateFormat displayDateLTFormat = DateUtil.getISOFormat(
				displayDateLTParam);

			displayDateLT = GetterUtil.getDate(
				displayDateLTParam, displayDateLTFormat);
		}

		if (_log.isDebugEnabled()) {
			_log.debug("displayDateLT is " + displayDateLT);
		}

		Boolean approved = Boolean.TRUE;
		Boolean expired = Boolean.FALSE;
		Date reviewDate = null;
		boolean andOperator = true;
		int begin = 0;
		int end = ParamUtil.getInteger(req, "delta", 5);
		String orderBy = ParamUtil.getString(req, "orderBy");

		OrderByComparator obc = new ArticleModifiedDateComparator(false);

		if (orderBy.equals("display-date")) {
			obc = new ArticleDisplayDateComparator(false);
		}

		if (structureIds.length > 1) { 
			List results = new ArrayList();
			
			for (int i = 0; i< structureIds.length; i++) {
				results.addAll(JournalArticleLocalServiceUtil.search(
						companyId, groupId, articleId, version, title, 
						description, content, type, structureIds[i].trim(), 
						templateId, displayDateGT, displayDateLT, approved, 
						expired, reviewDate, andOperator, begin, end, obc));
			}
			
			Collections.sort(results, obc);
			
			if (results.size() > end) {
				return results.subList(0, end);
			}
			
			return results;
		}
		else {
			return JournalArticleLocalServiceUtil.search(
					companyId, groupId, articleId, version, title, description, content,
					type, structureIds[0], templateId, displayDateGT, displayDateLT,
					approved, expired, reviewDate, andOperator, begin, end, obc);
		}
	}

	protected byte[] getContent(HttpServletRequest req, List articles)
		throws Exception {

		long groupId = ParamUtil.getLong(req, "groupId");

		ThemeDisplay themeDisplay =
			(ThemeDisplay)req.getAttribute(WebKeys.THEME_DISPLAY);

		Map tokens = JournalUtil.getTokens(groupId, themeDisplay);

		Document resultsDoc =
			DocumentFactory.getInstance().createDocument();

		Element resultSetEl = resultsDoc.addElement("result-set");

		SAXReader reader = new SAXReader();

		Iterator itr = articles.iterator();

		while (itr.hasNext()) {
			JournalArticle article = (JournalArticle)itr.next();

			Element resultEl = resultSetEl.addElement("result");

			Document articleDoc = reader.read(new StringReader(
				article.getContentByLocale(themeDisplay.getLanguageId())));

			resultEl.content().add(
				articleDoc.getRootElement().createCopy());

			if (article.isTemplateDriven()) {
				resultEl = resultEl.element("root");
			}

			JournalUtil.addAllReservedEls(resultEl, tokens, article);
		}

		return JournalUtil.formatXML(resultsDoc).getBytes();
	}

	private static Log _log = LogFactory.getLog(GetArticlesAction.class);

}