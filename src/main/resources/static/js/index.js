import '../css/iview.miyukimodify.css';
import '../css/style.less';
import Vue from 'vue';
import MainPage from '../vue/mainpage.vue';
import iView from 'iview';
import VueRouter from 'vue-router';
import Routers from '../vue/routes';

Vue.use(iView);
Vue.use(VueRouter);

const RouterConfig = {
    routes: Routers
};

const router = new VueRouter(RouterConfig);

new Vue({
	el: '#app',
    render: h => h(MainPage),
    router: router
});