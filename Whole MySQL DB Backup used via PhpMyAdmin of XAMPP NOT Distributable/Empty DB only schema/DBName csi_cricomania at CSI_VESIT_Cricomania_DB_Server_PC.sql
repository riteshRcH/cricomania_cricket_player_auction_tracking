-- phpMyAdmin SQL Dump
-- version 4.0.4.1
-- http://www.phpmyadmin.net
--
-- Host: Localhost
-- Generation Time: Mar 13, 2014 at 10:36 PM
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
-- Database: `csi_cricomania`
--
CREATE DATABASE IF NOT EXISTS `csi_cricomania` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `csi_cricomania`;

DELIMITER $$
--
-- Functions
--
DROP FUNCTION IF EXISTS `calcAmt`$$
CREATE DEFINER=`mihirsathe`@`%` FUNCTION `calcAmt`(r int(10)) RETURNS int(15)
    READS SQL DATA
    DETERMINISTIC
    SQL SECURITY INVOKER
BEGIN
DECLARE d int(5);
set d = 0;
set d = d+ (select i_currentPrice from t_company where v_name = 'Arcelor_Mittal')*(select Arcelor_Mittal from t_shares where i_id = r);
set d = d+ (select i_currentPrice from t_company where v_name = 'Tata_Steel')*(select          Tata_Steel from t_shares where i_id = r);
set d = d+ (select i_currentPrice from t_company where v_name = 'SAIL' ) * (select SAIL from t_shares where i_id = r);
set d = d+ (select i_currentPrice from t_company where v_name =         'Ispat'             ) * (select        Ispat          from t_shares where i_id = r);
set d = d+ (select i_currentPrice from t_company where v_name =         'Microsoft'             ) * (select           Microsoft       from t_shares where i_id = r) ;
set d = d+ (select i_currentPrice from t_company where v_name =         'Wipro'             ) * (select         Wipro         from t_shares where i_id = r) ;
set d = d+ (select i_currentPrice from t_company where v_name =         'ONGC'             ) * (select        ONGC          from t_shares where i_id = r) ;
set d = d+ (select i_currentPrice from t_company where v_name =         'Bharat_Petroleum'             ) * (select          Bharat_Petroleum        from t_shares where i_id = r) ;
set d = d+ (select i_currentPrice from t_company where v_name =         'Reliance_Petroleum'             ) * (select         Reliance_Petroleum         from t_shares where i_id = r) ;
set d = d+ (select i_currentPrice from t_company where v_name =         'Essar_Oil_Limited'             ) * (select          Essar_Oil_Limited        from t_shares where i_id = r) ;
set d = d+ (select i_currentPrice from t_company where v_name =         'Cipla'             ) * (select         Cipla         from t_shares where i_id = r) ;
set d = d+ (select i_currentPrice from t_company where v_name =         'Ranbaxy'             ) * (select         Ranbaxy         from t_shares where i_id = r) ;
set d = d+ (select i_currentPrice from t_company where v_name =         'GlaxoSmithKline'             ) * (select           GlaxoSmithKline       from t_shares where i_id = r) ;
set d = d+ (select i_currentPrice from t_company where v_name =         'Wockhardt'             ) * (select          Wockhardt        from t_shares where i_id = r) ;
set d = d+ (select i_currentPrice from t_company where v_name =         'Axis_Bank'             ) * (select           Axis_Bank       from t_shares where i_id = r) ;
set d = d+ (select i_currentPrice from t_company where v_name =         'Reliance_Telecom'             ) * (select         Reliance_Telecom         from t_shares where i_id = r) ;
set d = d+ (select i_currentPrice from t_company where v_name =         'Bharti_Airtel'             ) * (select         Bharti_Airtel         from t_shares where i_id = r) ;
set d = d+ (select i_currentPrice from t_company where v_name =         'BSNL'             ) * (select          BSNL        from t_shares where i_id = r) ;
set d = d+ (select i_currentPrice from t_company where v_name =         'MTNL'             ) * (select            MTNL      from t_shares where i_id = r) ;
set d = d+ (select i_currentPrice from t_company where v_name =         'Idea_Cellular'             ) * (select           Idea_Cellular       from t_shares where i_id = r) ;
set d = d+ (select i_currentPrice from t_company where v_name =         'Bombay_Dyeing'             ) * (select         Bombay_Dyeing         from t_shares where i_id = r) ;
set d = d+ (select i_currentPrice from t_company where v_name =         'Raymond'             ) * (select         Raymond         from t_shares where i_id = r) ;
set d = d+ (select i_currentPrice from t_company where v_name =         'Maruti_Suzuki'             ) * (select        Maruti_Suzuki          from t_shares where i_id = r) ;
set d = d+ (select i_currentPrice from t_company where v_name =         'Bajaj_Auto'             ) * (select            Bajaj_Auto      from t_shares where i_id = r) ;
set d = d+ (select i_currentPrice from t_company where v_name =         'Tata'             ) * (select         Tata         from t_shares where i_id = r) ;
set d = d+ (select i_currentPrice from t_company where v_name =         'GOLD'             ) * (select          GOLD        from t_shares where i_id = r) ;
set d = d+ (select i_currentPrice from t_company where v_name =         'SILVER'             ) * (select        SILVER          from t_shares where i_id = r) ;
set d = d+ (select i_currentPrice from t_company where v_name =         'DOLLAR'             ) * (select            DOLLAR      from t_shares where i_id = r) ;
set d = d+ (select i_currentPrice from t_company where v_name =         'POUNDS'             ) * (select            POUNDS      from t_shares where i_id = r) ;
set d = d+ (select i_currentPrice from t_company where v_name =         'EURO'             ) * (select         EURO         from t_shares where i_id = r);

