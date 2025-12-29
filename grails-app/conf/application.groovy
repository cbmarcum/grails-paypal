environments {
    production {
        grails.paypal.server = "https://ipnpb.paypal.com/cgi-bin/webscr"
        grails.paypal.email = "example@business.com"
        grails.serverURL = "http://www.grails.org"
    }
    development {
        grails.paypal.server = "https://ipnpb.sandbox.paypal.com/cgi-bin/webscr"
        grails.paypal.email = "seller_1237686842_biz@mattstine.com"
        grails.serverURL = "http://localhost:8080"
    }
}
