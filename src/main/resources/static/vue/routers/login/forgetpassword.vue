<template>
    <Card dis-hover :bordered="false" style="text-align: center; background-color: #eee; background-color:rgba(255, 255, 255, 1); width: 450px; height: 330px;">
                <h2 style="text-align: center">找回密码</h2>
            <div v-if="!pending" style="margin-top: 1px;">
                
                <!--验证码 -->
                <VerifyCode @on-code="inputImageCode" />

                <!--短信验证码 -->
                <SendTextMessage 
                    @on-invalid-number="handleInvalidNumber" 
                    @on-send-msg="sendMessage" 
                    @on-sent="sendMessageSuccess" 
                    @on-send-failed="sendMessageFailed" 
                    @on-input-msg-code="inputMessageCode"
                    :verify-code="imageCode"
                     />

                <div style="margin-top: 35px;">
                    <Button  @click="findpassword" style="width: 300px;" type="primary" :loading="pending">找回密码</Button>
                    <br/>
                    <br/>
                    <a class="reg" href="javascript:void(0)" @click="show('LoginFrame')">&lt; 返回用户登录</a>
                </div>
            </div>

            <div v-else style="margin-top: 90px;">
                正在验证
            </div>
    </Card>
</template>

<script>
// 事件
//   on-request-change-com (name)  - 请求转换组件时触发

import SendTextMessage from '../../components/sendtextmsg.vue';
import VerifyCode from '../../components/verifycode.vue';
import $ from '../../../js/ajax.js';
import util from '../../../js/util.js';

export default {
    components: {
        SendTextMessage, VerifyCode
    },
    data: () => ({
        username: '',
        password: '',
        code: '',

        pending: false,
        param: {
            phone: '',
            msgcode: '',
        },
        
        sent: false,
        pending: false,
        imageCode: '',
    }),
    methods: {
        show(name) {
            this.$emit('on-request-change-com', name);
        },
        sendMessageFailed(err) {
            util.MessageBox.Show(this, '发送短信失败。');
        },
        sendMessageSuccess(result) {
            this.sent = true;
            //alert('发送短信成功');
        },
        sendMessage(phone) {
            this.param.phone = phone;
            console.log('phone number: ' + phone);
        },
        handleInvalidNumber(phone) {
            if (phone.length === 0)
                return util.MessageBox.Show(this, '请输入手机号码');
            util.MessageBox.Show(this, `${phone} 不是一个正确的手机号码。`);
        },
        // 从组件获取短信验证码
        inputMessageCode(val) {
            this.param.msgcode = val;
            console.log(this.param.msgcode);
        },
        // 从组件获取图片验证码
        inputImageCode(val) {
            this.imageCode = val;
            console.log(this.imageCode);
        },

        // 找回密码
        async findpassword() {
            if (this.pending)
                return;

            if (this.sent === false || this.param.phone === '' || this.param.msgcode === '') {
                util.MessageBox.Show(this,  '请输入短信验证码。');
                return;
            }

            this.pending = true;

            try {
                let result = await $.ajax('/api/forget', this.param);
                if (result.code === 0 && typeof result.data !== "undefined")
                    location.href = "/reset?" + util.forGetParams(result.data);
                else {
                    util.MessageBox.Show(this, result.msg?result.msg:'无法找回密码');
                    throw Error(result);
                }
            }
            catch (err) {
                this.pending = false;
            }
        }
    },
    watch: {

    }
}
</script>

<style>
.main {
    position: fixed;
    top: 0px;
    left: 0px;
    height: 100%;
    width: 100%;
}

.tp {
    background: rgba(255, 255, 255, 1);
    border-color: rgba(255, 255, 255, 1);
}

a.reg {
    margin-top: 35px;
    color: gray;
    font-size: 14px;
}
a.reg:hover {
    border-bottom: 1px solid gray;
}
</style>