RETURN d;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `cricomania_current_pool_player`
--
-- Creation: Feb 15, 2014 at 08:51 PM
--

DROP TABLE IF EXISTS `cricomania_current_pool_player`;
CREATE TABLE IF NOT EXISTS `cricomania_current_pool_player` (
  `pool_id` int(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  PRIMARY KEY (`pool_id`,`player_id`),
  KEY `player_id` (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Current pool and player in auction';

--
-- RELATIONS FOR TABLE `cricomania_current_pool_player`:
--   `pool_id`
--       `cricomania_pools` -> `pool_id`
--   `player_id`
--       `cricomania_players` -> `player_id`
--

-- --------------------------------------------------------

--
-- Table structure for table `cricomania_elimination_criteria`
--
-- Creation: Feb 15, 2014 at 01:40 PM
--

DROP TABLE IF EXISTS `cricomania_elimination_criteria`;
CREATE TABLE IF NOT EXISTS `cricomania_elimination_criteria` (
  `pool_ids` varchar(256) NOT NULL,
  `min_must_be_bought_count` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Per pool or group of pools min numPlayers perTeam shud be bought, else eliminate';

-- --------------------------------------------------------

--
-- Table structure for table `cricomania_players`
--
-- Creation: Feb 15, 2014 at 08:51 PM
--

DROP TABLE IF EXISTS `cricomania_players`;
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
-- RELATIONS FOR TABLE `cricomania_players`:
--   `pool_id`
--       `cricomania_pools` -> `pool_id`
--

-- --------------------------------------------------------

--
-- Table structure for table `cricomania_pools`
--
-- Creation: Feb 15, 2014 at 08:51 PM
--

DROP TABLE IF EXISTS `cricomania_pools`;
CREATE TABLE IF NOT EXISTS `cricomania_pools` (
  `pool_id` int(11) NOT NULL,
  `pool_name` varchar(128) NOT NULL,
  PRIMARY KEY (`pool_id`),
  UNIQUE KEY `pool_name` (`pool_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `cricomania_pools_bought_consideration_limit`
--
-- Creation: Feb 10, 2014 at 11:22 PM
--

DROP TABLE IF EXISTS `cricomania_pools_bought_consideration_limit`;
CREATE TABLE IF NOT EXISTS `cricomania_pools_bought_consideration_limit` (
  `pool_ids` varchar(256) NOT NULL,
  `num_players_bought_consideration_limit` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `cricomania_pool_limit`
--
-- Creation: Feb 15, 2014 at 08:51 PM
--

DROP TABLE IF EXISTS `cricomania_pool_limit`;
CREATE TABLE IF NOT EXISTS `cricomania_pool_limit` (
  `player_type` varchar(20) NOT NULL,
  `limit` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `cricomania_stats_international`
--
-- Creation: Feb 15, 2014 at 08:51 PM
--

DROP TABLE IF EXISTS `cricomania_stats_international`;
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
-- RELATIONS FOR TABLE `cricomania_stats_international`:
--   `player_id`
--       `cricomania_players` -> `player_id`
--

-- --------------------------------------------------------

--
-- Table structure for table `cricomania_stats_ipl`
--
-- Creation: Feb 15, 2014 at 08:51 PM
--

DROP TABLE IF EXISTS `cricomania_stats_ipl`;
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
-- RELATIONS FOR TABLE `cricomania_stats_ipl`:
--   `player_id`
--       `cricomania_players` -> `player_id`
--

-- --------------------------------------------------------

--
-- Table structure for table `cricomania_teams`
--
-- Creation: Feb 15, 2014 at 08:51 PM
--

DROP TABLE IF EXISTS `cricomania_teams`;
CREATE TABLE IF NOT EXISTS `cricomania_teams` (
  `team_id` int(11) NOT NULL,
  `team_balance` bigint(20) NOT NULL,
  `password_to_view_own_team_stats` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`team_id`),
  UNIQUE KEY `password_to_view_own_team_stats` (`password_to_view_own_team_stats`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Team details';

-- --------------------------------------------------------

--
-- Table structure for table `cricomania_team_auction_details`
--
-- Creation: Feb 15, 2014 at 08:51 PM
--

DROP TABLE IF EXISTS `cricomania_team_auction_details`;
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
-- RELATIONS FOR TABLE `cricomania_team_auction_details`:
--   `team_id`
--       `cricomania_teams` -> `team_id`
--   `bought_player_id`
--       `cricomania_players` -> `player_id`
--

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
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
