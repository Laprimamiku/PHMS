/*
package filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter("/*")  // 过滤所有请求，可以根据需要调整
public class LoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化过滤器，通常不需要做什么
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 获取当前用户的登录状态（假设已登录的用户信息保存在 session 中）
        Object user = httpRequest.getSession().getAttribute("username");

        // 检查用户是否已经登录
        String requestURI = httpRequest.getRequestURI();

        // 允许访问登录页和注册页
        if (user != null || requestURI.endsWith("log.jsp") || requestURI.endsWith("reg.jsp")) {
            // 如果已登录或者请求的是登录/注册页面，继续处理请求
            chain.doFilter(request, response);
        } else {
            // 如果未登录且访问的不是登录/注册页面，则重定向到登录页面
            httpResponse.sendRedirect("log.jsp");
        }
    }

    @Override
    public void destroy() {
        // 清理资源
    }
}
*/