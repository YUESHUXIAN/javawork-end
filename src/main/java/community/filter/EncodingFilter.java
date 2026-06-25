package community.filter;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 全局字符编码过滤器
 * 拦截所有请求，统一设置 UTF-8 编码，解决中文乱码问题
 */
@WebFilter("/*") // "/*" 表示拦截项目下的所有请求
public class EncodingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 过滤器初始化时调用，这里不需要额外操作
        System.out.println("编码过滤器 EncodingFilter 已启动...");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 1. 强转 ServletRequest 和 ServletResponse 为 Http 类型 (为了后续方便设置 Header)
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 2. 设置请求和响应的字符编码为 UTF-8
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        // 3. 不在这里设置 Content-Type，由各个 Servlet 自行设置
        //    AJAX 接口需要 application/json，HTML 页面需要 text/html

        // 4. 放行请求，让请求继续往下走（到达具体的 Servlet）
        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {
        // 过滤器销毁时调用，这里不需要额外操作
    }
}