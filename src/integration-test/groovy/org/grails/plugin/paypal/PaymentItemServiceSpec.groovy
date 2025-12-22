package org.grails.plugin.paypal

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class PaymentItemServiceSpec extends Specification {

    PaymentItemService paymentItemService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new PaymentItem(...).save(flush: true, failOnError: true)
        //new PaymentItem(...).save(flush: true, failOnError: true)
        //PaymentItem paymentItem = new PaymentItem(...).save(flush: true, failOnError: true)
        //new PaymentItem(...).save(flush: true, failOnError: true)
        //new PaymentItem(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //paymentItem.id
    }

    void "test get"() {
        setupData()

        expect:
        paymentItemService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<PaymentItem> paymentItemList = paymentItemService.list(max: 2, offset: 2)

        then:
        paymentItemList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        paymentItemService.count() == 5
    }

    void "test delete"() {
        Long paymentItemId = setupData()

        expect:
        paymentItemService.count() == 5

        when:
        paymentItemService.delete(paymentItemId)
        sessionFactory.currentSession.flush()

        then:
        paymentItemService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        PaymentItem paymentItem = new PaymentItem()
        paymentItemService.save(paymentItem)

        then:
        paymentItem.id != null
    }
}
