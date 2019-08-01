## layui-soul-table 后台java版
当前soul-table版本 `v1.0`, layui版本：`v2.4.5`

mybatis版: master分支  
mybatis-plus版: mybatis-plus分支  
hibernate版: hibernate分支  

所有筛选demo：[https://yelog.org/layui-soul-table/](https://yelog.org/layui-soul-table/)
仅后台java版demo：[https://soultable.xiangzhangshugongyi.com/](https://soultable.xiangzhangshugongyi.com/)

当然你也可以下载此项目运行查看。

soulTable的前台使用方法跳转到[https://github.com/yelog/layui-soul-table](https://github.com/yelog/layui-soul-table) 查看，这里就不赘述了。

这里只讲如何使用后台筛选，喜欢的点个 start， 谢谢🙏

## 快速上手 - hibernate
### 1.环境基础
由于插件是基于 `hibernate` 的，所以项目要引用 `hibernate`, 由于引用 `hibernate` 不是本文重点，所以有需要自行网上查找，或参考本项目代码。

### 2.将插件引入项目
1）前端插件（js、css）的引入参考[soulTable](https://github.com/yelog/layui-soul-table)
2) 将文件 `FilterSo` `SoulTableTool` `SoulPage` 复制到自己的项目中。（文件位置`src/main/java/org/yelog/soultable/util/`）

### 3.配置到 BaseDaoImpl 中通用使用
在 BaseDaoImpl 中 声明一个通用查询方法，比如： `soulDataGrid` ，配置进去 `session` 与 数据库类型 即可
```java
public Object soulDataGrid(SoulPage<T> soulPage, String sql, Map<String, Object> param) {
    return SoulTableTool.handle(getSession(), soulPage, sql, param, "MYSQL");
}
```
### 4.controller 层 接收参数
```java
/**
 * 表格数据请求
 *
 * @author yelog
 * @date 2019-08-01 17:03
 * @param soulPage 接收表头筛选条件、分页条件
 * @param poetry 接收定义的查询条件 （注意：没有自定义的表单条件，也建议写上，为了初始化 soulPage.obj ，用于反射获取 类信息）
 * @return java.lang.Object
 */
@RequestMapping("/poetry/dataGrid")
@ResponseBody
public Object dataGrid(SoulPage<Poetry> soulPage, Poetry poetry) {
    soulPage.setObj(poetry);
    return poetryService.dataGrid(soulPage);
}
```
### 4.server层就可以直接调用
```java
@Override
public Object dataGrid(SoulPage<Poetry> soulPage) {

    // 拼接自己的业务逻辑
    StringBuilder sql = new StringBuilder();
    sql.append("select * from poetry where 1=1");

    // 获取表单条件（自己定义的 form 表单数据，没有可以不写）
    Poetry poetry = soulPage.getObj();
    Map<String, Object> param = new HashMap<>();
    if (poetry != null) {
        if (StringUtils.isNotBlank(poetry.getTitle())) {
            sql.append(" and title like :title");
            param.put("title", "%" + poetry.getTitle() + "%");
        }
        if (StringUtils.isNotBlank(poetry.getContent())) {
            sql.append(" and content like :content");
            param.put("content", "%" + poetry.getContent() + "%");
        }
        if (StringUtils.isNotBlank(poetry.getStartTime()) && StringUtils.isNotBlank(poetry.getEndTime())) {
            sql.append(" and create_time between :startTime and :endTime");
            param.put("startTime", poetry.getStartTime());
            param.put("endTime", poetry.getEndTime());
        }
    }
    // 拼接完成自己的业务sql，调用工具方法返回
    return poetryDao.soulDataGrid(soulPage, sql.toString(), param);
}
```

## 最后

当然具体的可以clone下来代码查看，有问题可以在 issue 区提问，我会尽可能快的回复。


