package com.hctym.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servlet相关对象处理工具
 * 
 * @author Warren
 * @since Jan 22, 2016
 */
@SuppressWarnings("unchecked")
public abstract class WebUtils {

	private static final Logger logger = LoggerFactory.getLogger(WebUtils.class);

	private static final UrlPathHelper urlPathHelper = new UrlPathHelper();

	//正则过滤防伪造注入
    private static final Pattern IP_PATTERN = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");

    /**
     * 获取请求路径
     * @param request 请求对象
     * @return 请求路径
     */
    public static String getRequestUri(HttpServletRequest request) {
    	return urlPathHelper.getLookupPathForRequest(request);
    }

    /**
     * 获取请求参数
     * @param request 请求对象
     * @return 请求参数
     */
    public static String getParams(HttpServletRequest request) {
    	StringBuilder build = new StringBuilder();
    	Map<String, String[]> param = getParameters(request);
    	for(Map.Entry<String, String[]> entry : param.entrySet()) {
    		for(String value : entry.getValue()) {
    			build.append(entry.getKey()).append("=").append(value).append("&");
    		}
    	}
    	if(build.length() > 0) {
    		build.deleteCharAt(build.length()-1);
    	}
    	return build.toString();
    }

	/**
	 * 获取所有请求参数
	 * @param request 请求对象
	 * @return key=>value
	 */
	public static Map<String, String[]> getParameters(HttpServletRequest request) {
		return getParametersStartingWith(request, "");
	}

	/**
	 * 获取所有请求参数
	 * @param request 请求对象
	 * @param prefix 参数数前缀
	 * @return key=>value
	 */
	public static Map<String, String[]> getParametersStartingWith(HttpServletRequest request, String prefix) {
		Enumeration<String> paramNames = request.getParameterNames();
		Map<String, String[]> params = new TreeMap<String, String[]>();
		if (prefix == null) {
			prefix = "";
		}
		while (paramNames != null && paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			if ("".equals(prefix) || paramName.startsWith(prefix)) {
				String unprefixed = paramName.substring(prefix.length());
				String[] values = request.getParameterValues(paramName);
				if(values != null && values.length > 0) {
					params.put(unprefixed, values);
				}
			}
		}
		return params;
	}

	/**
	 * 获取所有请求头参数
	 * @param request 请求对象
	 * @return key=>value
	 */
	public static Map<String, List<String>> getHeaders(HttpServletRequest request) {
		Map<String, List<String>> headers = new HashMap<String, List<String>>();
		for (Enumeration<?> headerNames = request.getHeaderNames(); headerNames.hasMoreElements();) {
			String headerName = (String) headerNames.nextElement();
			headerName = headerName.toLowerCase();
			for (Enumeration<?> headerEnumers = request.getHeaders(headerName); headerEnumers.hasMoreElements();) {
				String headerValue = (String) headerEnumers.nextElement();
				List<String> headerValues = headers.get(headerName);
				if (headerValues == null) {
					headerValues = new LinkedList<String>();
					headers.put(headerName, headerValues);
				}
				headerValues.add(headerValue);
			}
		}
		return headers;
	}

	/**
	 * 设置页面不缓存
	 * @param response 响应对象
	 */
	public static void setNoCache(HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Expires", "0");
        response.setHeader("Pragma", "No-cache");
	}

	/**
	 * 响应JSON串
	 * @param response 响应对象
	 * @param json 字符串
	 */
	public static void writeJson(HttpServletResponse response, String json) {
		if(json == null)
			return;
		String charset = "UTF-8";
		try {
			//setNoCache(response);
			response.setContentType("application/json;charset="+charset);
			response.setContentLength(json.getBytes(charset).length);
	        PrintWriter out = response.getWriter();
	        out.print(json);
	        out.flush();
		} catch (Exception e) {
			logger.error("Response write has exception {} json={}", e.getMessage(), json, e);
		}
	}

	/**
	 * 响应JSON串
	 * @param response 响应对象
	 * @param html 字符串
	 */
	public static void writeHtml(HttpServletResponse response, String html) {
		if(html == null)
			return;
		String charset = "UTF-8";
		try {
			//setNoCache(response);
			response.setContentType("text/html;charset="+charset);
			response.setContentLength(html.getBytes(charset).length);
	        PrintWriter out = response.getWriter();
	        out.print(html);
	        out.flush();
		} catch (Exception e) {
			logger.error("Response write has exception {} html={}", e.getMessage(), html, e);
		}
	}

	/**
	 * 获取请求参数，设置默认值
	 * @param request 请求对象
	 * @param name 参数名
	 * @param defValue 默认值
	 * @return 参数值
	 */
	public static String getParameter(HttpServletRequest request, String name, String defValue) {
		String param = request.getParameter(name);
		if(param == null) {
			param = defValue;
		}
		return param;
	}

	/**
	 * 获取请求IP
	 * @param request 请求对象
	 * @return IP地址
	 */
    public static String getRemoteAddr(HttpServletRequest request) {
        //从请求头部取IP记录
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        //如果是多级代理，那么取第一个ip
        if(StringUtils.isNotBlank(ip)) {
	        Matcher matcher = IP_PATTERN.matcher(ip);
	        while (matcher.find()) {
	            ip = matcher.group();
	        }
        }
        if(StringUtils.isBlank(ip)) {
        	ip = "0.0.0.0";
        }
        return ip;
    }

	/**
	 * 获取当前请求traceId
	 * @param request 请求对象
	 * @return traceId
	 */
	public static String getRequestId(HttpServletRequest request) {
		//新版本2015-11-02以后，客户端在Header中传M-TraceId，如果requestId和M-TraceId都有，则M-TraceId优先
        String requestId = request.getHeader("M-TraceId");
        if(StringUtils.isBlank(requestId)){
        	//老版本2015-11-02以前，客户端传requestId
        	requestId = request.getParameter("requestId");
        }
        return requestId;
	}
	
}
