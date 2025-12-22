package org.grails.plugin.paypal

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class BuyerInformationServiceSpec extends Specification {

    BuyerInformationService buyerInformationService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new BuyerInformation(...).save(flush: true, failOnError: true)
        //new BuyerInformation(...).save(flush: true, failOnError: true)
        //BuyerInformation buyerInformation = new BuyerInformation(...).save(flush: true, failOnError: true)
        //new BuyerInformation(...).save(flush: true, failOnError: true)
        //new BuyerInformation(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //buyerInformation.id
    }

    void "test get"() {
        setupData()

        expect:
        buyerInformationService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<BuyerInformation> buyerInformationList = buyerInformationService.list(max: 2, offset: 2)

        then:
        buyerInformationList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        buyerInformationService.count() == 5
    }

    void "test delete"() {
        Long buyerInformationId = setupData()

        expect:
        buyerInformationService.count() == 5

        when:
        buyerInformationService.delete(buyerInformationId)
        sessionFactory.currentSession.flush()

        then:
        buyerInformationService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        BuyerInformation buyerInformation = new BuyerInformation()
        buyerInformationService.save(buyerInformation)

        then:
        buyerInformation.id != null
    }
}
