androidApplication {
    namespace = "org.example.app"

    dependencies {
        // Core dependencies for modern Android UI and utilities
        implementation("androidx.core:core-ktx:1.12.0")
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("com.google.android.material:material:1.11.0")
        implementation("androidx.recyclerview:recyclerview:1.3.2")
        implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")

        // Existing sample deps
        implementation("org.apache.commons:commons-text:1.11.0")
        implementation(project(":utilities"))
    }

    // Ensure at least one JUnit 4 test can be discovered by default test runner
    testing {
        dependencies {
            implementation("junit:junit:4.13.2")
        }
    }
}
