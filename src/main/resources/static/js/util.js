import $ from './ajax.js';

function forPostParamsQueryString(obj) {
	if (typeof obj !== "object")
		throw new Error("obj is not an object");
	return encodeURIComponent(Object.keys(obj).map(e=>e + '=' + obj[e]).join('&'));
}

function forPostParams(obj) {
	if (typeof obj !== "object")
		throw new Error("obj is not an object");
	return Object.keys(obj).length < 3 ? 
		encodeURI(Object.keys(obj).map(e=>e + '=' + obj[e]).join('&')) :
		JSON.stringify(obj);
}

function forGetParams(obj) {
    if (typeof obj !== "object")
        throw new Error("obj is not an object");
    obj = removeEmptyAttributes(obj);
    return encodeURI(Object.keys(obj).map(e=>e + '=' + obj[e]).join('&'));
}

function forGetParamsN(obj) {
    if (typeof obj !== "object")
        throw new Error("obj is not an object");
    obj = removeEmptyAttributes(obj);
    return (Object.keys(obj).map(e=>e + '=' + obj[e]).join('&'));
}

function forGetParamsNR(obj) {
    if (typeof obj !== "object")
        throw new Error("obj is not an object");
    return (Object.keys(obj).map(e=>e + '=' + obj[e]).join('&'));
}

function forGetURL(url, obj) {
    return encodeURI(url+'?'+forGetParams(obj));
}

function getQueryParameter(param) {
    let query = window.location.search.substring(1);
	let vars = query.split("&");
	for (let i=0; i<vars.length; i++) {
		let pair = vars[i].split("=");
		if (pair[0] == param)
			return pair[1];
	}
    return null;
}

function removeEmptyAttributes(obj) {
    let keys = Object.keys(obj);
    let t = keys.filter(e=>obj[e]!=='');
    if (t.length == 0)
        return {};
    return Object.assign(... t.map(e=>({[e]: obj[e]})));
}

function isStringNullOrEmpty(str) {
	return !(typeof str === "string" &&
		   !( typeof str === "undefined" || str === null || str.length === 0 ));
}

const ASCII = {
	_0: '0'.charCodeAt(),
	_9: '9'.charCodeAt(),
	a: 'a'.charCodeAt(),
	z: 'z'.charCodeAt(),
	A: 'A'.charCodeAt(),
	Z: 'Z'.charCodeAt(),
}

function isLetter(ch) {	
	let c = ch[0].charCodeAt();
	return (c >= ASCII.a && c <= ASCII.z) || (c >= ASCII.A && c <= ASCII.Z); 
}

function toASCIICode(ch) {
	return ch[0].charCodeAt();
}

function isLowerCase(ch) {
	let c = ch[0].charCodeAt();
	return (c >= ASCII.a && c <= ASCII.z); 
}

function isUpperCase(ch) {
	let c = ch[0].charCodeAt();
	return (c >= ASCII.A && c <= ASCII.Z); 
}

function isNumber(ch) {
	let c = ch[0].charCodeAt();
	return (c >= ASCII._0 && c <= ASCII._9); 
}

function passwordMatchesRestriction(pwd) {
	if (isStringNullOrEmpty(pwd))
		return false;
	// 验证长度
	if (pwd.length < 8)
		return false;
	// 验证符号类型
	var ty = Array(4).fill(0);
	const LOWER_CASE = 0, UPPER_CASE = 1, NUMBER = 2, SIGNS = 3;
	for (let i in pwd) {
		let ch = pwd[i];
		if (isLowerCase(ch))
			ty[LOWER_CASE] = 1;
		else if (isUpperCase(ch))
			ty[UPPER_CASE] = 1;
		else if (isNumber(ch))
			ty[NUMBER] = 1;
		else ty[SIGNS] = 1;
	}
	return ty.reduce((a,b)=>a+b) >= 2;
}

/** 从input file获取文件内容 */
const ReaderFileResultType = {
	ARRAY_BUFFER: 'readAsArrayBuffer',
	BINARY_STRING: 'readAsBinaryString',
	DATA_UTL: 'readAsDataURL',
	TEXT: 'readAsText',
	BASE64: 'base64',
}

function getFileContentAsync(fileInput, resultIn = ReaderFileResultType.DATA_UTL) {
	const dataUrlToBase64 = dataURL => dataURL.replace(/^.*;base64,/, "");
	let reader = null;
	if (window.FileReader)
		reader = new FileReader();
	else throw new Error('FileReader unsupport');

	let file = fileInput.files[0];
	return new Promise((resolve, reject) => {
		reader.onload = e => {
			if (e.target.result.length<=1038336*5) {
				let ret = e.target.result;
				if (resultIn === ReaderFileResultType.BASE64) 
					ret = dataUrlToBase64(ret);
				resolve({
					name: file,
					data: ret,
					size: e.target.result.length,
				});
			}
			else {
				reject(new Error('文件大小超过限制'));
			}
		};
		let fn = resultIn === ReaderFileResultType.BASE64 ? 
						ReaderFileResultType.DATA_UTL : resultIn;
		reader[fn](file);
	});
}

