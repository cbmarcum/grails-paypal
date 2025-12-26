package org.grails.plugin.paypal

import grails.gorm.services.Service

@Service(BuyerInformation)
interface BuyerInformationService {

    BuyerInformation get(Serializable id)

    List<BuyerInformation> list(Map args)

    Long count()

    void delete(Serializable id)

    BuyerInformation save(BuyerInformation buyerInformation)

}