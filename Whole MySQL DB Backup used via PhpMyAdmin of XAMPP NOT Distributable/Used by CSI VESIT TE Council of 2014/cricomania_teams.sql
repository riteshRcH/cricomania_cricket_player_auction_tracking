-- phpMyAdmin SQL Dump
-- version 3.5.2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Feb 12, 2014 at 06:30 PM
-- Server version: 5.5.25a
-- PHP Version: 5.4.4

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `cricomania`
--

-- --------------------------------------------------------

--
-- Table structure for table `cricomania_teams`
--

CREATE TABLE IF NOT EXISTS `cricomania_teams` (
  `team_id` int(11) NOT NULL,
  `team_balance` bigint(20) NOT NULL,
  `password_to_view_own_team_stats` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`team_id`),
  UNIQUE KEY `password_to_view_own_team_stats` (`password_to_view_own_team_stats`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Team details';

--
-- Dumping data for table `cricomania_teams`
--

INSERT INTO `cricomania_teams` (`team_id`, `team_balance`, `password_to_view_own_team_stats`) VALUES
(1, 450000000, 'me194'),
(2, 440000000, 'dkt24'),
(3, 430000000, 'uqpr4'),
(4, 420000000, 'eklfl'),
(5, 420000000, 'pvkn3'),
(6, 420000000, 'sm9tp'),
(7, 410000000, 's87mm'),
(8, 410000000, 'ge5nu'),
(9, 400000000, '18m5s'),
(10, 400000000, '6lhb8'),
(11, 400000000, 'f4oop'),
(12, 390000000, '6uqbd'),
(13, 380000000, 'n98r9'),
(14, 380000000, 'rs0ah'),
(15, 350000000, 'dbdav'),
(16, 350000000, 'ofrgs'),
(17, 200000000, '30147'),
(18, 200000000, 'rruc3'),
(19, 200000000, 'osq2e'),
(20, 200000000, '7s1o7');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
