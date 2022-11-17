import com.gchristov.thecodinglove.gradleplugins.Deps

plugins {
    id("javascript-node-library-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                // TODO: These should not be directly accessed
                implementation(projects.kmpCommonFirebase)
                implementation(projects.kmpHtmlparser)
                implementation(projects.kmpCommonNetwork)
                implementation(projects.moduleA)
                // Needed to get the Firebase deployment to work
                implementation(npm(Deps.Firebase.firebase.name, Deps.Firebase.firebase.version))
                implementation(npm(Deps.Firebase.admin.name, Deps.Firebase.admin.version))
                implementation(npm(Deps.Firebase.functions.name, Deps.Firebase.functions.version))
            }
        }
    }
}