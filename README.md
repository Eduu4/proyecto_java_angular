Proyecto generado con JHipster que combina un backend Spring Boot (Java 17 + Maven) y un frontend Angular.

- **Backend**: Spring Boot + Maven (carpeta: `src/main/java`)
- **Frontend**: Angular (carpeta: `src/main/webapp`)
- **Bases de datos**: PostgreSQL, Redis (configuración en `src/main/docker` y `docker-compose.yml`)

**Prerequisitos**

- **Java 17** instalado y `JAVA_HOME` configurado.
- **Maven** (se puede usar el wrapper incluido `mvnw`).
- **Node.js** y **npm** (para desarrollo frontend). Versiones recomendadas: Node 18+ / npm 9+.
- **Docker** y **Docker Compose** (para ejecutar PostgreSQL/Redis localmente).
- **PowerShell** (Windows) — los comandos mostrados usan PowerShell.

Instalación y ejecución (rápida)

1. Clonar el repositorio:

```powershell
git clone <repo-url> C:\sistema\angularv3
cd C:\sistema\angularv3
```

2. Levantar servicios de soporte (Postgres + Redis) usando Docker Compose:

```powershell
docker-compose up -d
# Ver logs (opcional): docker-compose logs -f
```

3. Iniciar la aplicación (script conveniencia):

```powershell
.\iniciar.ps1
# Este script inicia los contenedores definidos en `docker-compose.yml`, espera a que la
# dependencias (por ejemplo PostgreSQL) estén listas y arranca la aplicación backend y
# el frontend Angular (si está configurado en el script).
```

4. Alternativa: construir y ejecutar con Maven (backend + frontend compilado):

```powershell
.\mvnw -DskipTests package
# Ejecutar jar generado:
java -jar target\angularv-3-0.0.1-SNAPSHOT.jar
```

Frontend en modo desarrollo (si desea trabajar en la UI separadamente)

1. Ir a la carpeta del frontend y usar el wrapper si es necesario (el proyecto está configurado por JHipster):

```powershell
# Desde la raíz
npm install
npm start
# o, si el proyecto expone scripts npm específicos:
# npm run webpack:dev
```

Importar entidades JDL

Si empieza desde cero o quiere regenerar entidades desde `finanzas.jdl`:

```powershell
# Requiere jhipster instalado globalmente
jhipster import-jdl finanzas.jdl
```

Pruebas

Ejecutar tests Java:

```powershell
.\mvnw test
```

Ejecutar tests frontend (si existen):

```powershell
npm test
```

Despliegue y empaquetado

Para crear el artefacto listo para despliegue:

```powershell
.\mvnw -Pprod -DskipTests package
```

El JAR resultante estará en `target/`.

Acceder a la aplicación

- Interfaz web (por defecto): `http://localhost:8080/`
- API REST: `http://localhost:8080/api/`
- Swagger UI si está habilitado: `http://localhost:8080/swagger-ui/index.html`

Solución de problemas comunes

- BUILD fails tras cambios menores (needle): si ves errores referidos a "jhipster-needle ..." revise `src/main/java/*/config/*` y asegúrate de que los marcadores (needles) no fueron eliminados accidentalmente.
- Angular errors (NG8001/NG6004 etc): revise imports en componentes standalone y módulos (`FontAwesomeModule`, `CommonModule`) y que los componentes estén exportados en `shared.module.ts`.
- FontAwesome no reconocido: importar `FontAwesomeModule` en el componente o módulo que lo usa.
- Problemas con SASS: advertencias de `@import` de Sass — migrar a `@use` si decide actualizar estilos.
- Puertos ocupados: confirme `8080`, `5432`, `6379` no estén en uso o cambie la configuración.
- Si Docker Compose falla: inspeccione `docker-compose logs` y ejecute `docker-compose down` y `docker-compose up -d` nuevamente.

Consejos para desarrolladores

- Para cambios de backend, use `./mvnw -DskipTests package` y revise `target/`.
- Para desarrollo frontend, ejecute `npm start` y reutilice las herramientas de HMR (si configurado).
- Para regenerar entidades con JDL, ejecutar `jhipster import-jdl finanzas.jdl`.

Estructura importante del proyecto

- `src/main/java` — código Java (controladores, servicios, repositorios)
- `src/main/webapp` — código Angular (componentes, assets)
- `src/main/resources/config` — archivos de configuración (`application.yml`)
- `src/main/docker` — definiciones Docker para servicios auxiliares
- `finanzas.jdl` — modelo JDL del dominio

Contacto y ayuda

Si necesita ayuda con este repositorio, abra un issue o contacte al mantenedor del proyecto.

Licencia

Ver `LICENSE` si existe en el repositorio (si no, consulte al autor para añadirla).

# angularv3

