<h1>Java Project @Preauthorize Crawler<h2></h2>

<h2>Description</h2>

This tool collects and lists all @PreAuthorize authorities used in @RestController classes within a Java project, making it easier to audit and document endpoint security settings.

<h2>Usage</h2>:
<code>
  
  Options:
                    
    --project-dir=<path>   Set the project's directory (by default = '.')
    --output-dir=<path>    Set the output directory (by default = 'scratches')
    --rest-controller-mask=<RestController>  Root part in names of RestController-classes (by default = 'RestController')
    --swagger-host=<url>   Set the Swagger host URL (by default = 'http://localhost:8080/webapp/swagger-ui/index.html?configUrl=/webapp/v3/api-docs/swagger-config#/'"
    --help                Show this help message
                    
  Usage example:
    java preauthorize-crawler-1.0-all.jar --project-dir=./../my-webapp --output-dir=my-scratches --swaggerhost=http://dev.mycrosoft.com:80A0/webapp/swagger-ui/index.html?configrl=/webapp/v3/api-docs/swagger-config#/
                    
    Thank you for using this @Preauthorize-crawler
    
</code>
