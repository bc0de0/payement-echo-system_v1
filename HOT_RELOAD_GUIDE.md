# Hot Reload Guide - Payment Echo System

## Overview

This project includes Spring Boot DevTools for automatic application restart and live reload functionality during development.

## Features

### 1. Automatic Restart

- Automatically restarts the application when classpath files change
- Faster than a full restart (uses two classloaders)
- Excludes static resources and templates from restart

### 2. Live Reload

- Automatically triggers browser refresh when resources change
- Works with LiveReload browser extensions

## Setup

### 1. Install LiveReload Browser Extension (Optional)

- **Chrome**: [LiveReload](https://chrome.google.com/webstore/detail/livereload/jnihajbhpnppcggjgedjdllhdggplhhk)
- **Firefox**: [LiveReload](https://addons.mozilla.org/en-US/firefox/addon/livereload-web-extension/)
- **Safari**: [LiveReload](https://apps.apple.com/app/livereload/id482898991)

### 2. Run Application

```bash
./gradlew bootRun
```

Or use your IDE's run configuration.

## How It Works

### Automatic Restart Triggers

The application will automatically restart when you modify:

- Kotlin source files in `src/main/kotlin/`
- Configuration files in `src/main/resources/`
- Build files (`build.gradle.kts`)

### Excluded from Restart

These files won't trigger a restart:

- Static resources (`static/`, `public/`)
- Templates (`templates/`)
- Maven metadata files

### Live Reload Port

- Default LiveReload port: **35729**
- The application automatically starts a LiveReload server
- Browser extensions connect to this port automatically

## Configuration

The DevTools configuration is in `application.properties`:

```properties
spring.devtools.restart.enabled=true
spring.devtools.livereload.enabled=true
spring.devtools.restart.additional-paths=src/main/kotlin,src/main/resources
spring.devtools.restart.exclude=static/**,public/**,templates/**,META-INF/maven/**,META-INF/resources/**
```

## Usage Tips

### 1. Development Mode

- Always run with `bootRun` or your IDE's run configuration
- DevTools is automatically disabled in production (packaged JAR)

### 2. Faster Restarts

- DevTools uses two classloaders for faster restarts
- Only changed classes are reloaded

### 3. Disable Restart

If you want to disable automatic restart temporarily:

```properties
spring.devtools.restart.enabled=false
```

### 4. Custom Exclusions

Add custom paths to exclude from restart:

```properties
spring.devtools.restart.exclude=path/to/exclude/**
```

## IDE Configuration

### IntelliJ IDEA

1. Go to **File** → **Settings** → **Build, Execution, Deployment** → **Compiler**
2. Enable **Build project automatically**
3. Press `Ctrl+Shift+A` (or `Cmd+Shift+A` on Mac) and search for "Registry"
4. Enable `compiler.automake.allow.when.app.running`

### VS Code

1. Install the **Spring Boot Extension Pack**
2. Enable auto-save
3. The extension will handle hot reload automatically

### Eclipse

1. Go to **Project** → **Build Automatically**
2. DevTools will handle the rest

## Troubleshooting

### Restart Not Working

1. Check that `spring-boot-devtools` is in `developmentOnly` configuration
2. Verify the application is running with `bootRun` (not a packaged JAR)
3. Check that files are being saved

### Live Reload Not Working

1. Ensure LiveReload browser extension is installed and enabled
2. Check that port 35729 is not blocked by firewall
3. Verify the extension is connected (icon should show "connected")

### Too Many Restarts

1. Exclude unnecessary paths in `application.properties`
2. Use `spring.devtools.restart.trigger-file` to set a trigger file
3. Disable restart for specific file patterns

## Production

**Important**: DevTools is automatically disabled when:

- Running a packaged JAR file
- Running with `java -jar`
- Deployed to production environments

This ensures DevTools never runs in production.

## Additional Resources

- [Spring Boot DevTools Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.devtools)
- [LiveReload Protocol](http://livereload.com/protocol/)