This application was generated using JHipster 8.11.0, you can find documentation and help at [https://www.jhipster.tech/documentation-archive/v8.11.0](https://www.jhipster.tech/documentation-archive/v8.11.0).

## Project Structure

Node is required for generation and recommended for development. `package.json` is always generated for a better development experience with prettier, commit hooks, scripts and so on.

In the project root, JHipster generates configuration files for tools like git, prettier, eslint, husky, and others that are well known and you can find references in the web.

`/src/*` structure follows default Java structure.

- `.yo-rc.json` - Yeoman configuration file
  JHipster configuration is stored in this file at `generator-jhipster` key. You may find `generator-jhipster-*` for specific blueprints configuration.
- `.yo-resolve` (optional) - Yeoman conflict resolver
  Allows to use a specific action when conflicts are found skipping prompts for files that matches a pattern. Each line should match `[pattern] [action]` with pattern been a [Minimatch](https://github.com/isaacs/minimatch#minimatch) pattern and action been one of skip (default if omitted) or force. Lines starting with `#` are considered comments and are ignored.
- `.jhipster/*.json` - JHipster entity configuration files

- `npmw` - wrapper to use locally installed npm.
  JHipster installs Node and npm locally using the build tool by default. This wrapper makes sure npm is installed locally and uses it avoiding some differences different versions can cause. By using `./npmw` instead of the traditional `npm` you can configure a Node-less environment to develop or test your application.
- `/src/main/docker` - Docker configurations for the application and services that the application depends on

## Development

The build system will install automatically the recommended version of Node and npm.

We provide a wrapper to launch npm.
You will only need to run this command when dependencies change in [package.json](package.json).

```
./npmw install
```

We use npm scripts and [Angular CLI][] with [Webpack][] as our build system.

If you are using redis as a cache, you will have to launch a cache server.
To start your cache server, run:

```
docker compose -f src/main/docker/redis.yml up -d
```

The cache can also be turned off by adding to the application yaml:

```yaml
spring:
  cache:
    type: none
```

See [here](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-caching.html#boot-features-caching-provider-none) for details.

**WARNING**: If you're using the second level Hibernate cache and disabling the Spring cache, you have to disable the second level Hibernate cache as well since they are using
the same CacheManager.

Run the following commands in two separate terminals to create a blissful development experience where your browser
auto-refreshes when files change on your hard drive.

```
./mvnw
./npmw start
```

Npm is also used to manage CSS and JavaScript dependencies used in this application. You can upgrade dependencies by
specifying a newer version in [package.json](package.json). You can also run `./npmw update` and `./npmw install` to manage dependencies.
Add the `help` flag on any command to see how you can use it. For example, `./npmw help update`.

The `./npmw run` command will list all the scripts available to run for this project.

### PWA Support

JHipster ships with PWA (Progressive Web App) support, and it's turned off by default. One of the main components of a PWA is a service worker.

The service worker initialization code is disabled by default. To enable it, uncomment the following code in `src/main/webapp/app/app.config.ts`:

```typescript
ServiceWorkerModule.register('ngsw-worker.js', { enabled: false }),
```

### Managing dependencies

For example, to add [Leaflet][] library as a runtime dependency of your application, you would run following command:

```
./npmw install --save --save-exact leaflet
```

To benefit from TypeScript type definitions from [DefinitelyTyped][] repository in development, you would run following command:

```
./npmw install --save-dev --save-exact @types/leaflet
```

Then you would import the JS and CSS files specified in library's installation instructions so that [Webpack][] knows about them:
Edit [src/main/webapp/app/app.config.ts](src/main/webapp/app/app.config.ts) file:

```
import 'leaflet/dist/leaflet.js';
```

Edit [src/main/webapp/content/scss/vendor.scss](src/main/webapp/content/scss/vendor.scss) file:

```
@import 'leaflet/dist/leaflet.css';
```

Note: There are still a few other things remaining to do for Leaflet that we won't detail here.

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].

### Using Angular CLI

You can also use [Angular CLI][] to generate some custom client code.

For example, the following command:

```
ng generate component my-component
```

will generate few files:

```
create src/main/webapp/app/my-component/my-component.component.html
create src/main/webapp/app/my-component/my-component.component.ts
update src/main/webapp/app/app.config.ts
```

## Building for production

### Packaging as jar

To build the final jar and optimize the angularv3 application for production, run:

```
./mvnw -Pprod clean verify
```

This will concatenate and minify the client CSS and JavaScript files. It will also modify `index.html` so it references these new files.
To ensure everything worked, run:

```
java -jar target/*.jar
```

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

Refer to [Using JHipster in production][] for more details.

### Packaging as war

To package your application as a war in order to deploy it to an application server, run:

```
./mvnw -Pprod,war clean verify
```

### JHipster Control Center

JHipster Control Center can help you manage and control your application(s). You can start a local control center server (accessible on http://localhost:7419) with:

```
docker compose -f src/main/docker/jhipster-control-center.yml up
```

## Testing

### Spring Boot tests

To launch your application's tests, run:

```
./mvnw verify
```

### Client tests

Unit tests are run by [Jest][]. They're located near components and can be run with:

```
./npmw test
```

## Others

### Code quality using Sonar

Sonar is used to analyse code quality. You can start a local Sonar server (accessible on http://localhost:9001) with:

```
docker compose -f src/main/docker/sonar.yml up -d
```

Note: we have turned off forced authentication redirect for UI in [src/main/docker/sonar.yml](src/main/docker/sonar.yml) for out of the box experience while trying out SonarQube, for real use cases turn it back on.

You can run a Sonar analysis with using the [sonar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) or by using the maven plugin.

Then, run a Sonar analysis:

```
./mvnw -Pprod clean verify sonar:sonar -Dsonar.login=admin -Dsonar.password=admin
```

If you need to re-run the Sonar phase, please be sure to specify at least the `initialize` phase since Sonar properties are loaded from the sonar-project.properties file.

```
./mvnw initialize sonar:sonar -Dsonar.login=admin -Dsonar.password=admin
```

Additionally, Instead of passing `sonar.password` and `sonar.login` as CLI arguments, these parameters can be configured from [sonar-project.properties](sonar-project.properties) as shown below:

```
sonar.login=admin
sonar.password=admin
```

For more information, refer to the [Code quality page][].

### Docker Compose support

JHipster generates a number of Docker Compose configuration files in the [src/main/docker/](src/main/docker/) folder to launch required third party services.

For example, to start required services in Docker containers, run:

```
docker compose -f src/main/docker/services.yml up -d
```

To stop and remove the containers, run:

```
docker compose -f src/main/docker/services.yml down
```

[Spring Docker Compose Integration](https://docs.spring.io/spring-boot/reference/features/dev-services.html) is enabled by default. It's possible to disable it in application.yml:

```yaml
spring:
  ...
  docker:
    compose:
      enabled: false
```

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a Docker image of your app by running:

```sh
npm run java:docker
```

Or build a arm64 Docker image when using an arm64 processor os like MacOS with M1 processor family running:

```sh
npm run java:docker:arm64
```

Then run:

```sh
docker compose -f src/main/docker/app.yml up -d
```

For more information refer to [Using Docker and Docker-Compose][], this page also contains information on the Docker Compose sub-generator (`jhipster docker-compose`), which is able to generate Docker configurations for one or several JHipster applications.

## Continuous Integration (optional)

To configure CI for your project, run the ci-cd sub-generator (`jhipster ci-cd`), this will let you generate configuration files for a number of Continuous Integration systems. Consult the [Setting up Continuous Integration][] page for more information.

[JHipster Homepage and latest documentation]: https://www.jhipster.tech
[JHipster 8.11.0 archive]: https://www.jhipster.tech/documentation-archive/v8.11.0

## 🔌 Prueba de Webhook de WhatsApp

Para probar la integración de WhatsApp, consulta [WHATSAPP_TESTING_GUIDE.md](./WHATSAPP_TESTING_GUIDE.md).

**Inicio rápido:**

```powershell
.\test-whatsapp-webhook.ps1 -Interactivo
```

Esto abrirá un menú interactivo con opciones predefinidas para probar el webhook sin necesidad de WhatsApp real.

---

## 📚 Documentación Adicional

- **Guía de Pruebas de WhatsApp**: [WHATSAPP_TESTING_GUIDE.md](./WHATSAPP_TESTING_GUIDE.md)
- **Guía de Testing de WhatsApp para usuarios**: [WHATSAPP_TESTING_GUIDE.md](./WHATSAPP_TESTING_GUIDE.md)

[Using JHipster in development]: https://www.jhipster.tech/documentation-archive/v8.11.0/development/
[Using Docker and Docker-Compose]: https://www.jhipster.tech/documentation-archive/v8.11.0/docker-compose
[Using JHipster in production]: https://www.jhipster.tech/documentation-archive/v8.11.0/production/
[Running tests page]: https://www.jhipster.tech/documentation-archive/v8.11.0/running-tests/
[Code quality page]: https://www.jhipster.tech/documentation-archive/v8.11.0/code-quality/
[Setting up Continuous Integration]: https://www.jhipster.tech/documentation-archive/v8.11.0/setting-up-ci/
[Node.js]: https://nodejs.org/
[NPM]: https://www.npmjs.com/
[Webpack]: https://webpack.github.io/
[BrowserSync]: https://www.browsersync.io/
[Jest]: https://jestjs.io
[Leaflet]: https://leafletjs.com/
[DefinitelyTyped]: https://definitelytyped.org/
[Angular CLI]: https://angular.dev/tools/cli
