## layui-soul-table 后台java版
当前soul-table版本 `v1.0`, layui版本：`v2.4.5`

所有筛选demo：[https://yelog.org/layui-soul-table/](https://yelog.org/layui-soul-table/)
仅后台java版demo：[https://soultable.xiangzhangshugongyi.com/](https://soultable.xiangzhangshugongyi.com/)

当然你也可以下载此项目运行查看。

soulTable的前台使用方法跳转到[https://github.com/yelog/layui-soul-table](https://github.com/yelog/layui-soul-table) 查看，这里就不赘述了。

这里只讲如何使用后台筛选，喜欢的点个 start， 谢谢🙏

## 快速上手
### 1.环境基础
由于插件是基于 `mybatis` 的，所以项目要引用 `mybatis`, 由于引用 `mybatis` 不是本文重点，所以有需要自行网上查找，或参考本项目代码。

### 2.将插件引入项目
1）前端插件（js、css）的引入参考[soulTable](https://github.com/yelog/layui-soul-table)
2) 将文件 `FilterSo` `PageInterceptor` `ReflectHelper` `SoulPage` 复制到自己的项目中。（文件位置`src/main/java/org/yelog/soultable/util/`）

### 3.配置mybatis拦截器
在自己项目的 `sqlSessionFactory` 配置中引用插件 `PageInterceptor`， 
路径使用上一步自己放的位置，如下，dbType配置自己使用的数据库，目前支持：mysql、oracle
```xml
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">  
    <property name="dataSource" ref="dataSource" />  
    <!-- 自动扫描mapping.xml文件 -->  
    <property name="mapperLocations" value="classpath:mapper/*.xml"></property>
    <property name="plugins">
        <bean class="org.yelog.soultable.util.PageInterceptor"> 
            <property name="dbType" value="mysql" />
        </bean>
    </property>
</bean>
```
### 4.使用
1) controller 层
>注：poetrySo用于接收自定义的一些查询条件, soulPage的泛型也可使用Map：比如 `dataGrid(SoulPage<Map<String, Object>> soulPage, String name, String title)`
这里把查询条件塞到`soulPage.obj`中只是为了向service层传值方便。

```java
@RequestMapping("/poetry/dataGrid")
@ResponseBody
public Object dataGrid(SoulPage<Poetry> soulPage, PoetrySo poetrySo) {
    soulPage.setObj(poetrySo);
    return poetryService.dataGrid(soulPage);
}
```
2) service 层
```java
@Override
public Object dataGrid(SoulPage<Poetry> soulPage) {
    return soulPage.setData(poetryMapper.dataGrid(soulPage,(PoetrySo) soulPage.getObj()));
}
```
3）Dao 层
注：查询语句的第一参数必须是：`soulPage`
```java
    List<Poetry> dataGrid(SoulPage<Poetry> soulPage, @Param("so") PoetrySo poetrySo);
```

## 最后

当然具体的可以clone下来代码查看，有问题可以在 issue 区提问，我会尽可能快的回复。


