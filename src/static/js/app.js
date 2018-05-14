//var api = "http://xycs.wenxikeji.com/"
var api = "http://localhost:8080/"

function AntiSqlValid(oField) {
	var sql = /select|update|delete|exec|count|'|"|=|;|>|<|%/i;
	if(oField.length == 0) {
		return false;
	}

	if(sql.test(oField)) {
		//alert("请您不要输入特殊字符"); //注意中文乱码
		return false;
	}

	return true;
}

function isString(str) {
	if(str != null && str != undefined && str != "" && str.length > 0 && str != "null") {
		return true;
	} else {
		return false;
	}
}

function isNum(str) {
	var myreg = /^\d+$/;
	if(!myreg.test(str)) {
		return false;
	} else {
		return true;
	}
}

function isDouble(str) {
	var myreg = /^\d+\.\d+$/;
	if(!myreg.test(str)) {
		if(isNum(str)) {
			return true;
		} else {
			return false;
		}
	} else {
		return true;
	}
}

function isPhone(str) {
	var myreg = /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\d{8})$/;
	if(!myreg.test(str)) {
		return false;
	} else {
		return true;
	}
}

function lengthChar(str) {
	if(!isString(str)) return 0;
	str = str.replace(/[^\u0000-\u00ff]/g, "aa");
	return str.length;
}

function formatDate(now) {
	var year = now.getYear() - 100;
	var month = now.getMonth() + 1;
	var date = now.getDate();
	var hour = now.getHours();
	var minute = now.getMinutes();
	var second = now.getSeconds();
	return "20" + year + "年" + month + "月" + date + "日 " + hour + ":" + minute + ":" + second;
}

/**
 * 获取url的指定参数
 * @param {Object} name
 */
function getQueryString(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
	var r = window.location.search.substr(1).match(reg);
	if(r != null) return unescape(r[2]);
	return null;
}

