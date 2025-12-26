package org.grails.plugin.paypal

import grails.validation.Validateable


// This is a first version that only applies to the U.S. - Can anybody write an i18n-enabled version
// that Paypal can still understand?

class ShippingAddressCommand implements Validateable {
    String firstName
    String lastName
    String addressLineOne
    String addressLineTwo
    String city
    USState state
    String country = 'US'
    String zipCode
    String areaCode
    String phonePrefix
    String phoneSuffix

    static constraints = {
        firstName(blank: false)
        lastName(blank: false)
        addressLineOne(blank: false)
        addressLineTwo(nullable: true, blank: true)
        city(blank: false)
        country(blank: false)
        zipCode(blank: false, matches: /\d{5}/)
        areaCode(blank: false, matches: /\d{3}/)
        phonePrefix(blank: false, matches: /\d{3}/)
        phoneSuffix(blank: false, matches: /\d{4}/)
    }
}

