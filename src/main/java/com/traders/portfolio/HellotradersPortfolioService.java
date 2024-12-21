package com.traders.portfolio;

import com.traders.common.appconfig.util.DefaultProfileUtil;
import com.traders.common.constants.ProfileConstants;
import com.traders.portfolio.config.CRLFLogConverter;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@EnableJpaRepositories
@EnableWebSecurity
@EnableDiscoveryClient
@SpringBootApplication(exclude = LiquibaseAutoConfiguration.class)
@EnableFeignClients
public class HellotradersPortfolioService {


	private static final Logger LOG = LoggerFactory.getLogger(HellotradersPortfolioService.class);

	private final Environment env;

	public HellotradersPortfolioService(Environment env) {
		this.env = env;
	}

	/**
	 * Initializes portfolioService.
	 * <p>
	 * Spring profiles can be configured with a program argument --spring.profiles.active=your-active-profile
	 * <p>
	 */
	@PostConstruct
	public void initApplication() {
		Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
		if (
				activeProfiles.contains(ProfileConstants.SPRING_PROFILE_DEVELOPMENT) &&
						activeProfiles.contains(ProfileConstants.SPRING_PROFILE_PRODUCTION)
		) {
			LOG.error(
					"You have misconfigured your application! It should not run " + "with both the 'dev' and 'prod' profiles at the same time."
			);
		}
		if (
				activeProfiles.contains(ProfileConstants.SPRING_PROFILE_DEVELOPMENT) &&
						activeProfiles.contains(ProfileConstants.SPRING_PROFILE_CLOUD)
		) {
			LOG.error(
					"You have misconfigured your application! It should not " + "run with both the 'dev' and 'cloud' profiles at the same time."
			);
		}
	}

	/**
	 * Main method, used to run the application.
	 *
	 * @param args the command line arguments.
	 */
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(HellotradersPortfolioService.class);
		DefaultProfileUtil.addDefaultProfile(app);
		Environment env = app.run(args).getEnvironment();
		logApplicationStartup(env);
	}

	private static void logApplicationStartup(Environment env) {
		String protocol = Optional.ofNullable(env.getProperty("server.ssl.key-store")).map(key -> "https").orElse("http");
		String applicationName = env.getProperty("spring.application.name");
		String serverPort = env.getProperty("server.port");
		String contextPath = Optional.ofNullable(env.getProperty("server.servlet.context-path"))
				.filter(StringUtils::isNotBlank)
				.orElse("/");
		String hostAddress = "localhost";
		try {
			hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			LOG.warn("The host name could not be determined, using `localhost` as fallback");
		}
		LOG.info(
				CRLFLogConverter.CRLF_SAFE_MARKER,
				"""
    
                ----------------------------------------------------------
                \tApplication '{}' is running! Access URLs:
                \tLocal: \t\t{}://localhost:{}{}
                \tExternal: \t{}://{}:{}{}
                \tProfile(s): \t{}
                ----------------------------------------------------------""",
				applicationName,
				protocol,
				serverPort,
				contextPath,
				protocol,
				hostAddress,
				serverPort,
				contextPath,
				env.getActiveProfiles().length == 0 ? env.getDefaultProfiles() : env.getActiveProfiles()
		);

		String configServerStatus = env.getProperty("configserver.status");
		if (configServerStatus == null) {
			configServerStatus = "Not found or not setup for this application";
		}
		LOG.info(
				CRLFLogConverter.CRLF_SAFE_MARKER,
				"\n----------------------------------------------------------\n\t" +
						"Config Server: \t{}\n----------------------------------------------------------",
				configServerStatus
		);
	}
}

