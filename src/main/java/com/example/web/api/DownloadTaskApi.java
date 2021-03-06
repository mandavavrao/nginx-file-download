package com.example.web.api;

import java.net.UnknownHostException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.HttpRequestTool;
import com.example.common.HttpResponseTool;
import com.example.domain.DownloadTask;
import com.example.domain.File;
import com.example.persist.must.DownloadTaskRMapper;
import com.example.persist.must.DownloadTaskWMapper;
import com.example.persist.must.FileRMapper;
import com.example.web.consts.RouteDefine;
import com.example.web.interceptor.LoginInterceptor;

@RestController
public class DownloadTaskApi {

	@Autowired
	private DownloadTaskRMapper rMapper;
	@Autowired
	private DownloadTaskWMapper wMapper;
	@Autowired
	private FileRMapper fileRMapper;

	@RequestMapping(value = RouteDefine.API + "/i/dld-tasks", method = RequestMethod.GET)
	public void getAll(HttpServletRequest request, HttpServletResponse response) {
		Long userId = LoginInterceptor.getAccountId(request);
		List<DownloadTask> tasks = rMapper.selectEnabledByUserId(userId);
		HttpResponseTool.writeResponse(response, tasks);
	}

	@RequestMapping(value = RouteDefine.API + "/i/dld-tasks", method = RequestMethod.POST)
	public void post(HttpServletRequest request, HttpServletResponse response)
			throws UnknownHostException {
		Long id = HttpRequestTool.extractId(request);
		if (id == null) {
			HttpResponseTool.setStatusAsNotFound(response);
			return;
		}
		File file = fileRMapper.selectEnabledById(id);
		if (file == null) {
			HttpResponseTool.setStatusAsNotFound(response);
			return;
		}
		DownloadTask task = new DownloadTask();
		task.reset();
		task.setClientIp(HttpRequestTool.getClientIpNullToEmpty(request));
		task.getFile().setId(id);
		task.getUser().setId(LoginInterceptor.getAccountId(request));
		wMapper.insert(task);
		HttpResponseTool.writeResponse(response, task);
	}

}