/** Debug **/
function ralert(param) {
	const DEBUG_MODE = 1;
	if (DEBUG_MODE == 1)
		return;
	alert(param);
}

/** 状态转换请求函数 */
function requestNextStateHandler(thisState, nextState, stateMap, baseURL, postBody=undefined, success=undefined, fail=undefined) {
    let requestAction = stateMap[thisState][nextState];
    if (typeof requestAction === "undefined")
        throw new Error('[状态转换] 不存在状态' + thisState + '到' + nextState + '的转换');
    fail = fail || function(err) { alert("操作失败"); console.log(err); }
	success = success || function(data) { alert('操作成功'); console.log(data); }
	postBody = postBody || {r: Math.random()};
    return async function() {
		let requestURL = baseURL + requestAction;
        try {
			let result = $.ajax(requestAction, postBody);
			if (result.code == 0) success.bind(this)(result.data);
			fail.bind(this)(result);
        }
        catch(err) {
			fail.bind(this)(err);
        }
    }
}

// 根据状态机进行render
// from , to, render, params
function __SM_renderVDOM(f, t, h, p, self, url, map, an, success, failed) {
    let action = map[f][t];
    // 如果转台转换不存在
    if (typeof action == "undefined")
        return [];
    let DividerVDOM = h('span', {}, ' | ');
    let LinkVDOM = h('a', {
        props: { href: 'javascript:void(0)', },
        on: {
            async click() {
                if (!confirm('确认执行“' + an[action] + '”操作吗？'))
                    return;
                let id = self.d[p.index].id;
                try {
                    let r = await $.ajax(`${url}/${id}/${action}`, {id});
                    if (r.code)
                        return failed('操作失败, ' + r.msg);
                    //alert('操作成功');
					success(action, r);
                    self.refresh();
                }
                catch (err) {
                    console.log(err);
                    //failed(action, err);
                    failed('操作失败');
                }
            }
        }
    }, an[action])
    return [ LinkVDOM, DividerVDOM ];
}

// 根据当前状态进行render
function __SM_render(state, h, p, self, url, map, an, success, fail) {
	let to = map[state];
	if (!to) return [];
	let kt = Object.keys(to);
    let rA = []
    for (let t of kt)
        rA = [ ...rA, ...__SM_renderVDOM(state, t, h, p, self, url, map, an, success, fail) ];
    return rA;
}

function __Miyuki_MessageBoxBuilder(ty) {
	return function(vmctx, msg, title='提示') {
		vmctx.$Modal[ty]({
			title,
			content: msg,
		});
	}
}

function __Miyuki_MessageBoxAsyncBuilder(ty) {
	return function(vmctx, msg, title='提示') {
		return new Promise((resolve, reject) => {
			vmctx.$Modal[ty]({
				title,
				content: msg,
				onOk: resolve,
				onCancel: reject,
			});
		});
	}
}

function __Miyuki_MessageBoxFromComponentBuilder(ty) {
	return function(vmctx, com, title, props) {
		vmctx.$Modal[ty]({
			title,
			render: h=>h(com, {
				props
			})
		});
	}
}

function __Miyuki_MessageBoxFromComponentAsyncBuilder(ty) {
	return function(vmctx, com, title, props=({}), attrs=({})) {
		return new Promise((resolve, reject) => {
			vmctx.$Modal[ty](Object.assign({
				title,
				onOk: resolve,
				onCancel: reject,
				render: h=>h(com, props)
			}, attrs));
		})
	}
}

/** 
 * 深层好看的文件大小字符串
 */
function __Miyuki_friendlySize(x) {
	var classifier = "KMGTPE";
	var i = -1;
	do {
		if (i == -1)
			x = Math.ceil(x / 1024);
		else
			x = (x / 1024).toFixed(1);
		i++;
	}
	while (x>1024);
	if (i >= classifier.length)
		throw new Error("File size is too large to be made friendly");
	return String(x) + classifier[i] + "B";
}

/*
  日期转换的处理
 */
function toTimestamp(date) {
	return date.getTime();
}

function toDate(timestamp) {
	return new Date(timestamp);
}

function toDateSafe(timestamp) {
	let ts = Number(timestamp);
	if (isNaN(ts) || ts === 0)
		return timestamp;
	return new Date(ts);
}

function toDateString(date) {
    if (!(date instanceof Date))
        return date;

	let year = date.getFullYear();
	let month = date.getMonth() + 1;
	let day = date.getDate();
	return year + '-' + month + '-' + day;
}

