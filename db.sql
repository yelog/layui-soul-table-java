DROP TABLE IF EXISTS `user_t`;  
  
-- auto-generated definition
create table user
(
  id         int auto_increment comment '自增id'
    primary key,
  name       varchar(50)  null comment '姓名',
  age        int          null comment '年龄',
  birthday   datetime     null comment '出生日期',
  country    varchar(50)  null comment '国籍',
  occupation varchar(50)  null comment '职业',
  remark     varchar(200) null comment '备注'
)
  comment '用户信息表';

/*Data for the table `user_t` */
