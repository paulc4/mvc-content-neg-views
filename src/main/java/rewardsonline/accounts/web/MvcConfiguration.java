package rewardsonline.accounts.web;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Controller;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.XmlViewResolver;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesViewResolver;

import rewardsonline.accounts.Account;
import rewardsonline.accounts.Customer;
import rewardsonline.accounts.Transaction;
import rewardsonline.accounts.view.JsonViewResolver;
import rewardsonline.accounts.view.MarshallingXmlViewResolver;

/**
 * Java configuration for Spring MVC.
 * <p>
 * Only used when the "separate" profile is enabled. All view resolvers are
 * explicitly created as top-level beans and the CNVR scans the Spring context
 * to find them.
 * 
 * @See {@link #contentNegotiatingViewResolver(ContentNegotiationManager, ServletContext)}
 */
@Configuration
@EnableWebMvc
@Profile("separate")
@ComponentScan(basePackages = "rewardsonline", useDefaultFilters = false, includeFilters = @Filter(Controller.class))
public class MvcConfiguration extends WebMvcConfigurerAdapter {

	protected Logger logger = Logger.getLogger(MvcConfiguration.class);

	@Autowired
	protected ServletContext servletContext;

	public MvcConfiguration() {
		logger.warn("Profile 'separate' - view resolvers defined as separate top-level beans.");
	}

	@Override
	public void configureDefaultServletHandling(
			DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("welcome");
		registry.addViewController("/home").setViewName("welcome");
		registry.addViewController("/denied").setViewName("denied");
	}


	@Bean(name = "tilesConfigurer")
	public TilesConfigurer getTilesConfigurer() {
		TilesConfigurer configurer = new TilesConfigurer();
		String[] tilesDefFiles = { "/WEB-INF/tiles.xml",
				"/WEB-INF/accounts/tiles.xml" };
		configurer.setDefinitions(tilesDefFiles);
		return configurer;
	}

	@Bean(name = "messageSource")
	// Mandatory name
	public MessageSource getMessageSource() {
		ReloadableResourceBundleMessageSource msgSrc = new ReloadableResourceBundleMessageSource();
		msgSrc.setBasename("/WEB-INF/messages/global");
		return msgSrc;
	}
	
	// View Resolvers - all separate top-level bens
	
	@Bean(name = "tilesViewResolver")
	public ViewResolver getTilesViewResolver() {
		TilesViewResolver resolver = new TilesViewResolver();
		resolver.setContentType("text/html");
		return resolver;
	}

	@Bean(name = "excelViewResolver")
	public ViewResolver getXmlViewResolver() {
		XmlViewResolver resolver = new XmlViewResolver();
		resolver.setLocation(new ServletContextResource(servletContext,
				"/WEB-INF/spring/spreadsheet-views.xml"));
		return resolver;
	}

	@Bean(name = "jsonViewResolver")
	public ViewResolver getJsonViewResolver() {
		return new JsonViewResolver();
	}

	@Bean(name = "marshallingXmlViewResolver")
	public ViewResolver getMarshallingXmlViewResolver() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(Account.class, Transaction.class,
				Customer.class);
		return new MarshallingXmlViewResolver(marshaller);
	}

	@Override
	public void configureContentNegotiation(
			ContentNegotiationConfigurer configurer) {
		// Simple strategy: only path extension is taken into account
		configurer.favorPathExtension(true).ignoreAcceptHeader(true)
				.useJaf(false).defaultContentType(MediaType.TEXT_HTML)
				.mediaType("html", MediaType.TEXT_HTML)
				.mediaType("xml", MediaType.APPLICATION_XML)
				.mediaType("json", MediaType.APPLICATION_JSON);
	}

	/**
	 * Create the CNVR. Simplest setup - just pass in the
	 * {@link ContentNegotiationManager}.
	 * 
	 * @param manager
	 *            The content negotiation manager to use.
	 * @return A CNVR instance.
	 */
	@Bean
	public ViewResolver contentNegotiatingViewResolver(
			ContentNegotiationManager manager) {
		ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
		resolver.setContentNegotiationManager(manager);
		return resolver;
	}
}
