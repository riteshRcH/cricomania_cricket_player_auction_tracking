-- phpMyAdmin SQL Dump
-- version 3.5.2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Feb 15, 2014 at 04:27 AM
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
--
-- Table structure for table `cricomania_current_pool_player`
--

CREATE TABLE IF NOT EXISTS `cricomania_current_pool_player` (
  `pool_id` int(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  PRIMARY KEY (`pool_id`,`player_id`),
  KEY `player_id` (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Current pool and player in auction';

--
-- Dumping data for table `cricomania_current_pool_player`
--

INSERT INTO `cricomania_current_pool_player` (`pool_id`, `player_id`) VALUES
(7, 93);

-- --------------------------------------------------------

--
-- Table structure for table `cricomania_players`
--

CREATE TABLE IF NOT EXISTS `cricomania_players` (
  `player_id` int(11) NOT NULL,
  `pool_id` int(11) NOT NULL,
  `player_name` varchar(256) NOT NULL,
  `age` int(11) NOT NULL,
  `country` varchar(256) DEFAULT NULL,
  `type_of_player` varchar(256) DEFAULT NULL,
  `is_available` varchar(3) NOT NULL DEFAULT 'yes',
  `is_sold` varchar(3) NOT NULL DEFAULT 'no',
  `photo_file_path` varchar(512) DEFAULT NULL,
  `base_price` double DEFAULT NULL,
  `brand_value` int(11) DEFAULT NULL,
  PRIMARY KEY (`player_id`),
  KEY `fk_to_pool_id` (`pool_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Players general details';

--
-- Dumping data for table `cricomania_players`
--

INSERT INTO `cricomania_players` (`player_id`, `pool_id`, `player_name`, `age`, `country`, `type_of_player`, `is_available`, `is_sold`, `photo_file_path`, `base_price`, `brand_value`) VALUES
(1, 1, 'Vijay Zol', 19, 'India', 'Left Handed Batsman', 'yes', 'no', NULL, 35000000, 45),
(2, 1, 'Kedar Jadhav', 28, 'India', 'Right Handed Batsman', 'yes', 'no', NULL, 30000000, 35),
(3, 1, 'Mayank Agarwal', 22, 'India', 'Right Handed Batsman', 'yes', 'no', NULL, 25000000, 25),
(4, 1, 'George Bailey', 31, 'Australia', 'Right Handed Batsman', 'yes', 'no', NULL, 40000000, 40),
(5, 1, 'Cheteshwar Pujara', 26, 'India', 'Right Handed Batsman', 'yes', 'no', NULL, 40000000, 35),
(6, 1, 'Robin Uthappa', 28, 'India', 'Right Handed Batsman', 'yes', 'no', NULL, 35000000, 30),
(7, 1, 'Manoj Tiwary', 28, 'India', 'Right Handed Batsman', 'yes', 'no', NULL, 30000000, 25),
(8, 1, 'Saurabh Tiwary', 24, 'India', 'Left Handed Batsman', 'yes', 'no', NULL, 30000000, 25),
(9, 1, 'Aaron Finch', 27, 'Australia', 'Right Handed Batsman', 'yes', 'no', NULL, 45000000, 45),
(10, 1, 'Brad Hodge', 39, 'Australia', 'Right Handed Batsman', 'yes', 'no', NULL, 50000000, 40),
(11, 1, 'Manish Pandey', 24, 'India', 'Right Handed Batsman', 'yes', 'no', NULL, 35000000, 30),
(12, 1, 'Unmukt Chand', 20, 'India', 'Right Handed Batsman', 'yes', 'no', NULL, 40000000, 30),
(13, 1, 'Ryan Ten Doeschate', 33, 'Netherlands', 'Right Handed Batsman', 'yes', 'no', NULL, 30000000, 40),
(14, 1, 'J P Duminy', 29, 'South Africa', 'Left Handed Batsman', 'yes', 'no', NULL, 50000000, 40),
(15, 1, 'Dwayne Smith', 30, 'West Indies', 'Left Handed Batsman', 'yes', 'no', NULL, 35000000, 45),
(16, 1, 'Murli Vijay', 29, 'India', 'Right Handed Batsman', 'yes', 'no', NULL, 50000000, 25),
(17, 1, 'Shaun Marsh', 30, 'Australia', 'Left Handed Batsman', 'yes', 'no', NULL, 45000000, 50),
(18, 1, 'Faf du Plasis', 29, 'South Africa', 'Right Handed Batsman', 'yes', 'no', NULL, 50000000, 50),
(19, 2, 'Lakshmipathy Balaji', 32, 'India', 'Right Handed Bowler', 'yes', 'no', NULL, 30000000, 25),
(20, 2, 'Morne Morkel', 29, 'South Africa', 'Right Handed Bowler', 'yes', 'no', NULL, 45000000, 45),
(21, 2, 'Josh Hazelwood', 23, 'Australia', 'Right Handed Batsman', 'yes', 'no', NULL, 25000000, 35),
(22, 2, 'R Vinay Kumar', 30, 'India', 'Right Handed Bowler', 'yes', 'no', NULL, 35000000, 30),
(23, 2, 'Umesh Yadav', 26, 'India', 'Right Handed Bowler', 'yes', 'no', NULL, 40000000, 30),
(24, 2, 'Jaidev Unadkat', 22, 'India', 'Left Handed Bowler', 'yes', 'no', NULL, 25000000, 25),
(25, 2, 'Mohammed Shami', 23, 'India', 'Right Handed Bowler', 'yes', 'no', NULL, 45000000, 35),
(26, 2, 'Pragyan Ojha', 27, 'India', 'Left Handed Bowler', 'yes', 'no', NULL, 35000000, 35),
(27, 2, 'Ashish Nehra ', 34, 'India', 'Left Handed Bowler', 'yes', 'no', NULL, 40000000, 20),
(28, 2, 'Ishant Sharma', 25, 'India', 'Right Handed Bowler', 'yes', 'no', NULL, 50000000, 25),
(29, 2, 'Jason Holder', 22, 'West Indies', 'Right Handed Bowler', 'yes', 'no', NULL, 25000000, 35),
(30, 2, 'Ishwar Pandey', 24, 'India', 'Right Handed Bowler', 'yes', 'no', NULL, 35000000, 40),
(31, 2, 'Michelle Starc', 24, 'Australia', 'Left Handed Bowler', 'yes', 'no', NULL, 45000000, 45),
(32, 2, 'Bhuvaneshwar Kumar', 24, 'India', 'Right Handed Bowler', 'yes', 'no', NULL, 40000000, 40),
(33, 2, 'Nathan Coulter-Nile', 26, 'Australia', 'Right Handed Bowler', 'yes', 'no', NULL, 35000000, 35),
(34, 2, 'Beuran Hendricks', 23, 'South Africa', 'Left Handed Bowler', 'yes', 'no', NULL, 30000000, 35),
(35, 2, 'Tim Southee', 25, 'New Zealand', 'Right Handed Bowler', 'yes', 'no', NULL, 40000000, 40),
(36, 2, 'Mutthiah Muralidharan', 41, 'Sri Lanka', 'Right Handed Bowler', 'yes', 'no', NULL, 50000000, 50),
(37, 2, 'Ravi Rampaul', 29, 'West Indies', 'Right Handed Bowler', 'yes', 'no', NULL, 30000000, 35),
(38, 2, 'Parwinder Awana', 27, 'India', 'Right Handed Bowler', 'yes', 'no', NULL, 25000000, 30),
(39, 3, 'Dale Steyn', 30, 'South Africa', 'Right Handed Bowler', 'yes', 'no', NULL, 60000000, 60),
(40, 3, 'Lasith Malinga', 30, 'Sri Lanka', 'Right Handed Bowler', 'yes', 'no', NULL, 65000000, 55),
(41, 3, 'Sunil Narine', 25, 'West Indies', 'Right Handed Bowler', 'yes', 'no', NULL, 70000000, 60),
(42, 3, 'Zaheer Khan', 35, 'India', 'Left Handed Bowler', 'yes', 'no', NULL, 35000000, 35),
(43, 3, 'Kane Richardson', 23, 'Australia', 'Right Handed Bowler', 'yes', 'no', NULL, 40000000, 40),
(44, 3, 'Mitchelle Johnson', 32, 'Australia', 'Left Handed Bowler', 'yes', 'no', NULL, 65000000, 50),
(45, 3, 'Rishi Dhawan', 23, 'India', 'Right Handed Bowler', 'yes', 'no', NULL, 50000000, 45),
(46, 3, 'Harbhajan Singh', 33, 'India', 'Right Handed Bowler', 'yes', 'no', NULL, 55000000, 35),
(47, 3, 'Ravichandran Ashwin', 27, 'India', 'Right Handed Bowler', 'yes', 'no', NULL, 50000000, 25),
(48, 3, 'Brett Lee', 37, 'Australia', 'Right Handed Bowler', 'yes', 'no', NULL, 35000000, 35),
(49, 3, 'Karan Sharma', 26, 'India', 'Right Handed Bowler', 'yes', 'no', NULL, 55000000, 40),
(50, 3, 'Pat Cummins', 20, 'Australia', 'Right Handed Bowler', 'yes', 'no', NULL, 30000000, 25),
(51, 3, 'Mohit Sharma', 25, 'India', 'Right Handed Bowler', 'yes', 'no', NULL, 35000000, 30),
(52, 3, 'Amit Mishra', 31, 'India', 'Right Handed Bowler', 'yes', 'no', NULL, 45000000, 50),
(53, 3, 'Rahul Sharma', 27, 'India', 'Right Handed Bowler', 'yes', 'no', NULL, 35000000, 30),
(54, 3, 'Ashoke Dinda', 29, 'India', 'Right Handed Bowler', 'yes', 'no', NULL, 40000000, 25),
(55, 3, 'Varun Aaron', 24, 'India', 'Right Handed Bowler', 'yes', 'no', NULL, 45000000, 35),
(56, 3, 'Pravin Tambe', 42, 'India', 'Right Handed Bowler', 'yes', 'no', NULL, 55000000, 45),
(57, 3, 'Dhawal Kulkarni', 25, 'India', 'Right Handed Bowler', 'yes', 'no', NULL, 50000000, 25),
(58, 3, 'Jasprit Bumrah', 20, 'India', 'Right Handed Bowler', 'yes', 'no', NULL, 45000000, 45),
(59, 4, 'Mahendra Singh Dhoni', 32, 'India', 'Right Handed Batsman', 'yes', 'no', NULL, 75000000, 65),
(60, 4, 'AB DeVilliers', 29, 'South Africa', 'Right Handed Batsman', 'yes', 'no', NULL, 80000000, 65),
(61, 4, 'Aditya Tare', 26, 'India', 'Right Handed Batsman', 'yes', 'no', NULL, 60000000, 45),
(62, 4, 'Manvinder Singh Bisla', 29, 'India', 'Right Handed Batsman', 'yes', 'no', NULL, 45000000, 40),
(63, 4, 'Wriddhiman Saha', 29, 'India', 'Right Handed Batsman', 'yes', 'no', NULL, 35000000, 30),
(64, 4, 'Brendon McCullum', 32, 'New Zealand', 'Right Handed Batsman', 'yes', 'no', NULL, 65000000, 55),
(65, 4, 'Quinton De Kock', 21, 'South Africa', 'Left Handed Batsman', 'yes', 'no', NULL, 55000000, 50),
(66, 4, 'Sanju Samson', 19, 'India', 'Right Handed Batsman', 'yes', 'no', NULL, 60000000, 50),
(67, 4, 'Naman Ojha', 30, 'India', 'Right Handed Batsman', 'yes', 'no', NULL, 35000000, 25),
(68, 4, 'Brendan Taylor', 28, 'Zimbabwe', 'Right Handed Batsman', 'yes', 'no', NULL, 40000000, 35),
(69, 4, 'Parthiv Patel', 28, 'India', 'Left Handed Batsman', 'yes', 'no', NULL, 45000000, 35),
(70, 4, 'Dinesh Karthik', 28, 'India', 'Right Handed Batsman', 'yes', 'no', NULL, 60000000, 50),
(71, 5, 'Jacques Kallis', 38, 'South Africa', 'Right Handed Batsman', 'yes', 'no', NULL, 65000000, 55),
(72, 5, 'Yuvraj Singh', 32, 'India', 'Left Handed Batsman', 'yes', 'no', NULL, 70000000, 40),
(73, 5, 'Shane Watson', 32, 'Australia', 'Right Handed Batsman', 'yes', 'no', NULL, 75000000, 60),
(74, 5, 'Corey Anderson', 23, 'New Zealand', 'Left Handed Batsman', 'yes', 'no', NULL, 60000000, 55),
(75, 5, 'Keiron Pollard', 26, 'West Indies', 'Right Handed Batsman', 'yes', 'no', NULL, 70000000, 55),
(76, 5, 'Ravindra Jadeja', 25, 'India', 'Left Handed Batsman', 'yes', 'no', NULL, 65000000, 45),
(77, 5, 'Shakib Al Hasan', 26, 'Bangladesh', 'Left Handed Batsman', 'yes', 'no', NULL, 45000000, 65),
(78, 5, 'James Faulkner', 23, 'Australia', 'Right Handed Batsman', 'yes', 'no', NULL, 45000000, 60),
(79, 6, 'Chris Gayle', 34, 'West Indies', 'Left Handed Batsman', 'yes', 'no', NULL, 80000000, 60),
(80, 6, 'Michael Hussey', 38, 'Australia', 'Left Handed Batsman', 'yes', 'no', NULL, 75000000, 55),
(81, 6, 'Virat Kohli', 25, 'India', 'Right Handed Batsman', 'yes', 'no', NULL, 80000000, 60),
(82, 6, 'Gautam Gambhir', 32, 'India', 'Left Handed Batsman', 'yes', 'no', NULL, 60000000, 45),
(83, 6, 'Virendra Sehwag', 35, 'India', 'Right Handed Batsman', 'yes', 'no', NULL, 60000000, 30),
(84, 6, 'Suresh Raina', 27, 'India', 'Left Handed Batsman', 'yes', 'no', NULL, 65000000, 35),
(85, 6, 'Rohit Sharma', 26, 'India', 'Right Handed Batsman', 'yes', 'no', NULL, 75000000, 45),
(86, 6, 'Ajinkya Rahane', 25, 'India', 'Right Handed Batsman', 'yes', 'no', NULL, 45000000, 40),
(87, 6, 'Shikhar Dhawan', 28, 'India', 'Left Handed Batsman', 'yes', 'no', NULL, 70000000, 55),
(88, 6, 'Glenn Maxwell', 25, 'Australia', 'Right Handed Batsman', 'yes', 'no', NULL, 65000000, 50),
(89, 6, 'Steven Smith', 24, 'Australia', 'Right Handed Batsman', 'yes', 'no', NULL, 60000000, 55),
(90, 6, 'Kevin Pieterson', 33, 'England', 'Right Handed Batsman', 'yes', 'no', NULL, 70000000, 35),
(91, 6, 'David Warner', 27, 'Australia', 'Left Handed Batsman', 'yes', 'no', NULL, 75000000, 55),
(92, 6, 'Ross Taylor', 29, 'New Zealand', 'Right Handed Batsman', 'yes', 'no', NULL, 60000000, 35),
(93, 7, 'Sachin Tendulkar', 40, 'India', 'Right Handed Batsman', 'yes', 'no', NULL, 100000000, 60),
(94, 7, 'Sanath Jayasuriya', 44, 'Sri Lanka', 'Left Handed Batsman', 'yes', 'no', NULL, 75000000, 50),
(95, 7, 'Adam Gilchrist', 42, 'Australia', 'Wicketkeeper', 'yes', 'no', NULL, 90000000, 70),
(96, 7, 'Herchelle Gibbs', 39, 'South Africa', 'Right Handed Batsman', 'yes', 'no', NULL, 65000000, 55),
(97, 7, 'Brian Lara', 44, 'West Indies', 'Left Handed Batsman', 'yes', 'no', NULL, 80000000, 50),
(98, 7, 'Andrew Flintoff', 36, 'England', 'Allrounder', 'yes', 'no', NULL, 85000000, 60);

-- --------------------------------------------------------

--
-- Table structure for table `cricomania_pools`
--

CREATE TABLE IF NOT EXISTS `cricomania_pools` (
  `pool_id` int(11) NOT NULL,
  `pool_name` varchar(128) NOT NULL,
  PRIMARY KEY (`pool_id`),
  UNIQUE KEY `pool_name` (`pool_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `cricomania_pools`
--

INSERT INTO `cricomania_pools` (`pool_id`, `pool_name`) VALUES
(5, 'All-Rounders-1'),
(1, 'Batsmen-1'),
(6, 'Batsmen-2'),
(2, 'Bowlers-1'),
(3, 'Bowlers-2'),
(7, 'Legends-1'),
(4, 'Wicketkeepers-1');

-- --------------------------------------------------------

--
-- Table structure for table `cricomania_pool_limit`
--

CREATE TABLE IF NOT EXISTS `cricomania_pool_limit` (
  `player_type` varchar(20) NOT NULL,
  `limit` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `cricomania_pool_limit`
--

INSERT INTO `cricomania_pool_limit` (`player_type`, `limit`) VALUES
('Bowlers', 2),
('Batsmen', 2),
('Wicketkeepers', 1),
('Allrounders', 1),
('Legends', 1);

-- --------------------------------------------------------

--
-- Table structure for table `cricomania_stats_international`
--

CREATE TABLE IF NOT EXISTS `cricomania_stats_international` (
  `player_id` int(11) NOT NULL,
  `num_50s` varchar(11) NOT NULL,
  `num_100s` varchar(11) NOT NULL,
  `num_runs` varchar(11) NOT NULL,
  `num_matches` varchar(11) NOT NULL,
  `batting_avg` varchar(11) NOT NULL,
  `str_rate` varchar(10) NOT NULL,
  `wickets` varchar(10) NOT NULL,
  `5wi` varchar(10) NOT NULL,
  `econ` varchar(10) NOT NULL,
  `catches` varchar(10) NOT NULL,
  `stumpings` varchar(10) NOT NULL,
  PRIMARY KEY (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='international stats of players';

--
-- Dumping data for table `cricomania_stats_international`
--

INSERT INTO `cricomania_stats_international` (`player_id`, `num_50s`, `num_100s`, `num_runs`, `num_matches`, `batting_avg`, `str_rate`, `wickets`, `5wi`, `econ`, `catches`, `stumpings`) VALUES
(1, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(2, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(3, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(4, '12', '2', '1647', '39', '53.12', '91.39', '0', '0', '0', '23', 'NA'),
(5, '0', '0', '13', '2', '6.5', '46.42', 'NA', 'NA', 'NA', '0', 'NA'),
(6, '5', '0', '786', '38', '27.1', '91.12', 'NA', 'NA', 'NA', '15', 'NA'),
(7, '1', '1', '251', '8', '35.85', '77.46', '5', '0', '6.85', '3', 'NA'),
(8, '0', '0', '49', '3', '49', '87.5', 'NA', 'NA', 'NA', 'NA', 'NA'),
(9, '2', '3', '772', '23', '36', '92.34', '2', '0', '3.9', '12', 'NA'),
(10, '3', '1', '575', '25', '30.26', '87.51', '1', '0', '4.63', '16', 'NA'),
(11, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(12, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(13, '9', '5', '1541', '33', '67', '87.7', '55', '0', '5.03', '13', 'NA'),
(14, '18', '3', '3260', '115', '39.27', '83.58', '35', '0', '4.92', '46', 'NA'),
(15, '5', '0', '1102', '87', '16.69', '93.78', '59', '1', '4.93', '27', 'NA'),
(16, '0', '0', '253', '14', '19.46', '66.4', '0', '0', '0', '8', 'NA'),
(17, '10', '3', '1667', '45', '39.69', '77.03', 'NA', 'NA', 'NA', '9', 'NA'),
(18, '7', '0', '1102', '47', '27.55', '83.54', '2', '0', '5.68', '27', 'NA'),
(19, '0', '0', '120', '30', '12', '78.94', '34', '0', '5.57', '11', 'NA'),
(20, '0', '0', '2919', '73', '23.54', '29.5', '124', '1', '4.78', '19', 'NA'),
(21, '0', '0', '0', '2', '0', '0', '1', '0', '5.22', '0', 'NA'),
(22, '0', '0', '86', '31', '9.5', '58.9', '38', '0', '5.94', '6', 'NA'),
(23, '0', '0', '27', '28', '13.5', '57.44', '30', '6.07', '0', '5', 'NA'),
(24, '0', '0', '0', '7', '26.12', '0', '8', '0', '4.01', '0', 'NA'),
(25, '0', '0', '1218', '25', '29.7', '30.9', '41', '0', '5.76', '7', 'NA'),
(26, '0', '0', '46', '18', '23', '41.07', '21', '0', '4.46', '7', 'NA'),
(27, '0', '0', '141', '120', '31.72', '57.31', '157', '2', '5,19', '18', 'NA'),
(28, '0', '0', '72', '72', '31.44', '36', '102', '0', '5.72', '16', 'NA'),
(29, '0', '0', '68', '15', '11.33', '66.66', '22', '0', '5.37', '4', 'NA'),
(30, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(31, '1', '0', '122', '19', '40.66', '100.82', '37', '3', '5.05', '4', 'NA'),
(32, '0', '0', '1214', '31', '35.7', '45.1', '34', '0', '4.74', '11', 'NA'),
(33, '0', '0', '363', '7', '33', '37.6', '11', '0', '5.26', '1', 'NA'),
(34, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(35, '0', '0', '313', '78', '10.79', '90.72', '105', '1', '5.2', '19', 'NA'),
(36, '0', '0', '674', '350', '6.8', '77.56', '534', '10', '3.93', '130', 'NA'),
(37, '1', '0', '3020', '82', '29.6', '34.7', '102', '2', '5.1', '12', 'NA'),
(38, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(39, '0', '0', '3136', '79', '25.49', '31.9', '123', '3', '4.79', '21', 'NA'),
(40, '1', '0', '6608', '160', '27.3', '31.7', '242', '5', '5.15', '21', 'NA'),
(41, '0', '0', '1748', '46', '26.89', '38.1', '65', '1', '4.22', '9', 'NA'),
(42, '0', '0', '8301', '200', '29.43', '35.8', '282', '1', '4.93', '43', 'NA'),
(43, '0', '0', '0', '1', '0', '0', '1', '0', '2.5', '0', 'NA'),
(44, '2', '0', '5384', '136', '25.88', '31.9', '208', '3', '4.85', '29', 'NA'),
(45, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(46, '0', '0', '8651', '229', '33.4', '46.5', '259', '3', '4.3', '69', 'NA'),
(47, '1', '0', '3322', '75', '34.24', '41.5', '97', '0', '4.95', '19', 'NA'),
(48, '3', '0', '8877', '221', '23.36', '29.4', '380', '9', '4.76', '54', 'NA'),
(49, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(50, '0', '0', '21', '5', '21', '95.45', '7', '0', '5.94', '0', 'NA'),
(51, '0', '0', '0', '5', 'NA', 'NA', '4', '0', '5.77', '1', 'NA'),
(52, '0', '0', '14', '21', '23.29', '56', '37', '1', '4.66', '3', 'NA'),
(53, '0', '0', '1', '4', '1', '50', '6', '0', '5.15', '1', 'NA'),
(54, '0', '0', '21', '13', '4.2', '58.33', '12', '0', '6.18', '1', 'NA'),
(55, '0', '0', '8', '7', '8', '53.33', '10', '0', '6.21', '0', 'NA'),
(56, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(57, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(58, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(59, '54', '9', '8046', '243', '53.28', '89.24', '1', '0', '5.16', '224', '80'),
(60, '36', '16', '6331', '159', '49.46', '93.84', '0', '0', '11', '134', '3'),
(61, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(62, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(63, '0', '0', '4', '3', '4', '80', '', 'NA', 'NA', '6', '0'),
(64, '26', '4', '5172', '229', '30.24', '90.8', 'NA', 'NA', 'NA', '246', '15'),
(65, '0', '100', '741', '16', '46.31', '86.56', 'NA', 'NA', 'NA', '25', '0'),
(66, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'Na', 'NA', 'NA'),
(67, '0', '0', '1', '1', '1', '14.28', 'NA', 'NA', 'NA', '0', '0'),
(68, '27', '6', '4414', '146', '33.69', '72.71', '', 'NA', 'NA', '86', '19'),
(69, '4', '0', '736', '38', '23.74', '76.5', 'NA', 'NA', 'NA', '30', '9'),
(70, '7', '0', '1263', '67', '28.06', '73.64', 'NA', 'NA', 'NA', '46', '6'),
(71, '86', '17', '11574', '325', '44.86', '72.94', '273', '5', '4.84', '129', 'NA'),
(72, '51', '13', '8329', '293', '36.67', '87.24', '111', '1', '38.18', '93', 'NA'),
(73, '30', '9', '5256', '173', '41.06', '90.2', '163', '0', '4.87', '60', 'NA'),
(74, '1', '1', '424', '12', '53', '159.39', '19', '1', '6.09', '1', 'NA'),
(75, '6', '3', '1869', '85', '24.92', '93.54', '44', '0', '5.55', '45', 'NA'),
(76, '8', '0', '1467', '97', '31.89', '82.36', '113', '1', '4.75', '34', 'NA'),
(77, '25', '5', '3688', '129', '35.12', '78.18', '161', '0', '4.31', '36', 'NA'),
(78, '3', '1', '506', '24', '46', '116.58', '36', '0', '5.79', '5', 'NA'),
(79, '45', '21', '8743', '255', '37.52', '84.22', '157', '1', '4.74', '108', 'NA'),
(80, '39', '3', '5442', '185', '48.15', '87.16', '2', '5.87', '0', '105', 'NA'),
(81, '30', '18', '5445', '130', '51.85', '89.48', '4', '0', '6.05', '60', 'NA'),
(82, '34', '11', '5238', '147', '39.68', '85.25', 'NA', 'NA', 'NA', '36', 'NA'),
(83, '38', '15', '8273', '251', '35.05', '104.33', '96', '0', '5.26', '93', 'NA'),
(84, '29', '3', '4596', '189', '35.33', '91.35', '25', '0', '5.07', '80', 'NA'),
(85, '21', '4', '3319', '119', '35.68', '78.64', '8', '0', '5.04', '39', 'NA'),
(86, '3', '0', '513', '23', '22.3', '70.66', '0', '0', '0', '11', 'NA'),
(87, '5', '5', '1312', '35', '41', '91.68', 'NA', 'NA', 'NA', '14', 'NA'),
(88, '6', '0', '621', '24', '34.5', '124.2', '11', '0', '5.42', '9', 'NA'),
(89, '0', '0', '399', '34', '21', '87.88', '22', '0', '5.2', '15', 'NA'),
(90, '25', '9', '4440', '136', '40.73', '86.58', '7', '5.55', '0', '40', 'NA'),
(91, '8', '2', '1287', '42', '31.39', '82.39', 'NA', 'NA', 'NA', '11', 'NA'),
(92, '26', '10', '4328', '137', '40.07', '82.39', 'NA', 'NA', 'NA', '91', 'NA'),
(93, '96', '49', '18426', '463', '44.83', '86.23', '154', '2', '5.1', '140', 'NA'),
(94, '68', '28', '13430', '445', '32.36', '91.2', '323', '4.78', '4', '123', 'NA'),
(95, '55', '16', '96.19', '287', '35.89', '96.94', 'NA', 'NA', 'NA', '417', '55'),
(96, '37', '21', '8094', '248', '36.13', '83.26', 'NA', 'NA', 'NA', '108', 'NA'),
(97, '63', '19', '10405', '299', '40.48', '79.51', '4', '7.46', '0', '120', 'NA'),
(98, '18', '3', '3394', '141', '32.01', '88.82', '169', '4.39', '2', '47', 'NA');

-- --------------------------------------------------------

--
-- Table structure for table `cricomania_stats_ipl`
--

CREATE TABLE IF NOT EXISTS `cricomania_stats_ipl` (
  `player_id` int(11) NOT NULL,
  `num_50s` varchar(11) NOT NULL,
  `num_100s` varchar(11) NOT NULL,
  `num_runs` varchar(11) NOT NULL,
  `num_matches` varchar(11) NOT NULL,
  `batting_avg` varchar(11) NOT NULL,
  `str_rate` varchar(11) NOT NULL,
  `wickets` varchar(11) NOT NULL,
  `5wi` varchar(11) NOT NULL,
  `econ` varchar(11) NOT NULL,
  `catches` varchar(11) NOT NULL,
  `stumpings` varchar(11) NOT NULL,
  PRIMARY KEY (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ipl stats of players';

--
-- Dumping data for table `cricomania_stats_ipl`
--

INSERT INTO `cricomania_stats_ipl` (`player_id`, `num_50s`, `num_100s`, `num_runs`, `num_matches`, `batting_avg`, `str_rate`, `wickets`, `5wi`, `econ`, `catches`, `stumpings`) VALUES
(1, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(2, '1', '0', '246', '23', '18.92', '121.18', '0', '0', '0', '0', 'NA'),
(3, '1', '0', '433', '29', '18.82', '127.35', '0', '0', '0', '15', 'NA'),
(4, '0', '0', '63', '4', '21', '95.45', '0', '0', '0', '0', 'NA'),
(5, '1', '0', '265', '24', '18.92', '99.25', '0', '0', '0', '0', 'NA'),
(6, '8', '0', '1972', '91', '27', '124.02', '0', '0', '0', '48', '10'),
(7, '4', '0', '1115', '65', '28.58', '112.06', '1', '0', '11.25', '33', 'NA'),
(8, '4', '0', '579', '23', '25.17', '127.53', '1', '0', '9.34', '6', 'NA'),
(9, '6', '0', '1299', '62', '33.3', '124.18', '17', '0', '7.76', '20', 'NA'),
(10, '3', '1', '945', '48', '24.86', '113.99', '0', '0', '0', '25', 'NA'),
(11, '0', '0', '196', '13', '15.07', '96.07', '0', '0', '0', '5', 'NA'),
(12, '1', '0', '213', '13', '30.42', '135.66', '1', '0', '7.85', '4', 'NA'),
(13, '1', '0', '213', '13', '30.42', '135.66', '1', '0', '7.85', '4', 'NA'),
(14, '8', '0', '978', '39', '36.22', '119.26', '10', '0', '7.11', '15', 'NA'),
(15, '5', '0', '575', '20', '33.82', '130.97', '4', '0', '8.55', '9', 'NA'),
(16, '7', '2', '1600', '64', '26.66', '127.89', '0', '0', '0', '32', 'NA'),
(17, '16', '1', '1903', '50', '44.25', '133.26', '0', '0', '0', '21', 'NA'),
(18, '3', '0', '398', '13', '33.16', '130.92', '0', '0', '16', '4', 'NA'),
(19, '0', '0', '35', '64', '5', '76.08', '64', '1', '7.94', '13', 'NA'),
(20, '0', '0', '1219', '41', '27.08', '21.33', '45', '0', '7.61', '4', 'NA'),
(21, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(22, '0', '0', '287', '77', '11.97', '114.34', '84', '0', '8.22', '29', 'NA'),
(23, '0', '0', '44', '47', '11', '81.48', '43', '0', '8.16', '12', 'NA'),
(24, '0', '0', '2', '24', '27.69', '100', '23', '1', '7.92', '4', 'NA'),
(25, '0', '0', '230', '11', '38.33', '26', '6', '0', '8.84', '4', 'NA'),
(26, '0', '0', '13', '79', '1.18', '35.13', '85', '0', '7.18', '14', 'NA'),
(27, '0', '0', '39', '54', '25.55', '72.22', '59', '0', '7.77', '17', 'NA'),
(28, '0', '0', '37', '59', '31.61', '80.43', '52', '1', '7.65', '14', 'NA'),
(29, '0', '0', '0', '6', 'NA', 'NA', '2', '0', '8.4', '3', 'NA'),
(30, '0', '0', '0', '2', '50', '0', '1', '0', '12.5', '0', 'NA'),
(31, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(32, '0', '0', '719', '31', '29.95', '26.75', '24', '0', '6.71', '4', 'NA'),
(33, '0', '0', '29', '1', '29', '24', '1', '0', '7.25', '1', 'NA'),
(34, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(35, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(36, '0', '0', '20', '61', '3.33', '66.66', '60', '0', '6.6', '15', 'NA'),
(37, '0', '0', '261', '10', '20.07', '17.38', '13', '0', '6.92', '4', 'NA'),
(38, '0', '0', '5', '26', '2.5', '35.71', '32', '0', '7.76', '4', 'NA'),
(39, '0', '0', '3136', '79', '25.49', '31.9', '123', '3', '4.79', '21', 'NA'),
(40, '0', '0', '1849', '73', '17.95', '16.44', '103', '1', '6.54', '17', 'NA'),
(41, '0', '0', '674', '31', '14.65', '16.06', '46', '1', '5.47', '7', 'NA'),
(42, '0', '0', '1830', '64', '26.14', '20.25', '70', '0', '7.74', '18', 'NA'),
(43, '0', '0', '34', '3', '50.5', '94.44', '2', '0', '8.53', '0', 'NA'),
(44, '0', '0', '459', '17', '19.12', '16', '24', '0', '7.17', '7', 'NA'),
(45, '0', '0', '21', '6', '7', '100', '2', '0', '54.5', '3', 'NA'),
(46, '0', '0', '1925', '82', '24.67', '21.88', '78', '1', '6.76', '28', 'NA'),
(47, '0', '0', '1516', '67', '23.68', '22.39', '64', '0', '6.34', '16', 'NA'),
(48, '0', '0', '1095', '38', '43.8', '35', '25', '0', '7.5', '10', 'NA'),
(49, '0', '0', '85', '13', '21.25', '98.93', '11', '0', '6.6', '4', 'NA'),
(50, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(51, '0', '0', '0', '15', '0', '0', '20', '0', '6.43', '2', 'NA'),
(52, '0', '0', '275', '76', '20.25', '98.92', '95', '1', '6.93', '13', 'NA'),
(53, '0', '0', '66', '42', '4.71', '88', '39', '0', '6.86', '11', 'NA'),
(54, '0', '0', '9', '13', '4.5', '81.81', '51', '0', '7.92', '3', 'NA'),
(55, '0', '0', '1', '12', '0.5', '12.5', '10', '0', '8.2', '1', 'NA'),
(56, '0', '0', '3', '3', '0', '50', '1', '0', '8', '0', 'NA'),
(57, '0', '0', '23', '33', '7.66', '54.76', '36', '0', '7.96', '6', 'NA'),
(58, '0', '0', '0', '2', '0', '0', '3', '0', '10', '0', 'NA'),
(59, '13', '0', '2244', '96', '38.03', '141.22', 'NA', 'NA', 'NA', '47', '19'),
(60, '10', '1', '1662', '74', '34.62', '134.9', 'NA', 'NA', 'NA', '41', '7'),
(61, '1', '0', '173', '12', '19.22', '144.16', '0', '0', '0', '1', '2'),
(62, '4', '0', '758', '36', '21.25', '114.32', '0', '0', '0', '19', '7'),
(63, '1', '0', '442', '47', '27.62', '127.74', '0', '0', '0', '15', '5'),
(64, '5', '1', '1239', '48', '27.53', '123.16', 'NA', 'NA', 'NA', '20', '7'),
(65, '0', '0', '6', '3', '2', '40', 'NA', 'NA', 'NA', '3', '1'),
(66, '1', '0', '206', '11', '25.75', '115.73', 'NA', 'NA', 'NA', '13', '0'),
(67, '5', '0', '969', '56', '22.09', '117.59', '0', '0', '0', '28', '8'),
(68, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(69, '4', '0', '1206', '67', '20.79', '109.14', 'NA', 'NA', 'NA', '27', '7'),
(70, '6', '0', '1741', '92', '25.23', '123.91', 'NA', 'NA', 'NA', '48', '17'),
(71, '16', '0', '2276', '90', '28.81', '107.74', '61', '7.85', '0', '29', 'NA'),
(72, '5', '0', '1475', '70', '24.86', '130.76', '29', '0', '7', '23', 'NA'),
(73, '11', '1', '1785', '55', '39.66', '145.29', '48', '0', '7.16', '41', 'NA'),
(74, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(75, '5', '0', '1059', '62', '25.82', '147.08', '51', '0', '8.27', '34', 'NA'),
(76, '0', '0', '1105', '78', '23.51', '125.14', '39', '1', '7.36', '20', 'NA'),
(77, '0', '0', '120', '15', '15', '125', '23', '0', '6.66', '1', 'NA'),
(78, '0', '0', '71', '19', '11.83', '101.42', '32', '2', '6.94', '4', 'NA'),
(79, '16', '4', '2512', '59', '52.33', '160.2', '16', '0', '8.12', '14', 'NA'),
(80, '12', '1', '1691', '46', '44.5', '124.33', 'NA', 'NA', 'NA', '23', 'NA'),
(81, '14', '0', '2273', '93', '31.56', '124.13', '4', '0', '8.84', '35', 'NA'),
(82, '20', '0', '2471', '88', '32.09', '127.04', 'NA', 'NA', 'NA', '14', 'NA'),
(83, '15', '1', '2174', '79', '29.37', '160.32', 'NA', 'NA', 'NA', '29', 'NA'),
(84, '18', '1', '2802', '99', '35.02', '141.3', '21', '0', '7.12', '52', 'NA'),
(85, '18', '1', '2513', '97', '32.63', '129.66', '14', '0', '7.91', '49', 'NA'),
(86, '10', '1', '1316', '54', '31.33', '116.56', '1', '0', '5', '23', 'NA'),
(87, '16', '0', '1851', '68', '32.47', '122.42', '4', '8.25', '0', '29', 'NA'),
(88, '0', '0', '42', '5', '14', '110.52', '1', '0', '9', '1', 'NA'),
(89, '0', '0', '521', '22', '40.07', '130.57', '0', '0', '15', '14', 'NA'),
(90, '3', '1', '634', '21', '42.26', '141.2', '7', '7.41', '0', '9', 'NA'),
(91, '10', '2', '1435', '55', '28.7', '133.11', 'NA', 'NA', 'NA', '22', 'NA'),
(92, '3', '0', '958', '51', '25.89', '125.55', 'NA', 'NA', 'NA', '16', 'NA'),
(93, '13', '1', '2334', '78', '34.83', '119.81', '0', '0', '9.66', '23', 'NA'),
(94, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(95, '11', '2', '2069', '80', '27.22', '138.39', '0', '0', '0', '51', '16'),
(96, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(97, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA'),
(98, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA');

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

-- --------------------------------------------------------

--
-- Table structure for table `cricomania_team_auction_details`
--

CREATE TABLE IF NOT EXISTS `cricomania_team_auction_details` (
  `team_id` int(11) NOT NULL,
  `bought_player_id` int(11) NOT NULL,
  `bought_in_amt` bigint(20) NOT NULL,
  `bought_in_amt_words` varchar(1024) DEFAULT NULL,
  `bought_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`team_id`,`bought_player_id`),
  KEY `bought_player_id` (`bought_player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Which team has bought which players';

--
-- Constraints for dumped tables
--

--
-- Constraints for table `cricomania_current_pool_player`
--
ALTER TABLE `cricomania_current_pool_player`
  ADD CONSTRAINT `cricomania_current_pool_player_ibfk_1` FOREIGN KEY (`pool_id`) REFERENCES `cricomania_pools` (`pool_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `cricomania_current_pool_player_ibfk_2` FOREIGN KEY (`player_id`) REFERENCES `cricomania_players` (`player_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `cricomania_players`
--
ALTER TABLE `cricomania_players`
  ADD CONSTRAINT `fk_to_pool_id` FOREIGN KEY (`pool_id`) REFERENCES `cricomania_pools` (`pool_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `cricomania_stats_international`
--
ALTER TABLE `cricomania_stats_international`
  ADD CONSTRAINT `cricomania_stats_international_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `cricomania_players` (`player_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `cricomania_stats_ipl`
--
ALTER TABLE `cricomania_stats_ipl`
  ADD CONSTRAINT `cricomania_stats_ipl_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `cricomania_players` (`player_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `cricomania_team_auction_details`
--
ALTER TABLE `cricomania_team_auction_details`
  ADD CONSTRAINT `cricomania_team_auction_details_ibfk_1` FOREIGN KEY (`team_id`) REFERENCES `cricomania_teams` (`team_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `cricomania_team_auction_details_ibfk_2` FOREIGN KEY (`bought_player_id`) REFERENCES `cricomania_players` (`player_id`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
