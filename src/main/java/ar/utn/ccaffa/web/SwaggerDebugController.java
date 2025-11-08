package ar.utn.ccaffa.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SwaggerDebugController {

    @GetMapping("/swagger-test")
    public String testSwagger() {
        return "Si puedes ver este mensaje, el problema no es con los permisos generales de la aplicación. " +
               "Intenta acceder a /swagger-ui/index.html después de reiniciar la aplicación.";
    }

    @GetMapping("/v3-test")
    public String testApiDocs() {
        return "Si puedes ver este mensaje, el problema no es con los permisos para acceder a la documentación de API. " +
               "Intenta acceder a /v3/api-docs/public después de reiniciar la aplicación.";
    }
}