function toMonthString(date) {
    if (!(date instanceof Date))
        return date;

    let year = date.getFullYear();
    let month = date.getMonth() + 1;
    return year + '-' + month;
}

function toYearString(date) {
    if (!(date instanceof Date))
        return date;

    let year = date.getFullYear();
    return year;
}

function toTimeString(date) {
	if (!(date instanceof Date))
		return date;

    let year = date.getFullYear();
    let month = date.getMonth() + 1;
    let day = date.getDate();

    let hour = date.getHours();  // 获取小时数(0-23)
    let minute = date.getMinutes();  // 获取分钟数(0-59)
    let second = date.getSeconds();  // 获取秒数(0-59)

    return year + '-' + month + '-' + day + ' ' + hour + ':' + String(minute).padStart(2, '0') + ':' + String(second).padStart(2, '0');
}

function toTimeStringFromTimestamp(timestamp) {
	return toTimeString(toDateSafe(timestamp));
}

/// Any
function Object_isNullOrUndefined(obj) {
	return obj === null || typeof obj === "undefined";
}

/// Network

function network_post(URL, PARAMS) {
	let temp_form = document.createElement("form");
	temp_form.action = URL;
	temp_form.target = "_blank";
	temp_form.method = "post";
	temp_form.style.display = "none";
	for (let x in PARAMS) {
		let opt = document.createElement("textarea"); 
		opt.name = x;
		opt.value = PARAMS[x];
		temp_form.appendChild(opt);
	} document.body.appendChild(temp);
	temp_form.submit();
}

/// Object驼峰

function convUnderlineToHamp(name) {
    return name.replace(/_(\w)/g, m=>m.toUpperCase()).replace(/_/g, '');
}

function convUnderlineToHampObject(obj) {
    let k = Object.keys(obj);
    let h = k.map(convUnderlineToHamp);
    return Object.assign({}, ...h.map((e,i)=>({[e]:obj[k[i]]})));
}

function convUnderlineToHampObjectArray(arr) {
	return arr.map(convUnderlineToHampObject);
}

export default {
    forGetParams, // 通过对象生成Get方法参数
	forPostParams,  // 通过对象生成Post方法参数
    forGetParamsN,
    forGetParamsNR,
	forPostParamsQueryString, // 通过对象生成Post方法参数(不采用JSON)
	forGetURL,    // 通过对象生成Get方法地址
	getQueryParameter, // 获取页面参数
	isStringNullOrEmpty, // 测试字符串是否为空
	passwordMatchesRestriction, // 判断密码是否符合复杂性要求

	isNullOrUndefined: Object_isNullOrUndefined,

	// 对象
	Objects: {
        convUnderlineToHampObject, // 将下划线的对象转为驼峰的对象
        convUnderlineToHampObjectArray,
        removeEmptyAttributes,
	},

	// 字符相关
	Character: {
		isUpperCase,
		isLowerCase,
		isNumber,
		toASCIICode,
	},

	// 文件
	File: {
		ReaderFileResultType, // 读文件方式
		getFileContentAsync,  // 读文件
		friendlySize: __Miyuki_friendlySize, // 用好看的方式显示文件大小
	},

	// 调试用
	Debug: {
		ralert, // 仅在release时alert
	},

	// 字符串处理
	String: {
		isNullOrEmpty: isStringNullOrEmpty,
		isVividPassword: passwordMatchesRestriction,
        convUnderlineToHamp, // 将下划线字符串转为驼峰字符串
	},

	// 状态机
	State: {
		requestNextStateHandler,
		render: __SM_render, // 根据状态机渲染DOM，务必bind this指针
	},

	// 提示框
	MessageBox: {
		Show: __Miyuki_MessageBoxBuilder('info'),
		Error: __Miyuki_MessageBoxBuilder('error'),
		Warning: __Miyuki_MessageBoxBuilder('warning'),
		Success: __Miyuki_MessageBoxBuilder('success'),
		ShowAsync: __Miyuki_MessageBoxAsyncBuilder('info'),
		ErrorAsync: __Miyuki_MessageBoxAsyncBuilder('error'),
		WarningAsync: __Miyuki_MessageBoxAsyncBuilder('warning'),
		SuccessAsync: __Miyuki_MessageBoxAsyncBuilder('success'),
		ComfirmAsync:  __Miyuki_MessageBoxAsyncBuilder('confirm'),
		ShowComponent: __Miyuki_MessageBoxFromComponentBuilder('info'),
		ShowComponentAsync: __Miyuki_MessageBoxFromComponentAsyncBuilder('confirm'),
	},

	// 日期
	Date: {
        toTimestamp,
        toDate,
        toDateSafe,
        toDateString,
        toMonthString,
        toYearString,
        toTimeString,
        toTimeStringFromTimestamp,
	},

	Network: {
        POSTInNewWindow: network_post,
	}
}