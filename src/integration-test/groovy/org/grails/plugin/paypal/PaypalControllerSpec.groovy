package org.grails.plugin.paypal

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

@Integration
@Rollback
class PaypalControllerSpec extends Specification  implements ControllerUnitTest<PaypalController> {

    def oldConfig

    PaymentService paymentService

    def setup() {
        controller.paymentService = paymentService
        oldConfig = grailsApplication.config
        grailsApplication.config.grails.paypal.server = "https://www.sandbox.paypal.com/cgi-bin/webscr"
        grailsApplication.config.grails.paypal.email = "test@g2one.com"
        controller.grailsApplication = grailsApplication
    }

    def cleanup() {
        grailsApplication.config = oldConfig
        controller.grailsApplication.config = oldConfig
    }

    void "test buy with an invalid payment" () {
        given: "an original url"
        params.originalURL = "/start/page"

        when: "buy action without item information"
        request.method = 'POST'
        controller.buy()

        then: "redirected to original url"
        response.redirectedUrl == "/start/page"
    }

    void "test buy with valid payment"() {
        given: "valid params for payment"
        params.originalURL = "/start/page"
        params.itemName = "iPod"
        params.itemNumber = "IP390483"
        params.buyerId = "10"
        params.amount = "200.00"
        params.baseUrl = 'http://myowndomain.com:8180/'

        when: "buy action with item information"
        request.method = 'POST'
        controller.buy()

        // https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_xclick&business=testpp_1211202427_biz@g2one.com&item_name=iPod&item_number=IP390483&amount=200.0&currency_code=USD&notify_url=http://localhost:8080/paypal/notify?buyerId=10&return=http://myowndomain.com:8180/paypal/success?buyerId=10&cancel_return=http://localhost:8080/paypal/cancel?buyerId=10

        then: "the redirected url is correct"
        def url = response.redirectedUrl
        println "\n${url}\n"
        assert url.startsWith("https://www.sandbox.paypal.com/cgi-bin/webscr")

        assert url.indexOf("cmd=_xclick") > -1
        assert url.indexOf("business=test@g2one.com") > -1
        assert url.indexOf("item_name=iPod") > -1
        assert url.indexOf("item_number=IP390483") > -1
        assert url.indexOf("amount=200.0") > -1
        assert url.indexOf("quantity=1") > -1
        assert url.indexOf("return=http%3A%2F%2Fmyowndomain.com%3A8180") > -1

        and: "a payment is created"
        Payment.count() == 1
        def payment = Payment.findByBuyerId(10)

        assert payment
        payment.buyerId == 10

        and: "the item is correct"
        payment.paymentItems[0].amount == 200.00
        payment.paymentItems[0].itemName == "iPod"
        payment.paymentItems[0].itemNumber == "IP390483"
        payment.paymentItems[0].quantity == 1

        assert payment.transactionId.startsWith("TRANS-10-")
    }

