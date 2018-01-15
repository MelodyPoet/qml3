/*
SQLyog v10.2 
MySQL - 5.5.56-MariaDB : Database - server_list
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`server_list` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci */;

USE `server_list`;

/*Table structure for table `gift_code` */

DROP TABLE IF EXISTS `gift_code`;

CREATE TABLE `gift_code` (
  `guid` bigint(11) NOT NULL AUTO_INCREMENT,
  `giftCode` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`guid`),
  KEY `guid` (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `gift_code` */

/*Table structure for table `platform` */

DROP TABLE IF EXISTS `platform`;

CREATE TABLE `platform` (
  `guid` bigint(11) DEFAULT NULL,
  `platformName` varchar(25) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Data for the table `platform` */

/*Table structure for table `server_info` */

DROP TABLE IF EXISTS `server_info`;

CREATE TABLE `server_info` (
  `id` int(11) NOT NULL,
  `ser_name` varchar(16) DEFAULT NULL,
  `ser_ip` varchar(16) DEFAULT NULL,
  `ser_port` int(11) DEFAULT '9091',
  `status` varchar(11) DEFAULT '空闲',
  `statusTag` tinyint(4) DEFAULT '0',
  `newServer` tinyint(4) DEFAULT '1',
  `sql_cmd` varchar(128) DEFAULT NULL,
  `sql_ip` varchar(16) DEFAULT NULL,
  `sql_port` int(11) DEFAULT NULL,
  `sql_name` varchar(16) DEFAULT NULL,
  `sql_user` varchar(16) DEFAULT NULL,
  `sql_pwd` varchar(16) DEFAULT NULL,
  `sql_pool` int(11) DEFAULT '50',
  `openDay` date DEFAULT NULL,
  `merge_to` int(11) DEFAULT '0',
  `devCode` tinyint(1) DEFAULT '0',
  `serverID` int(11) NOT NULL,
  `platform` tinyint(4) DEFAULT NULL,
  `gameID` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `server_info` */

insert  into `server_info`(`id`,`ser_name`,`ser_ip`,`ser_port`,`status`,`statusTag`,`newServer`,`sql_cmd`,`sql_ip`,`sql_port`,`sql_name`,`sql_user`,`sql_pwd`,`sql_pool`,`openDay`,`merge_to`,`devCode`,`serverID`,`platform`,`gameID`) values (1,'欧阳伟良','192.168.9.37',9091,'空闲',0,1,'jdbc:mysql://192.168.9.37:3306/oywl?characterEncoding=utf-8','192.168.9.37',3306,'oywl','root','123456',50,'2017-01-01',0,0,1,1,1),(2,'内网开发服','192.168.9.112',9091,'空闲',0,1,'jdbc:mysql://192.168.9.112:3306/develop?characterEncoding=utf-8','192.168.9.112',3306,'develop','root','123456',50,'2017-01-01',0,0,2,1,1),(3,'不帅测试2','192.168.9.41',9091,'空闲',0,1,'jdbc:mysql://192.168.9.112:3306/jackie?characterEncoding=utf-8','192.168.9.112',3306,'jackie','root','123456',50,'2017-01-01',0,0,3,1,1);

/*Table structure for table `server_info_copy` */

DROP TABLE IF EXISTS `server_info_copy`;

CREATE TABLE `server_info_copy` (
  `id` int(11) NOT NULL,
  `ser_name` varchar(16) DEFAULT NULL,
  `url` varchar(64) DEFAULT NULL,
  `status` varchar(11) DEFAULT '空闲',
  `statusTag` tinyint(4) DEFAULT '0',
  `newServer` tinyint(4) DEFAULT '1',
  `sql_cmd` varchar(64) DEFAULT NULL,
  `sql_user` varchar(16) DEFAULT NULL,
  `sql_pwd` varchar(16) DEFAULT NULL,
  `sql_pool` int(11) DEFAULT '50',
  `openDay` date DEFAULT NULL,
  `merge_to` int(11) DEFAULT '0',
  `devCode` tinyint(1) DEFAULT '0',
  `ser_port` int(11) DEFAULT '9091',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `server_info_copy` */

/*Table structure for table `test_code` */

DROP TABLE IF EXISTS `test_code`;

CREATE TABLE `test_code` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `code` int(11) unsigned DEFAULT '0',
  `open` tinyint(1) unsigned DEFAULT '0',
  `info` varchar(11) DEFAULT NULL,
  `from` varchar(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

/*Data for the table `test_code` */

insert  into `test_code`(`id`,`code`,`open`,`info`,`from`) values (1,99999,1,'免验证','伟良');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
