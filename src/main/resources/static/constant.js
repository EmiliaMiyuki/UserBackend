/// 常量

// 行业列表
const industry = ["餐饮", "家政保洁/安保", "美容/美发", "酒店", "旅游", "娱乐/休闲", "保健按摩", "运动健身", "人事/行政/后勤", "司机", "高级管理", "销售", "客服", "贸易/采购", "超市/百货/零售", "淘宝职位", "房产中介", "市场/媒介/公关", "广告/会展/咨询", "美术/设计/创意", "普工/技工", "生产管理/研发", "物流/仓储", "服装/纺织/食品", "质控/安防", "汽车制造/服务", "计算机/互联网/通信", "电子/电气", "机械/仪器仪表", "法律", "教育培训", "翻译", "编辑/出版/印刷", "财务/审计/统计", "金融/银行/证券/投资", "保险", "医院/医疗/护理", "制药/生物工程", "环保", "建筑", "物业管理", "农/林/牧/渔业", "其他行业"];

// 会员类别
const memberType = ["企业会员", "个人会员"];

// 交易类型
const paymentMethod = ["在线-支付宝", "在线-微信", "在线-余额", "在线-转账", "对公-银行", "对公-现金", "对公-支付宝", "其他"];

// 对公订单状态
const publicOrderStatus = ["待确认","已确认","已取消","已拒绝"];

// 发票类型
const receiptType = ["增值税普通发票", "增值税专用发票"];

// 发票状态
const receiptStatus = ["已保存，待提交", "已提交，待审核", "已审核，待分配", "已分配（分配），待开票", "已分配（自取），待开票", "已开票，待打包", "已打包，待核对", "已核对，已寄送", "已签收，已完成", "已驳回，待提交", "已开票，已作废", "已驳回，待打包" ];

export {
    industry,
    memberType,
    paymentMethod,
    publicOrderStatus,
    receiptType,
    receiptStatus,
};