package org.grails.plugin.paypal

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class BuyerInformationSpec extends Specification implements DomainUnitTest<BuyerInformation> {

    void "test BuyerInformation can be created with all nullable fields"() {
        when:
        def buyerInfo = new BuyerInformation()

        then:
        buyerInfo.validate()
    }

    void "test BuyerInformation can be saved with minimal data"() {
        when:
        def buyerInfo = new BuyerInformation()
        buyerInfo.save(flush: true)

        then:
        buyerInfo.id != null
    }

    void "test populateFromPaypal correctly maps paypal arguments"() {
        given:
        def buyerInfo = new BuyerInformation()
        def paypalArgs = [
            payer_id: 'PAYER123',
            first_name: 'John',
            last_name: 'Doe',
            payer_business_name: 'Acme Corp',
            address_name: 'Jane Doe',
            payer_email: 'john@example.com',
            address_street: '123 Main St',
            address_zip: '12345',
            address_city: 'Springfield',
            address_state: 'IL',
            address_country: 'United States',
            address_country_code: 'US',
            contact_phone: '555-1234',
            address_status: 'unconfirmed'
        ]

        when:
        buyerInfo.populateFromPaypal(paypalArgs)

        then:
        buyerInfo.uniqueCustomerId == 'PAYER123'
        buyerInfo.firstName == 'John'
        buyerInfo.lastName == 'Doe'
        buyerInfo.companyName == 'Acme Corp'
        buyerInfo.receiverName == 'Jane Doe'
        buyerInfo.email == 'john@example.com'
        buyerInfo.street == '123 Main St'
        buyerInfo.zip == '12345'
        buyerInfo.city == 'Springfield'
        buyerInfo.state == 'IL'
        buyerInfo.country == 'United States'
        buyerInfo.countryCode == 'US'
        buyerInfo.phoneNumber == '555-1234'
    }

    void "test populateFromPaypal sets addressConfirmed to true when status is confirmed"() {
        given:
        def buyerInfo = new BuyerInformation()
        def paypalArgs = [address_status: 'confirmed']

        when:
        buyerInfo.populateFromPaypal(paypalArgs)

        then:
        buyerInfo.addressConfirmed == true
    }

    void "test populateFromPaypal sets addressConfirmed to false when status is not confirmed"() {
        given:
        def buyerInfo = new BuyerInformation()
        def paypalArgs = [address_status: 'unconfirmed']

        when:
        buyerInfo.populateFromPaypal(paypalArgs)

        then:
        buyerInfo.addressConfirmed == false
    }

    void "test BuyerInformation validation allows all fields to be null or blank"() {
        when:
        def buyerInfo = new BuyerInformation(
            uniqueCustomerId: null,
            firstName: '',
            lastName: null,
            companyName: '',
            receiverName: null,
            email: '',
            street: null,
            zip: '',
            city: null,
            state: '',
            country: null,
            countryCode: '',
            phoneNumber: null
        )

        then:
        buyerInfo.validate()
    }

}
