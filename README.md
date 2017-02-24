[![Build Status](https://travis-ci.org/bootique/bootique-shiro.svg)](https://travis-ci.org/bootique/bootique-shiro)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.bootique.shiro/bootique-shiro/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.bootique.shiro/bootique-shiro/)

# bootique-shiro

## Overview

Integration of Apache Shiro security engine with Bootique. Consists of 2 modules - `bootique-shiro` and 
`bootique-shiro-web`. `bootique-shiro` provides injectable Shiro 
[SecurityManager](https://github.com/apache/shiro/blob/master/core/src/main/java/org/apache/shiro/mgt/SecurityManager.java) 
built on top of a set of injectable Shiro 
[Realms](https://github.com/apache/shiro/blob/master/core/src/main/java/org/apache/shiro/realm/Realm.java), but otherwise
does not provide any guideance as to how to apply Shiro in your app.

More interesting is `bootique-shiro-web`. In addition to injectable realms, it provides `ShiroFilter` servlet filter 
that can be contributed to a webapp to intercept requests and run Shiro 
["chains"](https://shiro.apache.org/web.html#Web-%7B%7B%5Curls%5C%7D%7D), authenticating and authorizing requests.
Instead of .ini, the chains are configured via via Bootique configuration (such as YAML files), as described below.

## Configuration

As mentioned above, `bootique-shiro` does not use .ini configuration typical in Shiro apps. Instead it assumes that 
objects that comprise the Shiro stack (SecurityManager, SessionManager, etc) are provided via dependency injection. So
if you are porting an existing app, `[main]` .ini section should be converted to DI. Configuration corresponding to
 other sections, such as `[urls]`, `[users]` and `[roles]`, is laoded via Bootique (e.g. from YAML files).

## Usage

Include ```bootique-shiro```:
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.bootique.bom</groupId>
            <artifactId>bootique-bom</artifactId>
            <version>0.22</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

...

<dependency>
	<groupId>io.bootique.shiro</groupId>
	<artifactId>bootique-shiro</artifactId>
</dependency>
<dependency>
	<groupId>io.bootique.shiro</groupId>
	<artifactId>bootique-shiro-web</artifactId>
</dependency>
```

Install `ShiroFilter` (or your own subclass) in Jetty to intercept all or parts of your application URL space, 
contribute realms:

```java
@Provides
@Singleton
MappedFilter<ShiroFilter> mapShiroFilter(ShiroFilter filter) {
    return new MappedFilter(filter, Collections.singleton("/*"), 0);
}

@Override
public void configure(Binder binder) {
    
    // contribute realms
    ShiroModule.extend(binder).addRealm(MyRealm.class);
    
    // install ShiroFilter
    TypeLiteral<MappedFilter<ShiroFilter>> tl = new TypeLiteral<MappedFilter<ShiroFilter>>() {};
    JettyModule.extend(binder).addMappedFilter(tl);
}
```

Configure your app.

_TODO: config example... Use `-H` flag to run your app to see built-in documentation._

