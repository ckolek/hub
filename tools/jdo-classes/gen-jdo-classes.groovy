import groovy.json.JsonSlurper
import groovy.text.GStringTemplateEngine

import java.sql.DriverManager

static def getConnection() {
    def url = System.getProperty('db.url')
    def driver = System.getProperty('db.driver')
    def user = System.getProperty('db.user')
    def password = System.getProperty('db.password')

    Class.forName(driver)

    return DriverManager.getConnection(url, user, password)
}

class ClassDefinition {
    Map<String, FieldDefinition> fields = [:]
}

class FieldDefinition {

}

class RelationshipDefinition {

}

def srcDir = new File('src/gen/java')
def resDir = new File('src/gen/resources')
def scriptDir = new File(getClass().protectionDomain.codeSource.location.path).parent

def engine = new GStringTemplateEngine()
def classTemplate = engine.createTemplate(new File(scriptDir, 'jdo-class.template'))

def generateClass = { String _package, String _class, classDefinition ->
    _class += 'Bean'

    def packageDir = new File(srcDir, _package.replaceAll('\\.', '/'))
    def classFile = new File(packageDir, _class + ".java")

    def map = [
            package: _package,
            class: _class
    ]

    classFile.text = classTemplate.make(map).toString()
}

def jsonSlurper = new JsonSlurper()
def definition = jsonSlurper.parse(new File('jdo-classes.json'))
def _package = definition.package;
def classes = definition.classes;

classes.each { _class, classDefinition -> generateClass(_package, _class, classDefinition) }