    void "test uploadCart with valid payment"() {
        given: "a payment with no shipping"
        def payment = new Payment()
        payment.buyerId = 10

        PaymentItem paymentItem = new PaymentItem()
        paymentItem.amount = 200.00
        paymentItem.itemName = "iPod"
        paymentItem.itemNumber = "IP390483"
        payment.addToPaymentItems(paymentItem)

        paymentItem = new PaymentItem()
        paymentItem.amount = 299.00
        paymentItem.itemName = "iPhone"
        paymentItem.itemNumber = "IP987123"
        payment.addToPaymentItems(paymentItem)

        payment.save(flush: true)
        def transactionId = payment.transactionId

        params.originalURL = "/start/page"
        params.transactionId = transactionId

        when: "uploadCart action"
        controller.uploadCart()

        // https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_xclick&business=testpp_1211202427_biz@g2one.com&item_name=iPod&item_number=IP390483&amount=200.0&currency_code=USD&notify_url=http://localhost:8080/paypal/notify?buyerId=10&return=http://localhost:8080/paypal/success?buyerId=10&cancel_return=http://localhost:8080/paypal/cancel?buyerId=10

        then: "the redirected url is correct"
        def url = response.redirectedUrl

        assert url.startsWith("https://www.sandbox.paypal.com/cgi-bin/webscr")

        assert url.indexOf("cmd=_cart&upload=1&") > -1
        assert url.indexOf("business=test@g2one.com") > -1
        assert url.indexOf("item_name_1=iPod") > -1
        assert url.indexOf("item_number_1=IP390483") > -1
        assert url.indexOf("amount_1=200.0") > -1
        assert url.indexOf("quantity_1=1") > -1
        assert url.indexOf("item_name_2=iPhone") > -1
        assert url.indexOf("item_number_2=IP987123") > -1
        assert url.indexOf("amount_2=299.0") > -1
        assert url.indexOf("quantity_2=1") > -1

        and: "a payment is created"
        Payment.count() == 1
        def paymentInstance = Payment.findByBuyerId(10)

        assert paymentInstance
        paymentInstance.buyerId == 10

        and: "item 1 is correct"
        paymentInstance.paymentItems[0].amount == 200.00
        paymentInstance.paymentItems[0].itemName == "iPod"
        paymentInstance.paymentItems[0].itemNumber == "IP390483"
        paymentInstance.paymentItems[0].quantity == 1

        and: "item 2 is correct"
        paymentInstance.paymentItems[1].amount == 299.00
        paymentInstance.paymentItems[1].itemName == "iPhone"
        paymentInstance.paymentItems[1].itemNumber == "IP987123"
        paymentInstance.paymentItems[1].quantity == 1

        and: "the transaction prefix is correct"
        assert paymentInstance.transactionId.startsWith("TRANS-10-")
    }

    void "test uploadCart with no shipping"() {
        given: "a payment with no shipping"
        def payment = new Payment()
        payment.buyerId = 10

        PaymentItem paymentItem = new PaymentItem()
        paymentItem.amount = 200.00
        paymentItem.itemName = "iPod"
        paymentItem.itemNumber = "IP390483"
        payment.addToPaymentItems(paymentItem)

        paymentItem = new PaymentItem()
        paymentItem.amount = 299.00
        paymentItem.itemName = "iPhone"
        paymentItem.itemNumber = "IP987123"
        payment.addToPaymentItems(paymentItem)

        payment.save(flush: true)
        def transactionId = payment.transactionId

        params.originalURL = "/start/page"
        params.transactionId = transactionId
        params.noShipping = 'true'

        when: "uploadCart action"
        controller.uploadCart()

        // https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_xclick&business=testpp_1211202427_biz@g2one.com&item_name=iPod&item_number=IP390483&amount=200.0&currency_code=USD&notify_url=http://localhost:8080/paypal/notify?buyerId=10&return=http://localhost:8080/paypal/success?buyerId=10&cancel_return=http://localhost:8080/paypal/cancel?buyerId=10

        then: "the redirected url is correct"
        def url = response.redirectedUrl

        assert url.startsWith("https://www.sandbox.paypal.com/cgi-bin/webscr")

        assert url.indexOf("cmd=_cart&upload=1&") > -1
        assert url.indexOf("business=test@g2one.com") > -1
        assert url.indexOf("no_shipping=1") > -1
        assert url.indexOf("item_name_1=iPod") > -1
        assert url.indexOf("item_number_1=IP390483") > -1
        assert url.indexOf("amount_1=200.0") > -1
        assert url.indexOf("quantity_1=1") > -1
        assert url.indexOf("item_name_2=iPhone") > -1
        assert url.indexOf("item_number_2=IP987123") > -1
        assert url.indexOf("amount_2=299.0") > -1
        assert url.indexOf("quantity_2=1") > -1
        assert url.indexOf("localhost%3A8080") > -1

        and: "a payment is created"
        Payment.count() == 1
        def paymentInstance = Payment.findByBuyerId(10)

        assert paymentInstance
        paymentInstance.buyerId == 10

        and: "item 1 is correct"
        paymentInstance.paymentItems[0].amount == 200.00
        paymentInstance.paymentItems[0].itemName == "iPod"
        paymentInstance.paymentItems[0].itemNumber == "IP390483"
        paymentInstance.paymentItems[0].quantity == 1

        and: "item 2 is correct"
        paymentInstance.paymentItems[1].amount == 299.00
        paymentInstance.paymentItems[1].itemName == "iPhone"
        paymentInstance.paymentItems[1].itemNumber == "IP987123"
        paymentInstance.paymentItems[1].quantity == 1

        and: "the transaction prefix is correct"
        assert paymentInstance.transactionId.startsWith("TRANS-10-")
    }



