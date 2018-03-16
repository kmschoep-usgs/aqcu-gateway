# aqcu-gateway
Aquarius Customization - Gateway

This service sits between the outside world and the back end services of AQCU. It is build upon the Spring Cloud/Netflix platform.

It is built as a war to be deployed into a Tomcat container.

Configured functionality includes:

- **Swagger Api Documentation** Located at https://localhost:8443/aqcu-gateway/swagger-iu.html
- **Hystrix Dashboard** Located at https://localhost:8443/aqcu-gateway/hystrix/monitor?stream=https%3A%2F%2Flocalhost%3A8443%2Faqcu-gateway%2Fhystrix.stream%20
- **CIDA Auth** token forwarding
- **Leagcy Endpoint Mapping** Each major endpoint has it's own mapping and timeout configuration

## Migrating to new Services
The filter CustomZuulFilter is designed so that the application can transition from delivering data via aqcu-webservice to using the service per report model. It will intercept proxies to serviceId's configured with the new pattern and route them correctly. The built in Zuul behavior will not change the URI of a request (beyond stripping the prefix). This filter will change the URI to be the same as the serviceId. Only serviceIds mathing the regex "^aqcu-.*/.*$" will be intercepted. For example, serviceId "timeseriessummaryReport" will route via default Zuul logic, while serviceId "aqcu-tss-report/timeseriessummary" will route via this custom logic.

## Running the Application

The war built using maven can be deployed the same as any other to a Tomcat instance. Some additional configurations are needed before starting Tomcat:

- At this time, it only works when deployed with all the other aqcu modules. To wire it into them, modify the context.xml's "aqcu.reports.webservice" value to "https://localhost:8443/aqcu-gateway/aqcu-webservice"
- Add ```<Parameter name="spring.config.location" value="${catalina.base}/conf/application.yml" />``` to the context.xml file
- Copy the base directory's application.yml to the conf folder (may need to append contents into existing file) and adjust any values as required.
- Note that multiple apps in the same tomcat instance will share the same application.yml configuration.

Once Tomcat is started, AQCU should function similar to before. Swagger and the Hystrix Dashboard should give you interesting info on the functioning of the gateway.
