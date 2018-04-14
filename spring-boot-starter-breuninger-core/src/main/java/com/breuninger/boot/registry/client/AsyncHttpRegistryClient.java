package com.breuninger.boot.registry.client;

import static java.util.Arrays.stream;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.MINUTES;

import static org.springframework.util.StringUtils.isEmpty;

import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PostConstruct;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.breuninger.boot.annotations.Beta;
import com.breuninger.boot.configuration.BreuningerApplicationProperties;

import com.breuninger.boot.registry.configuration.ServiceRegistryProperties;
import com.breuninger.boot.status.domain.ApplicationInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnClass(AsyncHttpClient.class)
@ConditionalOnBean(AsyncHttpClient.class)
@EnableConfigurationProperties(ServiceRegistryProperties.class)
@Beta
public class AsyncHttpRegistryClient implements RegistryClient {

  private final ApplicationInfo applicationInfo;
  private final AsyncHttpClient httpClient;
  private final ServiceRegistryProperties serviceRegistryProperties;
  private final BreuningerApplicationProperties breuningerApplicationProperties;
  private final ScheduledExecutorService scheduledExecutorService = newSingleThreadScheduledExecutor();
  private boolean isRunning;

  public AsyncHttpRegistryClient(final ApplicationInfo applicationInfo, final AsyncHttpClient httpClient,
                                 final ServiceRegistryProperties serviceRegistryProperties,
                                 final BreuningerApplicationProperties breuningerApplicationProperties) {
    this.applicationInfo = applicationInfo;
    this.httpClient = httpClient;
    this.serviceRegistryProperties = serviceRegistryProperties;
    this.breuningerApplicationProperties = breuningerApplicationProperties;
  }

  @PostConstruct
  public void postConstruct() {
    if (serviceRegistryProperties.isEnabled()) {
      if (validateConfig()) {
        LOG.info("Scheduling registration at Breuninger JobTrigger every '{}' minutes.", serviceRegistryProperties.getRefreshAfter());
        scheduledExecutorService.scheduleWithFixedDelay(this::registerService, 0, serviceRegistryProperties.getRefreshAfter(),
          MINUTES);
        isRunning = true;
      } else {
        LOG.warn("===================================================================================");
        LOG.warn("ServiceRegistryProperties is enabled, but no service and/or servers are configured");
        LOG.warn(serviceRegistryProperties.toString());
        LOG.warn("===================================================================================");
      }
    } else {
      LOG.info("Scheduling registration at Breuninger JobTrigger disabled!");
    }
  }

  @Override
  public void registerService() {
    stream(serviceRegistryProperties.getServers().split(",")).filter(server -> !isEmpty(server)).forEach(discoveryServer -> {
      try {
        LOG.debug("Updating registration of service at '{}'", discoveryServer);
        httpClient.preparePut(
          discoveryServer + "/environments/" + breuningerApplicationProperties.getEnvironment() + "/" + applicationInfo.name)
          .setHeader("Content-Type", "application/vnd.breuninger.breuninger.links+json")
          .setHeader("Accept", "application/vnd.breuninger.breuninger.links+json")
          .setBody("{\n" + "   \"groups\":[\"" + breuningerApplicationProperties.getGroup() + "\"],\n" + "   \"expire\":" +
            serviceRegistryProperties.getExpireAfter() + ",\n" + "   \"links\":[{\n" +
            "      \"rel\":\"http://github.com/e-breuninger/spring-boot-starter-breuninger/link-relations/microservice\",\n" + "      \"href\" : \"" +
            serviceRegistryProperties.getService() + "\",\n" + "      \"title\":\"" + applicationInfo.title + "\"\n" +
            "   }]  \n" + "}")
          .execute(new AsyncCompletionHandler<Integer>() {
            @Override
            public void onThrowable(final Throwable t) {
              LOG.error("Failed to register at '{}'", discoveryServer, t);
            }

            @Override
            public Integer onCompleted(final Response response) {
              if (response.getStatusCode() < 300) {
                LOG.info("Successfully updated registration at " + discoveryServer);
              } else {
                LOG.warn("Failed to update registration at '{}': Status='{}' '{}'", discoveryServer, response.getStatusCode(),
                  response.getStatusText());
              }
              return response.getStatusCode();
            }
          });
      } catch (final Exception e) {
        LOG.error("Error updating registration", e);
      }
    });
  }

  @Override
  public boolean isRunning() {
    return isRunning;
  }

  private boolean validateConfig() {
    if (!serviceRegistryProperties.isEnabled()) {
      return true;
    }

    if (isEmpty(serviceRegistryProperties.getServers())) {
      return false;
    }

    return !isEmpty(serviceRegistryProperties.getService());
  }
}
