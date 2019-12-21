-- phpMyAdmin SQL Dump
-- version 4.0.4.1
-- http://www.phpmyadmin.net
--
-- Host: Localhost
-- Generation Time: Mar 13, 2014 at 10:35 PM
-- Server version: 5.6.11
-- PHP Version: 5.5.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `csi_mini_events_scorer_db`
--
CREATE DATABASE IF NOT EXISTS `csi_mini_events_scorer_db` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `csi_mini_events_scorer_db`;

DELIMITER $$
--
-- Procedures
--
DROP PROCEDURE IF EXISTS `create_view_if_not_exists_stored_proc`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `create_view_if_not_exists_stored_proc`(IN `team_id` INT)
    MODIFIES SQL DATA
if not exists(select TABLE_SCHEMA,TABLE_NAME from information_schema.tables where TABLE_TYPE like 'VIEW' and TABLE_NAME like concat('view_of_team', team_id)) then
		SET @ViewName = concat('view_of_team', cast(team_id as char(50)));
		SET @sql = concat('create view ', @ViewName, ' as select * from mini_events_score_log where team_id=', team_id);
		PREPARE stmt FROM @sql;
		EXECUTE stmt;
		DEALLOCATE PREPARE stmt;
end if$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `mini_events_score_log`
--
-- Creation: Feb 09, 2014 at 12:57 PM
--

DROP TABLE IF EXISTS `mini_events_score_log`;
CREATE TABLE IF NOT EXISTS `mini_events_score_log` (
  `team_id` int(11) NOT NULL,
  `mini_event_name` varchar(256) NOT NULL,
  `timestamp_of_entry` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mini_event_amt_won` double NOT NULL,
  `entry_by_council_member_name` varchar(256) NOT NULL,
  `entry_by_imei_num` varchar(256) NOT NULL,
  PRIMARY KEY (`team_id`,`mini_event_name`,`timestamp_of_entry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Entry of teams playing mini events and winning amt as per playing order';
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
