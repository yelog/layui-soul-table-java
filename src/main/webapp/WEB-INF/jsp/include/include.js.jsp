<%--标签 --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:set var="static_version" value="1.6.3.7" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<link rel="stylesheet" type="text/css" href="${staticPath}/layui/css/layui.css?v=${static_version}" media="all"/>
<link rel="stylesheet" type="text/css" href="${staticPath}/soul/animate.min.css?v=${static_version}" media="all"/>
<script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript" src="${staticPath}/layui/layui.js?v=${static_version}"></script>
<link rel="stylesheet" type="text/css" href="${staticPath}/soul/soulTable.css?v=${static_version}" />


<script>
    var basePath = '${ctxPath}',
        staticPath = '${staticPath}';

    // 自定义模块
    layui.config({
        base: staticPath+'/soul/modules/',   // 模块目录
        version : '${static_version}'
    }).extend({                         // 模块别名
		soulTable: 'soulTable'
    });


    // formdata 转 json
    $.fn.serializeObject = function()
    {
        var o = {};
        var a = this.serializeArray();
        $.each(a, function() {
            if (o[this.name]) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o;
    };

</script>