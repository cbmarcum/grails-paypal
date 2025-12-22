package grails.paypal

import groovy.transform.CompileStatic

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import grails.plugins.metadata.PluginSource
import org.springframework.context.annotation.ComponentScan

@PluginSource
@CompileStatic
@ComponentScan(basePackages = ['org.grails.plugin.paypal'])
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }
}
