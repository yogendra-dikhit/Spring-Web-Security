package com.securityproject.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.filter.OncePerRequestFilter;

import com.securityproject.security.MyUserDetailsService;
import com.securityproject.util.JwtUtil;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtutil;
	
	@Autowired
	private MyUserDetailsService userDetailService;
	
	@Override
	@CrossOrigin(origins = {"*"}, allowedHeaders = "GET, POST, PUT, PATCH, POST, DELETE, OPTIONS" ,exposedHeaders = "GET, POST, PUT, PATCH, POST, DELETE, OPTIONS" )
	
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		final String authHeader = request.getHeader("Authorization");
		
		String jwt = null;
		String userName = null;
		
		if( authHeader != null && authHeader.startsWith("Bearer ")){
			jwt = authHeader.substring(7);
			userName = jwtutil.extractUserName(jwt);
		}
		
		if( userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			UserDetails userDetails = userDetailService.loadUserByUsername(userName);
			
			UsernamePasswordAuthenticationToken uAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
			uAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(uAuthenticationToken);
		}
		filterChain.doFilter(request, response);
		
	}

}
