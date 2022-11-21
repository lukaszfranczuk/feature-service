# Features Service

Service is providing REST API - [Swagger documentation](http://localhost:8080/swagger-ui)

### Build project
Run ``./gradlew build``

### Run service:

To run service choose one of the following options. It doesn't require any additional services. By default it has the H2
database with Liquibase configured.

#### First option:

In the IDE run FeaturesServiceApplication class

#### Second option:

In the terminal run ``./gradlew bootRun``

### Login to the application

By default, application is configured with three users

* ``admin_user`` with ``adminPwd`` password and roles: ``ROLE_ADMIN``
* ``normal_user`` with ``normalPwd`` password and roles: ``ROLE_USER``
* ``normal_user2`` with ``normalPwd`` password and roles: ``ROLE_USER``

``/login`` endpoint is responsible for authentication

### Postman collection

In the ``postman-collection`` catalog you can find a Postman collection with pre-configured environments, one for
localhost with ``admin_user`` and second with localhost with ``normal_user``

### Enable H2 console

Set following property in ``application.yaml``, default is set to ``false``

```
  h2:
    console:
      enabled: true
```

### Enable Swagger UI

Set following property in ``application.yaml``, default is set to ``true``

```
springfox:
  documentation:
    swagger-ui:
      enabled: true
```