    void "test uploadCart with a shipping address"() {
        given: "a payment with a shipping address"
        def payment = new Payment()
        payment.buyerId = 10

        PaymentItem paymentItem = new PaymentItem()
        paymentItem.amount = 200.00
        paymentItem.itemName = "iPod"
        paymentItem.itemNumber = "IP390483"
        payment.addToPaymentItems(paymentItem)

        paymentItem = new PaymentItem()
        paymentItem.amount = 299.00
        paymentItem.itemName = "iPhone"
        paymentItem.itemNumber = "IP987123"
        payment.addToPaymentItems(paymentItem)

        payment.save(flush: true)
        def transactionId = payment.transactionId

        params.originalURL = "/start/page"
        params.transactionId = transactionId
        params.addressOverride = 'true'
        params.firstName = 'Matt'
        params.lastName = 'Stine'
        params.addressLineOne = 'Memphis Java User Group'
        params.addressLineTwo = '160 Shadyac Avenue'
        params.city = 'Memphis'
        params.state = 'TN'
        params.zipCode = '38105'
        params.areaCode = '901'
        params.phonePrefix = '493'
        params.phoneSuffix = '5546'

        when: "uploadCart action"
        controller.uploadCart()

        //	https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_xclick&business=testpp_1211202427_biz@g2one.com&item_name=iPod&item_number=IP390483&amount=200.0&currency_code=USD&notify_url=http://localhost:8080/paypal/notify?buyerId=10&return=http://localhost:8080/paypal/success?buyerId=10&cancel_return=http://localhost:8080/paypal/cancel?buyerId=10

        then: "the redirected url is correct"
        def url = response.getRedirectedUrl()

        assert url.startsWith("https://www.sandbox.paypal.com/cgi-bin/webscr")

        assert url.indexOf("cmd=_cart&upload=1&") > -1
        assert url.indexOf("business=test@g2one.com") > -1
        assert url.indexOf("item_name_1=iPod") > -1
        assert url.indexOf("item_number_1=IP390483") > -1
        assert url.indexOf("amount_1=200.0") > -1
        assert url.indexOf("quantity_1=1") > -1
        assert url.indexOf("item_name_2=iPhone") > -1
        assert url.indexOf("item_number_2=IP987123") > -1
        assert url.indexOf("amount_2=299.0") > -1
        assert url.indexOf("quantity_2=1") > -1

        assert url.indexOf("address_override=1&") > -1
        assert url.indexOf("first_name=Matt") > -1
        assert url.indexOf("last_name=Stine") > -1
        assert url.indexOf("address1=Memphis Java User Group") > -1
        assert url.indexOf("address2=160 Shadyac Avenue") > -1
        assert url.indexOf("city=Memphis") > -1
        assert url.indexOf("country=US") > -1
        assert url.indexOf("night_phone_a=901") > -1
        assert url.indexOf("night_phone_b=493") > -1
        assert url.indexOf("night_phone_c=5546") > -1
        assert url.indexOf("state=TN") > -1
        assert url.indexOf("zip=38105") > -1

        and: "a payment is created"
        Payment.count() == 1
        def paymentInstance = Payment.findByBuyerId(10)

        assert paymentInstance
        paymentInstance.buyerId == 10

        and: "item 1 is correct"
        paymentInstance.paymentItems[0].amount == 200.00
        paymentInstance.paymentItems[0].itemName == "iPod"
        paymentInstance.paymentItems[0].itemNumber == "IP390483"
        paymentInstance.paymentItems[0].quantity == 1

        and: "the transaction prefix is correct"
        assert paymentInstance.transactionId.startsWith("TRANS-10-")
    }

}
