package boselecta

import grails.plugin.boselecta.BoSelectaEndpoint
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean
import org.springframework.context.annotation.Bean

class DefaultBoConfig {
/*
	@Bean
	public ServletContextInitializer myInitializer() {
		return new ServletContextInitializer() {
			@Override
			public void onStartup(ServletContext servletContext) throws ServletException {
				servletContext.addListener(BoSelectaEndpoint)

			}
		}
	}
*/
	@Bean
	public ServletListenerRegistrationBean<BoSelectaEndpoint> httpSessionEventPublisher() {
		return new ServletListenerRegistrationBean<BoSelectaEndpoint>(new BoSelectaEndpoint())

	}


}
