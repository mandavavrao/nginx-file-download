package com.example.web.gui.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.example.common.HttpRequestTool;
import com.example.common.ModelAndViewTool;
import com.example.common.ReflectTool;
import com.example.config.AppConfig;
import com.example.domain.FileServiceGroup;
import com.example.persist.must.FileServiceGroupRMapper;
import com.example.persist.must.FileServiceGroupWMapper;
import com.example.web.consts.RouteDefine;
import com.example.web.gui.WebGuiDefine;
import com.google.common.base.Strings;

@Controller
public class AdminFileServiceGroupController {

	static final String VIEW_NAME_PREFIX = WebGuiDefine.ADMIN + "/file-service-group/";
	static final String VIEW_NAME_DISABLE = VIEW_NAME_PREFIX + WebGuiDefine.DISABLE;
	static final String VIEW_NAME_EDIT = VIEW_NAME_PREFIX + WebGuiDefine.EDIT;
	static final String VIEW_NAME_ENABLE = VIEW_NAME_PREFIX + WebGuiDefine.ENABLE;
	static final String VIEW_NAME_LIST = VIEW_NAME_PREFIX + WebGuiDefine.LIST;
	static final String VIEW_NAME_NEW = VIEW_NAME_PREFIX + WebGuiDefine.NEW;

	static final String BASE_ROUTE = RouteDefine.ADMIN + "/fsgroups";

	private static final Logger logger = LoggerFactory.getLogger(AdminFileServiceGroupController.class);

	@Autowired
	private AppConfig appConfig;
	@Autowired
	private FileServiceGroupRMapper rMapper;
	@Autowired
	private FileServiceGroupWMapper wMapper;

	@RequestMapping(value = BASE_ROUTE, method = RequestMethod.GET)
	public ModelAndView list(HttpServletRequest request) {
		return ModelAndViewTool.newModelAndView(request, appConfig, VIEW_NAME_LIST);
	}

	@RequestMapping(value = RouteDefine.ADMIN + "/fsgroups/new", method = RequestMethod.GET)
	public ModelAndView gotoNew(HttpServletRequest request) {
		return ModelAndViewTool.newModelAndView(request, appConfig, VIEW_NAME_NEW);
	}

	@RequestMapping(value = BASE_ROUTE, method = RequestMethod.POST)
	public ModelAndView newOne(HttpServletRequest request) {
		String name = HttpRequestTool.extractName(request);
		logger.debug("name: " + name);
		FileServiceGroup e = new FileServiceGroup();
		e.reset();
		e.setName(name);
		wMapper.insert(e);
		return ModelAndViewTool.newModelAndViewAndRedirect(request, appConfig, BASE_ROUTE);
	}

	@RequestMapping(value = RouteDefine.ADMIN + "/fsgroups/edit", method = RequestMethod.GET)
	public ModelAndView gotoEdit(HttpServletRequest request, HttpServletResponse response) {
		Long id = HttpRequestTool.extractId(request);
		if (id == null) {
			return ModelAndViewTool.newModelAndViewFor404(request, response, appConfig);
		}
		FileServiceGroup e = rMapper.selectById(id);
		if (e == null) {
			return ModelAndViewTool.newModelAndViewFor404(request, response, appConfig);
		}
		ModelAndView ret = ModelAndViewTool.newModelAndView(request, appConfig, VIEW_NAME_EDIT);
		ret.getModel().putAll(ReflectTool.toMap(e));
		return ret;
	}

	@RequestMapping(value = RouteDefine.ADMIN + "/fsgroups/edit", method = RequestMethod.POST)
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) {
		Long id = HttpRequestTool.extractId(request);
		if (id == null) {
			return ModelAndViewTool.newModelAndViewFor404(request, response, appConfig);
		}
		FileServiceGroup e = rMapper.selectById(id);
		if (e == null) {
			return ModelAndViewTool.newModelAndViewFor404(request, response, appConfig);
		}
		String name = request.getParameter(HttpRequestTool.NAME);
		if (!Strings.isNullOrEmpty(name)) {
			e.setName(name);
		}
		Boolean enabled = HttpRequestTool.extractEnabled(request);
		if (enabled != null) {
			e.setEnabled(enabled);
		}
		e.resetUpdatedAt();
		wMapper.update(e);
		return ModelAndViewTool.newModelAndViewAndRedirect(request, appConfig, BASE_ROUTE);
	}

}
