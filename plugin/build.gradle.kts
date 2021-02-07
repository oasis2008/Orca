import org.jetbrains.kotlin.cfg.pseudocode.or
import org.jetbrains.kotlin.konan.properties.Properties

buildscript {
    repositories {
        jcenter()
        google()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.21")
        classpath("com.novoda:bintray-release:0.9.2")
    }
}


plugins {
    id("org.gradle.kotlin.kotlin-dsl") version "1.4.0"
    `java-gradle-plugin`
     id("maven-publish")
}


plugins.apply("com.github.dcendents.android-maven")
group= "com.occ.orca"

val properties =  Properties()
properties.load(project.file("../local.properties").inputStream())
val pVersion = "2.0.0-release09"


val parentDir = project.rootDir.path
val orca_core = file(parentDir + File.separator + "orca-core")
val archivesBaseName = "Orca"
task("zipNative",Zip::class){
    destinationDir = project.file("build/libs")
    archiveName  = "$archivesBaseName-${pVersion}.jarx"
    from(project.zipTree(getAnyJarPath(project)))
    include("META-INF/**")
    include("com/**")
    from(orca_core.canonicalPath)
    include("src/main/**")
    exclude("CMakeLists.txt")
    exclude("src/main/AndroidManifest.xml")

    doLast {
        val originJar = project.file(getAnyJarPath(project))
        val xJar = project.file("build/libs/$archivesBaseName-${pVersion}.jarx")
        originJar.delete()
        xJar.renameTo(originJar)
    }
}

fun getAnyJarPath(project: Project):Any{
    val rootDir = "build/libs/"
    val files =  project.file(rootDir).listFiles()
    for(file in files){
        if(file.path.endsWith("jar")){
            return file.path
        }
    }
    return ""
}

tasks.getByName("jar").finalizedBy("zipNative")


repositories {
    mavenCentral()
    google()
    jcenter()
}

dependencies {
    compileOnly(gradleApi())
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.21")
    compileOnly("com.android.tools.build:gradle:4.0.1")
    implementation("com.squareup:javapoet:1.13.0")
}

gradlePlugin {
    plugins {
        create("Orca.So") {
            id = "Orca.So"
            implementationClass = "com.occ.orca.OrcaPlugin"
        }
    }
}



