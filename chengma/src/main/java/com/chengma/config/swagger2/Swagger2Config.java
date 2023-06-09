package com.chengma.config.swagger2;

import io.swagger.annotations.ApiOperation;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.*;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author psh 2023/4/18 18:23
 */
@EnableSwagger2
@Configuration
public class Swagger2Config {

	@Bean
	public Docket docket() {

		return new Docket(DocumentationType.SWAGGER_2).enable(true).apiInfo(apiInfo())
				.select()
				.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
				.paths(PathSelectors.any()).build();

	}

	private ApiInfo apiInfo() {
		return new ApiInfo("ChengMa Test", "This is Sample", "1.0", "",
				new Contact("chengma.com", "", ""), "anycase", "http://www.chengma.com",
				new ArrayList<>());
	}

	/**
	 * 增加如下配置可解决Spring Boot 6.x 与Swagger 3.0.0 不兼容问题 开启 @EnableWebMvc
	 **/
	@Bean
	public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(
			WebEndpointsSupplier webEndpointsSupplier,
			ServletEndpointsSupplier servletEndpointsSupplier,
			ControllerEndpointsSupplier controllerEndpointsSupplier,
			EndpointMediaTypes endpointMediaTypes, CorsEndpointProperties corsProperties,
			WebEndpointProperties webEndpointProperties, Environment environment) {

		// 所有环境参数
		List<ExposableEndpoint<?>> allEndpoints = new ArrayList();
		allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
		allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());

		// web环境参数
		Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier
				.getEndpoints();
		allEndpoints.addAll(webEndpoints);

		String basePath = webEndpointProperties.getBasePath();
		EndpointMapping endpointMapping = new EndpointMapping(basePath);
		boolean shouldRegisterLinksMapping = this
				.shouldRegisterLinksMapping(webEndpointProperties, environment, basePath);

		return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints,
				endpointMediaTypes, corsProperties.toCorsConfiguration(),
				new EndpointLinksResolver(allEndpoints, basePath),
				shouldRegisterLinksMapping, null);
	}

	private boolean shouldRegisterLinksMapping(
			WebEndpointProperties webEndpointProperties, Environment environment,
			String basePath) {

		return webEndpointProperties.getDiscovery().isEnabled()
				&& (StringUtils.hasText(basePath) || ManagementPortType.get(environment)
						.equals(ManagementPortType.DIFFERENT));
	}

}
