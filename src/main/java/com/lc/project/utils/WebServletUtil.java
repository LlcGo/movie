package com.lc.project.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: LJH
 * @Date: 2020/11/9 18:40
 */
public class WebServletUtil {

    private static final String[] HEADERS_TO_TRY = {"X-Forwarded-For","Proxy-Client-IP", "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR", "X-Real-IP"};

    /***
     * 获取客户端ip地址(可以穿透代理)
     *
     * @param request HttpServletRequest
     * @return 客户端ip地址
     */
    public static String getClientIpAddress(HttpServletRequest request) {
//        System.out.println("request.getRequestURI() = " + request.getRequestURI());
//        System.out.println("request.getRemoteHost() = " + request.getRemoteHost());
//        System.out.println("request.getRequestURL() = " + request.getRequestURL());
//        System.out.println("request.getPathInfo() = " + request.getPathInfo());
//        System.out.println("request.getRemoteAddr() = " + request.getRemoteAddr());
//        System.out.println("request.getQueryString() = " + request.getQueryString());
//        System.out.println("request.getRequestedSessionId() = " + request.getRequestedSessionId());
//        System.out.println("request.getServletPath() = " + request.getServletPath());
//
//        System.out.println("Accept: " + request.getHeader("Accept"));
//        System.out.println("Host: " + request.getHeader("Host"));
//        System.out.println("Referer : " + request.getHeader("Referer"));
//        System.out.println("Accept-Language : " + request.getHeader("Accept-Language"));
//        System.out.println("Accept-Encoding : " + request.getHeader("Accept-Encoding"));
//        System.out.println("User-Agent : " + request.getHeader("User-Agent"));
//        System.out.println("Connection : " + request.getHeader("Connection"));
//        System.out.println("Cookie : " + request.getHeader("Cookie"));
//        System.out.println("Created : " + request.getSession().getCreationTime());
//        System.out.println("LastAccessed : " + request.getSession().getLastAccessedTime());


        String queryString = request.getQueryString();
        if (queryString==null||queryString.equals("null")||queryString.equals(null)||queryString.length()<6){
            for (String header : HEADERS_TO_TRY) {
                String ip = request.getHeader(header);
                if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                    return ip;
                }
            }
            return request.getRemoteAddr();
        }else{
            queryString = queryString.split("%20=%20")[1];
//            System.out.println("queryString = " + queryString);

            for (String header : HEADERS_TO_TRY) {
                String ip = request.getHeader(header);
                if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                    return ip+":"+queryString;
                }
            }
            return request.getRemoteAddr()+":"+queryString;
        }

//        String remoteAddr = request.getRemoteAddr();
//        return remoteAddr+":"+queryString;
    }

    /**
     * 获取reqest中的信息
     *
     * @param request
     */
    public static void getRequestInfo(HttpServletRequest request) {
//		StringTokenizer st = new StringTokenizer("agent", ";");
//		st.nextToken();
////得到用户的浏览器名
//		String userbrowser = st.nextToken();
//		System.out.println(userbrowser);
////得到用户的操作系统名
//		String useros = st.nextToken();
//		System.out.println(useros);
        // 取得本机的信息也可以这样：
        // 操作系统信息
        System.out.println(System.getProperty("os.name"));
        System.out.println(System.getProperty("os.version"));
        System.out.println(System.getProperty("os.arch"));

        System.out.println(request.getHeader("user-agent")); // 返回客户端浏览器的版本号、类型
        System.out.println(request.getMethod()); // ：获得客户端向服务器端传送数据的方法有get、post、put等类型
        System.out.println(request.getRequestURI()); // ：获得发出请求字符串的客户端地址
        System.out.println(request.getServletPath()); // ：获得客户端所请求的脚本文件的文件路径
        System.out.println(request.getServerName()); // ：获得服务器的名字
        System.out.println(request.getServerPort()); // ：获得服务器的端口号
        System.out.println(request.getRemoteAddr()); // ：获得客户端的ip地址
        System.out.println(request.getRemoteHost()); // ：获得客户端电脑的名字，若失败，则返回客户端电脑的ip地址
        System.out.println(request.getProtocol()); // ：
        System.out.println(request.getHeaderNames()); // ：返回所有request header的名字，结果集是一个enumeration（枚举）类的实例

        System.out.println("Session Id: " + request.getRequestedSessionId());
        System.out.println("Content Length: " + request.getContentLength());
        System.out.println("Remote User: " + request.getRemoteUser());
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Host: " + request.getHeader("Host"));
        System.out.println("Connection : " + request.getHeader("Connection"));
        System.out.println("Cookie : " + request.getHeader("Cookie"));
        System.out.println("Created : " + request.getSession().getCreationTime());

        // 获取当前请求的路径
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                + request.getContextPath();
        System.out.println("basePath:" + basePath);

        // 获取来访者地址。只有通过链接访问当前页的时候，才能获取上一页的地址；
        String refererUrl = request.getHeader("Referer");
        // 获取全路径
        StringBuffer requestUrl = request.getRequestURL();
        // 除去host（域名或者ip）部分的路径
        String requestUri = request.getRequestURI();
        // 返回工程名部分，如果工程映射为/，此处返回则为空
        String contextPath = request.getContextPath();
        // 除去host和工程名部分的路径
        String servletPath = request.getServletPath();

        // http://127.0.0.1:8080/manage/course_info
        System.out.println("request.getHeader(\"Referer\")：" + refererUrl);
        // http://127.0.0.1:8080/manage/courseInfo/selectPageInfo.sose
        System.out.println("request.getRequestURL()：" + requestUrl);
        // /manage/courseInfo/selectPageInfo.sose
        System.out.println("request.getRequestURI()：" + requestUri);
        // 空
        System.out.println("request.getContextPath()：" + contextPath);
        // /manage/courseInfo/selectPageInfo.sose
        System.out.println("request.getServletPath()：" + servletPath);
    }

}
