<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="include/include.jsp" %>
<html>
<head>
    <title>Title</title>
    <style>
        .layui-table-tips {
            white-space: pre;
        }
    </style>
</head>
<body>
<form action="" id="searchForm" class="layui-form" lay-filter="searchForm">
    <%--这是上面搜索条件的部分--%>
    <div class="layui-row soul-condition">
        <div class="layui-col-lg3 layui-col-md4 layui-col-sm6">
            <div class="layui-inline">
                <label class="layui-form-label">诗词</label>
                <div class="layui-input-inline" >
                    <input type="text" name="title"
                           class="layui-input" placeholder="请输入诗词关键字">
                </div>
            </div>
        </div>
        <div class="layui-col-lg3 layui-col-md4 layui-col-sm6">
            <div class="layui-inline">
                <label class="layui-form-label">内容</label>
                <div class="layui-input-inline">
                    <input type="text" name="content"
                           class="layui-input" placeholder="请选择内容关键词">
                </div>
            </div>
        </div>
        <div class="layui-col-lg3 layui-col-md4 layui-col-sm6">
            <div>
                <label class="layui-form-label">录入时间</label>
                <div class="layui-input-inline">
                    <input type="text" name="createTime" style="width: 180px;"
                           class="layui-input" placeholder="录入时间">
                </div>
            </div>
        </div>
        <div class="layui-col-lg1 layui-col-md1 layui-col-sm2 layui-col-xs3">
            <button class="layui-btn mgl-20" lay-submit="" lay-filter="search"><i class="layui-icon">&#xe615;</i>查询</button>
        </div>
        <div class="layui-col-lg1 layui-col-md1 layui-col-sm2 layui-col-xs3">
            <button class="layui-btn mgl-20" lay-submit="" lay-filter="export"><i class="layui-icon">&#xe615;</i>导出</button>
        </div>
    </div>
    <%--这边是下面表格的部分--%>
    <table id="myTable" lay-filter="myTable"></table>
    <script type="text/html" id="bar">
        <a class="layui-btn layui-btn-xs" lay-event="edit"><i class="layui-icon">&#xe642;</i> 修改</a>
        <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del"><i class="layui-icon">&#xe640;</i> 删除</a>
    </script>
</form>
</body>
<script>
    // layui文档 : http://www.layui.com/doc/
    layui.use(['form', 'soulTable', 'table', 'laydate'], function () {
        var form = layui.form,
            table = layui.table,
            laydate = layui.laydate,
            soulTable = layui.soulTable;


        laydate.render({
            elem: '[name=createTime]',
            range: true
        });

        /* 渲染主表格*/
        var myTable = table.render({
            id: 'myTable',
            elem: '#myTable',
            url: '${path}/poetry/dataGrid',
            method: 'post',
            height: $(document).height() - $('#myTable').offset().top - 50,
            sort: 'back',
            cols: [[
                {type: 'checkbox', fixed: 'left'},
                {field: 'id', title: '序号', width: 100, sort: true, filter: true},
                {field: 'title', title: '诗词', width: 200, sort: true, filter: true},
                {field: 'dynasty', title: '朝代', width: 100, sort: true, filter: true},
				{field: 'author', title: '作者', width: 165 , filter: true},
				{field: 'content', title: '内容', width: 123, filter: true},
				{field: 'type', title: '类型', width: 112,  filter: true, sort:true},
				{field: 'heat', title: '点赞数', width: 112,  filter: true, sort:true},
				{field: 'createTime', title: '录入时间', width: 165, filter: {type: 'date[yyyy-MM-dd HH:mm:ss]'}, sort:true},
                {title: '操作', width: 156, templet: '#bar',fixed: 'right'}
            ]]
			, filter: {
				items:['column','data','condition','editCondition','excel'] //用于控制表头下拉显示，可以控制顺序、显示
			}
            , excel:{ // 导出excel配置, （以下值均为默认值）
            	on: true, //是否启用, 默认开启
            	filename: '诗词.xlsx', // 文件名
                head:{ // 表头样式
            		family: 'Calibri', // 字体
                    size: 12, // 字号
            		color: '000000', // 字体颜色
                    bgColor: 'C7C7C7' // 背景颜色
				},
                font: { // 正文样式
					family: 'Calibri', // 字体
                    size: 12, // 字号
                    color: '000000', // 字体颜色
                    bgColor: 'ffffff' //背景颜色
				}
			}
            // , where: $('#searchForm').serializeObject()
            , page: true
            , limits: [20, 30, 50, 100, 200]
            , limit: 20 //默认采用20
            , done: function (res, curr, count) {
                // 如果有使用到下拉筛选，这句话必须要
                soulTable.render(this)
            }
        });

        // 查询
        form.on('submit(search)', function (data) {

        	if (data.field.createTime) {
				data.field.startTime = data.field.createTime.split(' - ')[0];
				data.field.endTime = data.field.createTime.split(' - ')[1];
            }
			delete data.field.createTime

            myTable.reload({  // 重载 table
                where: data.field
            });
            return false;
        });

        // 导出
        form.on('submit(export)', function (data) {
        	soulTable.export(myTable);
        	return false;
		})

        // 修改和删除
        table.on('tool(myTable)', function(obj) {
            var data = obj.data; // 获得当前行数据
            if (obj.event === 'del') {
                layer.open({
                    title: '删除',
                    content: JSON.stringify(data)
                })
            } else if (obj.event === 'edit') {
                layer.open({
                    title: '修改',
                    content: JSON.stringify(data)
                })
            }
        });

        // 获取选中数据
        $('[data-type=getCheckData]').on('click', function () {
            var checkStatus = table.checkStatus('myTable')
                ,data = checkStatus.data; //选中行数据
            layer.alert(JSON.stringify(data), {title: '勾选数量: '+checkStatus.data.length} );
            return false;
        })

    });

</script>
</html>
