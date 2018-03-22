package me.anqi.jexam.conf;

import me.anqi.jexam.interceptor.UserSecurityInterceptor;
import me.anqi.jexam.utils.FileType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.PostConstruct;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
	//G:\cHome\work\judger-kernel\build\bin32


	//public static final  String OJ_PATH="G:/home/OJ/";
	//public static final String OJ_INI_PATH="G:/home/OJ/conf/config.ini";

	public static final  String OJ_PATH="G:/cHome/work/judger-kernel/build/bin32/";
	public static final String OJ_INI_PATH="G:/cHome/work/judger-kernel/build/bin32/conf/config.ini";

	private static  final String  basicHome="G:/home/jcala/jmooc";
	private static  final String basicUrl="http://127.0.0.1:8090";


	private UserSecurityInterceptor securityInterceptor;

	@Autowired
	public WebMvcConfig(UserSecurityInterceptor securityInterceptor) {
		this.securityInterceptor = securityInterceptor;
	}

    /*@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer() {

		return (ConfigurableEmbeddedServletContainer container) ->{
				ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/html/401.html");
				ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/html/404.html");
				ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/html/500.html");

				container.addErrorPages(error401Page, error404Page,error500Page);
		};
	}*/

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(securityInterceptor).addPathPatterns("/user/**");//配置登录拦截器拦截路径
	}

	@PostConstruct
	public void initConfigureBean(){
		FileType.FILE.setHome(basicHome+ "/files/");
		FileType.FILE.setUrl(basicUrl+"/files/");
		FileType.VIDEO.setHome(basicHome+ "/videos/");
		FileType.VIDEO.setUrl(basicUrl+"/videos/");
	}

}
