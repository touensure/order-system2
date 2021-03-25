package com.order.manager.config;


import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.schema.ModelRef;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;


/**
 * swagger2配置类
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.order.manager.web"))
                .paths(PathSelectors.any())// 选择所有的API,如果想只为部分API生成文档，可以配置这里
                .build()
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, newArrayList(
                        new ResponseMessageBuilder()
                                .code(200)
                                .message("Ok, implementation is successful")
//                                .responseModel(new ModelRef("successful"))//想用这个直接查一下就行了
                                .build(),
                        new ResponseMessageBuilder()
                                .code(201)
                                .message("Created, successfully created an item")
//                                .responseModel(new ModelRef("creation successful"))
                                .build(),
                        new ResponseMessageBuilder()
                                .code(401)
                                .message("Unauthorized, authorization failed")
//                                .responseModel(new ModelRef("authorization Error"))
                                .build(),
                        new ResponseMessageBuilder()
                                .code(403)
                                .message("Forbidden, resource is unavailable")
//                                .responseModel(new ModelRef("Forbidden"))
                                .build(),
                        new ResponseMessageBuilder()
                                .code(404)
                                .message("cannot found")
//                                .responseModel(new ModelRef(" Not Found Error"))
                                .build(),
                        new ResponseMessageBuilder()
                                .code(500)
                                .message("internal server error,A server-side exception occurred that prevented the system from correctly returning the result.")
//                                .responseModel(new ModelRef(" Server Error"))
                                .build()
                ))
                .globalResponseMessage(RequestMethod.POST, newArrayList(
                        new ResponseMessageBuilder()
                                .code(200)
                                .message("Ok, implementation is successful")
//                                .responseModel(new ModelRef("successful"))
                                .build(),
                        new ResponseMessageBuilder()
                                .code(201)
                                .message("Created, successfully created an item")
//                                .responseModel(new ModelRef("creation successful"))
                                .build(),
                        new ResponseMessageBuilder()
                                .code(401)
                                .message("Unauthorized, authorization failed")
//                                .responseModel(new ModelRef("authorization Error"))
                                .build(),
                        new ResponseMessageBuilder()
                                .code(403)
                                .message("Forbidden, resource is unavailable")
//                                .responseModel(new ModelRef("Forbidden"))
                                .build(),
                        new ResponseMessageBuilder()
                                .code(404)
                                .message("cannot found")
//                                .responseModel(new ModelRef(" Not Found Error"))
                                .build(),
                        new ResponseMessageBuilder()
                                .code(500)
                                .message("internal server error,A server-side exception occurred that prevented the system from correctly returning the result.")
                                .responseModel(new ModelRef(" Server Error"))
                                .build()
                ))
                .globalResponseMessage(RequestMethod.PUT, newArrayList(
                        new ResponseMessageBuilder()
                                .code(200)
                                .message("Ok, implementation is successful")
//                                .responseModel(new ModelRef("successful"))
                                .build(),
                        new ResponseMessageBuilder()
                                .code(201)
                                .message("Created, successfully created an item")
                                .responseModel(new ModelRef("creation successful"))
                                .build(),
                        new ResponseMessageBuilder()
                                .code(401)
                                .message("Unauthorized, authorization failed")
//                                .responseModel(new ModelRef("authorization Error"))
                                .build(),
                        new ResponseMessageBuilder()
                                .code(403)
                                .message("Forbidden, resource is unavailable")
//                                .responseModel(new ModelRef("Forbidden"))
                                .build(),
                        new ResponseMessageBuilder()
                                .code(404)
                                .message("cannot found")
//                                .responseModel(new ModelRef(" Not Found Error"))
                                .build(),
                        new ResponseMessageBuilder()
                                .code(500)
                                .message("internal server error,A server-side exception occurred that prevented the system from correctly returning the result.")
//                                .responseModel(new ModelRef(" Server Error"))
                                .build()
                ))
                .securityContexts(Lists.newArrayList(securityContexts()))
                .securitySchemes(securitySchemes())
                ;
    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Rest API Documentation Based on Swagger")
                .description("This is a API documentation about Order System for SAP Training")
                .contact(new Contact("Deng Yinxiang", "http://localhost:8081/swagger-ui.html#/", "1319654019@qq.com"))
                .termsOfServiceUrl("http://localhost:8081/swagger-ui.html#/")
                .version("1.0")
                .build();
    }
    private List<ApiKey> securitySchemes() {
        return newArrayList(
                new ApiKey("Authorization", "Authorization", "header"));
    }
    private List<SecurityContext> securityContexts() {
        return newArrayList(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .forPaths(PathSelectors.regex("^(?!login).*$"))
//                        .forPaths(PathSelectors.regex("/.*"))
                        .build()
        );
    }
    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return newArrayList(
                new SecurityReference("Authorization", authorizationScopes));
    }
}

