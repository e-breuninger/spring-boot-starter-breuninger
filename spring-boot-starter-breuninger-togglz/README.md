# spring-boot-starter-breuninger-togglz

Feature toggles for spring-boot-starter-breuninger.

## Usage

There is an example showing how to use the `spring-boot-starter-breuninger-togglz` library: see `examples/example-togglz`.  

Basically, you have to:
1. Add `com.breuninger.boot:spring-boot-starter-breuninger-togglz:<version>` to you project.
2. In clustered environments, also add `com.breuninger.boot:spring-boot-starter-breuninger-mongo:<version>` and configure MongoDB's
 properties as described in [spring-boot-starter-breuninger-mongo](https://github.com/e-breuninger/spring-boot-starter-breuninger/tree/master/spring-boot-starter-breuninger-mongo).
3. Implement your Features enum:
 ```java
 public enum Features implements Feature {
 
     @Label("Toggles the 'Hello Breuninger' message displayed on http://localhost:8080/example page")
     HELLO_TOGGLE;
 
     public boolean isActive() {
         return FeatureContext.getFeatureManager().isActive(this);
     }
 }
 ```
 4. Override the default FeatureClassProvider as a Spring Bean:
 ```java
@Component
public class FeatureClassProvider implements com.breuninger.boot.togglz.FeatureClassProvider {

    @Override
    public Class<? extends Feature> getFeatureClass() {
        return Features.class;
    }
}
```

### Persisting Feature State

In clustered environments it is necessary to persist the feature state, otherwise developers will have
to manually take care, that all service instances have the same state of the toggles.

By simply adding (and configuring) `spring-boot-starter-breuninger-mongo` to your project, you will automatically get a `MongoTogglzRepository`,
implementing `org.togglz.core.repository.StateRepository` interface. 

### Togglz Console

By default, the Togglz web console is configured and added to the 'Admin' navigation bar of the /internal pages. 

You can disable the console by setting `breuninger.togglz.console.enabled=false` in your application.properties.

### LDAP Authentication for Togglz Console

Authentication can be enabled for the console by configuring an LDAP server:
* `breuninger.ldap.enabled=true` Enables LDAP authentication. Default value is `false`.

If enabled, the following properties must be provided:
* `breuninger.ldap.host=<host>` Host name of the LDAP server.
* `breuninger.ldap.base-dn=<base dn>` The base distinguished name (base DN)
* `breuninger.ldap.rdn-identifier=<rdn>` The relative DN (RDN)

The port can be changed, too:
* `breuninger.ldap.port=<port>` Port of the LDAP server. Default value is `389`.

If this is not sufficient, Spring Security might be an alternative.

### Using Features

Nothing special about that: just use it as [documented](https://www.togglz.org):

```java
class Foo {

    public void doSomethingUseful() {
        if (HELLO_TOGGLE.isActive()) {
            sayHello();
        } else {
            saySomethingElse();
        }
    }
}
```
### Features in Thymeleaf

Sometimes it is necessary to use feature toggles in the frontend. Because most spring-boot-starter-breuninger are using
Thymeleaf as a template engine, a support for Thymeleaf templates would be nice. 

Fortunately, there already is a solution: a [Thymeleaf Togglz Dialect](https://github.com/heneke/thymeleaf-extras-togglz).

Example:
```xhtml
<html lang="en" xmlns:togglz="https://github.com/heneke/thymeleaf-extras-togglz">
    <body>
        <div togglz:active="YOUR_FEATURE_NAME">
            content only visible if feature is active
        </div>
        <div togglz:inactive="YOUR_FEATURE_NAME">
            content only visible if feature is <b>inactive</b>
        </div>
    </body>
</html>

```
