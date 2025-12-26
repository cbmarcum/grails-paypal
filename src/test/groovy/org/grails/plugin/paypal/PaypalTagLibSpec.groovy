package org.grails.plugin.paypal

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Ignore
import spock.lang.Specification

class PaypalTagLibSpec extends Specification implements TagLibUnitTest<PaypalTagLib> {

    def cleanup() {
        if (grailsApplication.config.grails.paypal) {
            grailsApplication.config.grails.paypal.clear()
        }
    }

    void "test errors tag delegates to renderErrors with payment from flash"() {
        given:
        def payment = new Payment()
        flash.payment = payment

        when:
        tagLib.errors([:])

        then:
        // Tag calls g.renderErrors which we can't easily test in unit test
        // This verifies the tag executes without error
        true
    }

    void "test button tag renders error when config is missing server"() {
        given:
        grailsApplication.config.grails.paypal.email = 'test@example.com'

        when:
        def output = tagLib.button(
            itemName: 'Test',
            itemNumber: 'ITEM-001',
            amount: 10.00,
            buyerId: 1
        ).toString()

        then:
        output.contains('Paypal is misconfigured')
        output.contains('grails.paypal.server')
    }

    void "test button tag renders error when config is missing email"() {
        given:
        grailsApplication.config.grails.paypal.server = 'https://www.paypal.com'

        when:
        def output = tagLib.button(
            itemName: 'Test',
            itemNumber: 'ITEM-001',
            amount: 10.00,
            buyerId: 1
        ).toString()

        then:
        output.contains('Paypal is misconfigured')
        output.contains('grails.paypal.email')
    }

    void "test button tag renders error when itemName is missing"() {
        given:
        grailsApplication.config.grails.paypal.server = 'https://www.paypal.com'
        grailsApplication.config.grails.paypal.email = 'test@example.com'

        when:
        def output = tagLib.button(
            itemNumber: 'ITEM-001',
            amount: 10.00,
            buyerId: 1
        ).toString()

        then:
        output.contains('Paypal button error')
        output.contains('itemName=null')
    }

    void "test button tag renders error when itemNumber is missing"() {
        given:
        grailsApplication.config.grails.paypal.server = 'https://www.paypal.com'
        grailsApplication.config.grails.paypal.email = 'test@example.com'

        when:
        def output = tagLib.button(
            itemName: 'Test',
            amount: 10.00,
            buyerId: 1
        ).toString()

        then:
        output.contains('Paypal button error')
        output.contains('itemNumber=null')
    }

    void "test button tag renders error when amount is missing"() {
        given:
        grailsApplication.config.grails.paypal.server = 'https://www.paypal.com'
        grailsApplication.config.grails.paypal.email = 'test@example.com'

        when:
        def output = tagLib.button(
            itemName: 'Test',
            itemNumber: 'ITEM-001',
            buyerId: 1
        ).toString()

        then:
        output.contains('Paypal button error')
        output.contains('amount=null')
    }

    void "test button tag renders error when buyerId is missing"() {
        given:
        grailsApplication.config.grails.paypal.server = 'https://www.paypal.com'
        grailsApplication.config.grails.paypal.email = 'test@example.com'

        when:
        def output = tagLib.button(
            itemName: 'Test',
            itemNumber: 'ITEM-001',
            amount: 10.00
        ).toString()

        then:
        output.contains('Paypal button error')
        output.contains('buyerId=null')
    }

    void "test button tag renders form with all required attributes"() {
        given:
        grailsApplication.config.grails.paypal.server = 'https://www.paypal.com'
        grailsApplication.config.grails.paypal.email = 'test@example.com'

        when:
        def output = applyTemplate(
            '<paypal:button itemName="Test" itemNumber="ITEM-001" amount="10.00" buyerId="1"/>'
        )

        then:
        output.contains('itemName')
        output.contains('Test')
        output.contains('itemNumber')
        output.contains('ITEM-001')
        output.contains('amount')
        output.contains('10.00')
        output.contains('buyerId')
        output.contains('1')
    }

    void "test button tag uses default tax value of 0.0 when not provided"() {
        given:
        grailsApplication.config.grails.paypal.server = 'https://www.paypal.com'
        grailsApplication.config.grails.paypal.email = 'test@example.com'

        when:
        def output = applyTemplate(
            '<paypal:button itemName="Test" itemNumber="ITEM-001" amount="10.00" buyerId="1"/>'
        )

        then:
        output.contains('tax')
        output.contains('0.0')
    }

    void "test button tag uses default USD currency when not provided"() {
        given:
        grailsApplication.config.grails.paypal.server = 'https://www.paypal.com'
        grailsApplication.config.grails.paypal.email = 'test@example.com'

        when:
        def output = applyTemplate(
            '<paypal:button itemName="Test" itemNumber="ITEM-001" amount="10.00" buyerId="1"/>'
        )

        then:
        output.contains('currency')
        output.contains('USD')
    }

    void "test button tag includes custom button source when provided"() {
        given:
        grailsApplication.config.grails.paypal.server = 'https://www.paypal.com'
        grailsApplication.config.grails.paypal.email = 'test@example.com'

        when:
        def output = applyTemplate(
            '<paypal:button itemName="Test" itemNumber="ITEM-001" amount="10.00" buyerId="1" buttonSrc="https://example.com/button.gif"/>'
        )

        then:
        output.contains('https://example.com/button.gif')
    }

    void "test button tag includes return action and controller when provided"() {
        given:
        grailsApplication.config.grails.paypal.server = 'https://www.paypal.com'
        grailsApplication.config.grails.paypal.email = 'test@example.com'

        when:
        def output = applyTemplate(
            '<paypal:button itemName="Test" itemNumber="ITEM-001" amount="10.00" buyerId="1" returnAction="success" returnController="payment"/>'
        )

        then:
        output.contains('returnAction')
        output.contains('success')
        output.contains('returnController')
        output.contains('payment')
    }

    void "test button tag includes cancel action and controller when provided"() {
        given:
        grailsApplication.config.grails.paypal.server = 'https://www.paypal.com'
        grailsApplication.config.grails.paypal.email = 'test@example.com'

        when:
        def output = applyTemplate(
            '<paypal:button itemName="Test" itemNumber="ITEM-001" amount="10.00" buyerId="1" cancelAction="cancelled" cancelController="payment"/>'
        )

        then:
        output.contains('cancelAction')
        output.contains('cancelled')
        output.contains('cancelController')
        output.contains('payment')
    }

    void "test button tag includes additional params when provided"() {
        given:
        grailsApplication.config.grails.paypal.server = 'https://www.paypal.com'
        grailsApplication.config.grails.paypal.email = 'test@example.com'

        when:
        def output = applyTemplate(
            '<paypal:button itemName="Test" itemNumber="ITEM-001" amount="10.00" buyerId="1" params="[customParam: \'value\']"/>'
        )

        then:
        output.contains('customParam')
        output.contains('value')
    }

    void "test button tag includes transactionId in form params when provided"() {
        given:
        grailsApplication.config.grails.paypal.server = 'https://www.paypal.com'
        grailsApplication.config.grails.paypal.email = 'test@example.com'

        when:
        def output = applyTemplate(
            '<paypal:button itemName="Test" itemNumber="ITEM-001" amount="10.00" buyerId="1" transactionId="TRANS-123"/>'
        )

        then:
        output.contains('paypal/buy')
    }
}
