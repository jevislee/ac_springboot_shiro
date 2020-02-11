CREATE DATABASE `usermanage`;

USE `usermanage`;

CREATE TABLE `t_admin` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL COMMENT '用户昵称',
  `pswd` varchar(32) DEFAULT NULL COMMENT '密码',
  `status` int(1) DEFAULT '1' COMMENT '1:有效 2:冻结',
  `last_login_time` timestamp NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `delete_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert  into `t_admin`(`id`,`name`,`pswd`,`status`,`last_login_time`,`create_time`,`update_time`,`delete_time`) values (1,'admin','666666',1,NULL,'2019-11-01 15:48:28','2019-11-01 15:48:28',NULL);

CREATE TABLE `t_admin_role` (
  `id` int(11) NOT NULL,
  `admin_id` int(11) NOT NULL COMMENT '用户ID',
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`,`admin_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `t_api` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(50) DEFAULT NULL COMMENT '接口标题',
  `uri` varchar(200) DEFAULT NULL COMMENT '接口uri(只支持非RESTful接口)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `t_excluded_api` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uri` varchar(200) DEFAULT NULL COMMENT '排除在权限控制外的接口uri',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert  into `t_excluded_api`(`id`,`uri`) values (1,'/admin/login');
insert  into `t_excluded_api`(`id`,`uri`) values (2,'/unlogined');
insert  into `t_excluded_api`(`id`,`uri`) values (3,'/unauthorized');
insert  into `t_excluded_api`(`id`,`uri`) values (4,'/error');
insert  into `t_excluded_api`(`id`,`uri`) values (5,'/logout');
insert  into `t_excluded_api`(`id`,`uri`) values (6,'/mappingList');
insert  into `t_excluded_api`(`id`,`uri`) values (7,'/swagger-resources');
insert  into `t_excluded_api`(`id`,`uri`) values (8,'/swagger-resources/configuration/security');
insert  into `t_excluded_api`(`id`,`uri`) values (9,'/swagger-resources/configuration/ui');
insert  into `t_excluded_api`(`id`,`uri`) values (10,'/user/login');
insert  into `t_excluded_api`(`id`,`uri`) values (11,'/user/wxlogin');

CREATE TABLE `t_menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `path` varchar(200) DEFAULT NULL,
  `component` varchar(200) DEFAULT NULL,
  `redirect` varchar(200) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `title` varchar(50) DEFAULT NULL,
  `icon` varchar(200) DEFAULT NULL,
  `hidden` int(11) DEFAULT NULL,
  `parent_id` int(11) DEFAULT '0',
  `order_no` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert  into `t_menu`(`id`,`path`,`component`,`redirect`,`name`,`title`,`icon`,`hidden`,`parent_id`,`order_no`) values (1,'/privilege',     'Layout','/privilege/apiList','Privilege','权限管理','user',0,0,1);
insert  into `t_menu`(`id`,`path`,`component`,`redirect`,`name`,`title`,`icon`,`hidden`,`parent_id`,`order_no`) values (2,'apiList',        'privilege/apiList','','ApiList','API列表','',0,1,1);
insert  into `t_menu`(`id`,`path`,`component`,`redirect`,`name`,`title`,`icon`,`hidden`,`parent_id`,`order_no`) values (3,'excludedApiList','privilege/excludedApiList','','ExcludedApiList','API排除列表','',0,1,2);
insert  into `t_menu`(`id`,`path`,`component`,`redirect`,`name`,`title`,`icon`,`hidden`,`parent_id`,`order_no`) values (4,'roleList',       'privilege/roleList','','RoleList','角色列表','',0,1,3);
insert  into `t_menu`(`id`,`path`,`component`,`redirect`,`name`,`title`,`icon`,`hidden`,`parent_id`,`order_no`) values (5,'roleApiList',    'privilege/roleApiList','','RoleApiList','角色API列表','',1,1,4);
insert  into `t_menu`(`id`,`path`,`component`,`redirect`,`name`,`title`,`icon`,`hidden`,`parent_id`,`order_no`) values (6,'adminList',      'privilege/adminList','','AdminList','管理员列表','',0,1,5);
insert  into `t_menu`(`id`,`path`,`component`,`redirect`,`name`,`title`,`icon`,`hidden`,`parent_id`,`order_no`) values (7,'adminRoleList',  'privilege/adminRoleList','','AdminRoleList','管理员角色列表','',1,1,6);
insert  into `t_menu`(`id`,`path`,`component`,`redirect`,`name`,`title`,`icon`,`hidden`,`parent_id`,`order_no`) values (8,'menuList',       'privilege/menuList','','MenuList','菜单列表','',0,1,7);
insert  into `t_menu`(`id`,`path`,`component`,`redirect`,`name`,`title`,`icon`,`hidden`,`parent_id`,`order_no`) values (9,'roleMenuList',   'privilege/roleMenuList','','RoleMenuList','角色菜单列表','',1,1,8);

CREATE TABLE `t_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL COMMENT '角色名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert  into `t_role`(`id`,`name`) values (1,'app');

CREATE TABLE `t_role_api` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  `api_id` int(11) NOT NULL COMMENT 'API权限ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique` (`role_id`,`api_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `t_role_menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  `menu_id` int(11) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique` (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `t_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `wx_openid` varchar(100) DEFAULT NULL COMMENT '微信对应的openid',
  `nickname` varchar(20) DEFAULT NULL COMMENT '用户昵称',
  `name` varchar(40) DEFAULT NULL COMMENT '登录账号:手机号,邮箱',
  `mobile` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(40) DEFAULT NULL COMMENT '邮箱',
  `pswd` varchar(40) DEFAULT NULL COMMENT '密码',
  `sex` int(11) DEFAULT NULL COMMENT '1:男 2:女',
  `avatar_url` varchar(500) DEFAULT NULL COMMENT '头像',
  `status` int(11) DEFAULT '1' COMMENT '1:有效 2:冻结',
  `code` varchar(10) DEFAULT NULL COMMENT '短信验证码',
  `last_login_time` timestamp NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `delete_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
