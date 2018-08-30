import Account from './routers/account.vue';
import Company from './routers/company.vue';
import Customer from './routers/customer.vue';
import Log from './routers/log.vue';
import Receipt from "./routers/receipt.vue";
import Tax from './routers/tax.vue';
import User from './routers/user.vue';

export default [
    { path: '/', component: Account },
    { path: '/account', component: Account },
    { path: '/company', component: Company },
    { path: '/customer', component: Customer },
    { path: '/log', component: Log },
    { path: '/receipt', component: Receipt },
    { path: '/tax', component: Tax },
    { path: '/user', component: User },
]