package com.devsuperior.bds04.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Autowired
	private JwtTokenStore tokenStore;
	
	@Autowired
	private Environment env;		//ambiente de execução da aplicação
	
	private static final String[] PUBLIC = {"/oauth/token", "/h2-console/**"};  //login e H2
	
	private static final String[] OPERATOR_GET = { "/cities/**" , "/events/**" };  //GET para /cities e /events
	
	private static final String[] OPERATOR_POST_EVENT = { "/events/**" };  //POST para /events
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		// Configurando nosso token
		resources.tokenStore(tokenStore);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		// Configurando as rotas.
		// Consultas : liberado para todos
	
		// se o profile ativo for "test", liberamos a rota da base de dados H2 
		if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
			http.headers().frameOptions().disable();	
		}
		
		// Funcionalidades :
//		1) Os endpoints de login e do H2 devem ser públicos
//		2) Os endpoints GET para /cities e /events devem ser públicos
//		3) O endpoint POST de /events devem requerer login de ADMIN ou CLIENT
//		4) Todos demais endpoints devem requerer login de ADMIN

		http.authorizeRequests()
			.antMatchers(PUBLIC).permitAll()
			.antMatchers(HttpMethod.GET, OPERATOR_GET).permitAll()
			.antMatchers(HttpMethod.POST, OPERATOR_POST_EVENT).hasAnyRole("CLIENT", "ADMIN")
			.anyRequest().hasAnyRole("ADMIN");			//qualquer outra rota, deve ser ADMIN
	}


}
