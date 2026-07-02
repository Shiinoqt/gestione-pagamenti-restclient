package com.its.gestionepagamentirestclient.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class MdcHttpFilter extends OncePerRequestFilter {

    private static final String CALLER_HEADER = "caller";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String caller = request.getHeader(CALLER_HEADER);
            MDC.put("caller", (caller == null || caller.isBlank()) ? "http" : caller);
            MDC.put("method", "HTTP");
            MDC.put("uri", request.getRequestURI());
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}