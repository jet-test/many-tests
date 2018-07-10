package one.trufle.many.tests

import java.io.File

fun main(args: Array<String>) {

    val modules: Int = args.getOrElse(0) {"10"}.toInt()
    val tests: Int = args.getOrElse(1) {"10"}.toInt()
    val methods: Int = args.getOrElse(2) {"10"}.toInt()

    Generator(Configuration((1..modules).map { "module$it" }, tests, methods)).generate()
}

private const val root = "mytest"

class Generator(private val config: Configuration) {
    private fun mainModule() {
        File(root).mkdir()
        File("$root/pom.xml").writeText(RootPom(config.modules).create())
    }

    fun generate() {
        File(root).deleteRecursively()
        mainModule()
        val methods = (1..config.methods).map { GeneratedMethod("test$it") }
        val tests = (1..config.tests).map { GeneratedTest("Generated${it}Test", methods) }
        config.modules.asSequence()
                .map { GeneratedModule(it, ModulePom(it)).apply { create() } }.forEach { module ->
                    tests.asSequence().forEach { test ->
                        File("${module.path}/${test.name}.java").writeText(test.create(module.name))
                    }
                }
    }
}

class GeneratedModule(val name: String, val pom: GeneratedPom) {
    val path = "$root/$name/src/test/java/one/trifle/many/tests"
    fun create() {
        File("$root/$name").mkdir()
        File("$root/$name/pom.xml").writeText(pom.create())
        File(path).mkdirs()
    }
}

interface GeneratedPom {
    fun create(): String
}

class RootPom(private val modules: List<String>) : GeneratedPom {
    override fun create(): String = """
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>one.trifle</groupId>
    <artifactId>many-tests</artifactId>
    <packaging>pom</packaging>
    <version>0.0.1-SNAPSHOT</version>
    <modules>
${modules.joinToString(separator = "\n") { "        <module>$it</module>" }}
    </modules>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>

        <project.package>one.trifle</project.package>
        <project.package.path>one/trifle</project.package.path>

        <java.version>1.8</java.version>
        <junit.version>4.12</junit.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>${'$'}{junit.version}</version>
        </dependency>
    </dependencies>

    <build>
        <sourceDirectory>src/main/java</sourceDirectory>
        <testSourceDirectory>src/test/java</testSourceDirectory>

        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>${'$'}{java.version}</source>
                    <target>${'$'}{java.version}</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
        """.trimIndent()
}

class ModulePom(private val name: String) : GeneratedPom {
    override fun create(): String = """
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>one.trifle</groupId>
        <artifactId>many-tests</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>$name</artifactId>
</project>
    """.trimIndent()
}

class GeneratedTest(val name: String, private val methods: List<GeneratedMethod>) {
    fun create(path: String): String = """
package one.trifle.many.tests.$path;
import org.junit.Test;
public class $name {
${methods.joinToString(separator = "\n") { it.create() }}
}""".trimIndent()
}

class GeneratedMethod(private val name: String) {
    fun create(): String = """
    @Test public void $name() {
        for(int i = 0; i < 100; i++) {
        System.err.println("error: $name");
        System.out.println("out: $name");
        }
    }
"""
}

data class Configuration(val modules: List<String>, val tests: Int, val methods: Int)