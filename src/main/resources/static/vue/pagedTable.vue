<template>
    <div>
        <Table border 
            :current="page" 
            :columns="columns_t" 
            :data="tableData"
            :height="height"
            :loading="loading"
        >
        </Table>        
        <Page 
            :total="count" 
            show-sizer 
            show-elevator 
            @on-change="handlePageChange"
            @on-page-size-change="handleSizeChange"
            style="margin-top: 10px; "
             />
    </div>
</template>

<script>

// 带有分页和数据显示，数据操作的Table
//TODO:  所有的请求均以特定的形式得到请求内容
/*
props:
    - columns: 和iview column name同（为了在render中可访问数据,需要传入函数，在data里面调用）、
    - data-source: 数据来源的ajax请求
      - url?count              获取数量
      - url?page=1&size=10     获取第1页的内容
      - url?page=1             获取第1页的内容
    - external: 外部控制的值和回调
    - height: 高度
    - additional-params 额外查询参数
    
    * pageChange回调和onPageSizeChange回调由组件实现
     
    事件:  pagechanged  页面改变时产生
           sizechanged  大小改变时产生
           onajaxerror  获取ajax内容发生错误时候调用     
           onrecvdata   获取到数据产生

    例如： <PagedTable data-source="admin" :columns="dataColumns" />
*/

import $ from '../js/ajax.js';
import util from '../js/util.js';

export default (function(){
    return ({
        props: [ 'columns', 'dataSource', 'additionalParams', 'height' ],
        data() {
            /// 计算
            let columns_t = null;
            if (typeof this.columns !== "function") {
                columns_t = this.columns;
                console.warn("[PagedTable WARN] 传入数组而非函数可能无法泽确使用render函数");
            }
            else {
                // 在此处bind该组件的this指针，以便于可以拿到组件内data的值。
                columns_t = this.columns.bind(this)();
            }

            return ({
                tableData: [],
                count: 0,
                size: 10,
                page: 1,
                columns_t,
                loading: true,
            }); 
        },
        created() {
            //Data = this.tableData;
        },
        methods: {
            handlePageChange(p) {
                this.page = p;
                this.getContentOfPage(p, this.size);
                this.$emit('pagechanged', p);
            },
            handleSizeChange(s) {
                this.size = s;
                this.getContentOfPage(this.page, s);
                this.$emit('sizechanged', s);
            },
            async getContentOfPage(page, size=10) {
                this.loading = true;
                let add_param = util.isStringNullOrEmpty(this.additionalParams)?'':'&'+this.additionalParams;
                let url = this.requestUrl + `?page=${page}&size=${size}${add_param}`;
                try {
                    let result = await $.ajax(url);
                    if (result.code != 0) {
                        throw new Error(result);
                    }
                    result.data = util.Objects.convUnderlineToHampObjectArray(result.data);
                    this.tableData = result.data;
                    this.getCount();
                    this.$emit("onrecvdata", result.data);
                    console.log(`[PagedTable] 获取数据成功: `, url);
                    this.loading = false;
                }
                catch(err) {
                    console.error(`[PagedTable] 获取数据失败: `, url);
                    this.$emit('onajaxerror', err);
                    this.loading = false;
                }
            },
            async getCount() {
                let add_param = util.isStringNullOrEmpty(this.additionalParams)?'':'&'+this.additionalParams;
                let url = this.requestUrl + `?count=0` + add_param;
                try {
                    let r = await $.ajax(url);
                    if (r.code) {
                        console.error(`[PagedTable] 获取记录数量失败`);
                    }
                    this.count = r.data;
                    console.log(`[PagedTable] 获取记录数量: ${this.count}条记录`);
                }
                catch(err) {
                    console.error(`[PagedTable] 获取记录数量失败：`, url);
                    this.$emit('onajaxerror', err);
                }
            },
            refresh() {
                this.getContentOfPage(this.page, this.size);
            },
            // 根据param获取第param.index个元素
            p(param) {
                return this.d[param];
            }
        },
        computed: {
            requestUrl() {
                return '/api/' + this.dataSource;
            },
            d() { return this.tableData; }
        },
        watch: {
            dataSource(val) {
                console.log('[PagedTable] 数据源发生变化', val);
                this.tableData = [];
                this.page = 1;
                this.getContentOfPage(1, this.size);
            },
            additionalParams(val) {
                console.log('[PagedTable] 数据查询参数发生变化', val);
                this.tableData = [];
                this.page = 1;
                this.getContentOfPage(1, this.size);
            }
        },
        mounted() {
            this.getContentOfPage(1, 10);
        },
    });
})();

</script>