BreninSul OkHTTP interceptor with Spring Boot starter.

Logging interceptor implementation and stater to autoregister it in Spring context

Client is not registered in this starter. Interceptor should be manually added by

````kotlin
    val client= OkHttpClient.Builder()
        .addInterceptor(thisInterceptor)
        .build()
````


| Parameter                                                | Type             | Description                                      |
|----------------------------------------------------------|------------------|--------------------------------------------------|
| `okhttp.logging-interceptor.enabled`                     | Boolean          | Enable autoconfig for this starter               |
| `okhttp.logging-interceptor.logging-level`               | JavaLoggingLevel | Logging level of messages                        |
| `okhttp.logging-interceptor.max-body-size`               | Int              | Max logging body size                            |
| `okhttp.logging-interceptor.order`                       | Int              | Filter order (Ordered interface)                 |
| `okhttp.logging-interceptor.new-line-column-symbols`     | Int              | How many symbols in first column (param name)    |
| `okhttp.logging-interceptor.request.id-included`         | Boolean          | Is request id included to log message (request)  |
| `okhttp.logging-interceptor.request.uri-included`        | Boolean          | Is uri included to log message (request)         |
| `okhttp.logging-interceptor.request.took-time-included`  | Boolean          | Is timing included to log message (request)      |
| `okhttp.logging-interceptor.request.headers-included`    | Boolean          | Is headers included to log message (request)     |
| `okhttp.logging-interceptor.request.body-included`       | Boolean          | Is body included to log message (request)        |
| `okhttp.logging-interceptor.response.id-included`        | Boolean          | Is request id included to log message (response) |
| `okhttp.logging-interceptor.response.uri-included`       | Boolean          | Is uri included to log message (response)        |
| `okhttp.logging-interceptor.response.took-time-included` | Boolean          | Is timing included to log message (response)     |
| `okhttp.logging-interceptor.response.headers-included`   | Boolean          | Is headers included to log message (response)    |
| `okhttp.logging-interceptor.response.body-included`      | Boolean          | Is body included to log message (response)       |

You can additionally configure logging for each request by passing headers from `io.github.breninsul.okhttp.logging.OkHttpConfigHeaders` to request


add the following dependency:

````kotlin
dependencies {
//Other dependencies
    implementation("io.github.breninsul:okhttp-logging-interceptor:${version}")
//Other dependencies
}

````
### Example of log messages

````
===========================CLIENT OKHttp Request begin===========================
=ID           : 1613-55
=URI          : POST https://test-c.free.beeceptor.com/
=Headers      : headerfirst:HeaderValueFirst;headersecond:HeaderValueSecond
=Body         : {"someKey":"someval"}
===========================CLIENT OKHttp Request end===========================

===========================CLIENT OKHttp Response begin===========================
=ID           : 1613-55
=URI          : 200 POST https://test-c.free.beeceptor.com/
=Took         : 5897 ms
=Headers      : access-control-allow-origin:*;alt-svc:h3=":443"; ma=2592000;content-type:text/plain;date:Tue, 18 Jun 2024 07:32:35 GMT;vary:Accept-Encoding
=Body         :
Hey ya! Great to see you here. Btw, nothing is configured for this request path. Create a rule and start building a mock API.

===========================CLIENT OKHttp Response end===========================
````
