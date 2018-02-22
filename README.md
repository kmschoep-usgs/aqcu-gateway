# aqcu-gateway
Aquarius Customization - Gateway

This service sits between the outside world and the back end services of AQCU. It is build upon the Spring Cloud/Netflix platform.

It is built as a war to be deployed into a Tomcat container.

Configured functionality includes:

- **Swagger Api Documentation** Located at https://localhost:8443/aqcu-gateway/swagger-iu.html
- **Hystrix Dashboard** Located at https://localhost:8443/aqcu-gateway/hystrix/monitor?stream=https%3A%2F%2Flocalhost%3A8443%2Faqcu-gateway%2Fhystrix.stream%20
- **CIDA Auth** token forwarding
- **Leagcy Endpoint Mapping** Each major endpoint has it's own mapping and timeout configuration

## Running the Application

The war built using maven can be deployed the same as any other to a Tomcat instance. Some additional configurations are needed before starting Tomcat:

- At this time, it only works when deployed with all the other aqcu modules. To wire it into them, modify the context.xml's "aqcu.reports.webservice" value to "https://localhost:8443/aqcu-gateway/aqcu-webservice"
- Add ```<Parameter name="spring.config.location" value="${catalina.base}/conf/aqcu-gateway-application.yml" />``` to the context.xml file
- Copy aqcu-gateway-application.yml to the conf folder and adjust any values as required.

Once Tomcat is started, AQCU should function similar to before. Swagger and the Hystrix Dashboard should give you interesting info on the functioning of the gateway.
