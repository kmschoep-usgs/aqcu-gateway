package gov.usgs.aqcu.lambda;

public class LambdaFunctionConfig {
    private String name;
    private Integer timeout;

    public LambdaFunctionConfig() {};
    public LambdaFunctionConfig(String name, Integer timeout) {
        this.name = name;
        this.timeout = timeout;
    }

    public String getName() {
        return name;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public void setName(String name) {
        this.name = name;
    }
}