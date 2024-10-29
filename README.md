Philosophers Stone

The library that will help you extend the life of your application. Use at your own risk!

**How to use:**
1. Add dependency:
```kotlin
dependencyResolutionManagement {
    repositories {
        maven("https://jitpack.io")
    }
}

dependencies {
    implementation("com.github.0xera.philosophers-stone:stone:0.1")
}
```

2. Add in your `Application` onCreate method:
```kotlin
PhilosophersStone.init(
    app = this, // Application's instance
    enabled = true,
    logging = true,
    calculateLifeTime = true
)
```